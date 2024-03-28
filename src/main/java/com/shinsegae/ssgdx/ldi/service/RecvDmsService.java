/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.ldi.service
 * @fileName : RecvDmsService.java
 * @author : q93m9k
 * @date : 2024.01.08
 * @description :
 * 
 * COPYRIGHT ©2024 SHINSEGAE. ALL RIGHTS RESERVED.
 * 
 * <pre>
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024.01.08       q93m9k              최초 생성
 * ===========================================================
 * </pre>
 */
package com.shinsegae.ssgdx.ldi.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.shinsegae.ssgdx.ldi.config.properties.LdiUrlProperties;
import com.shinsegae.ssgdx.ldi.model.IrtReqNJson;
import com.shinsegae.ssgdx.ldi.model.IrtResNJson;
import com.shinsegae.ssgdx.ldi.model.comirt.ComHeader;
import com.shinsegae.ssgdx.ldi.model.comirt.DmsComHeader;
import com.shinsegae.ssgdx.ldi.model.comirt.EncHeader;
import com.shinsegae.ssgdx.ldi.model.newirt.RetrieveCpnAmtDctkIssuPlndReqN;
import com.shinsegae.ssgdx.ldi.model.newirt.RetrieveCpnAmtDctkIssuPlndResN;
import com.shinsegae.ssgdx.ldi.model.newirt.RetrieveCpnAmtDctkUsgCfmtReqN;
import com.shinsegae.ssgdx.ldi.model.newirt.RetrieveCpnAmtDctkUsgCfmtResN;
import com.shinsegae.ssgdx.ldi.model.newirt.RetrieveCstTgetEvnCpnReqN;
import com.shinsegae.ssgdx.ldi.model.newirt.RetrieveCstTgetEvnCpnResN;
import com.shinsegae.ssgdx.ldi.model.newirt.RetrieveCstTgetEvnCpnResN.CpList.CpMdList;
import com.shinsegae.ssgdx.ldi.model.newirt.RetrieveEnrEvnInfoReqN;
import com.shinsegae.ssgdx.ldi.model.newirt.RetrieveEnrEvnInfoResN;
import com.shinsegae.ssgdx.ldi.model.newirt.RetrieveHwrtEnrAthReqN;
import com.shinsegae.ssgdx.ldi.model.newirt.RetrieveHwrtEnrAthResN;
import com.shinsegae.ssgdx.ldi.model.oldirt.RetrieveCpnAmtDctkIssuPlndReqO;
import com.shinsegae.ssgdx.ldi.model.oldirt.RetrieveCpnAmtDctkIssuPlndResO;
import com.shinsegae.ssgdx.ldi.model.oldirt.RetrieveCpnAmtDctkUsgCfmtReqO;
import com.shinsegae.ssgdx.ldi.model.oldirt.RetrieveCpnAmtDctkUsgCfmtResO;
import com.shinsegae.ssgdx.ldi.model.oldirt.RetrieveCstTgetEvnCpnReqO;
import com.shinsegae.ssgdx.ldi.model.oldirt.RetrieveCstTgetEvnCpnResO;
import com.shinsegae.ssgdx.ldi.model.oldirt.RetrieveEnrEvnInfoReqO;
import com.shinsegae.ssgdx.ldi.model.oldirt.RetrieveEnrEvnInfoResO;
import com.shinsegae.ssgdx.ldi.model.oldirt.RetrieveHwrtEnrAthReqO;
import com.shinsegae.ssgdx.ldi.model.oldirt.RetrieveHwrtEnrAthResO;
import com.shinsegae.ssgdx.ldi.util.Aes128CipherUtil;
import com.shinsegae.ssgdx.ldi.util.IEnumComIrt;
import com.shinsegae.ssgdx.ldi.util.IEnumComIrt.EnmIrtFieldType;
import com.shinsegae.ssgdx.ldi.util.IEnumComIrt.EnmIrtTypeWrap;
import com.shinsegae.ssgdx.ldi.util.IrtParseUtil;
import com.shinsegae.ssgdx.ldi.util.JsonUtil;
import com.shinsegae.ssgdx.ldi.util.SCSDaemon;
import com.shinsegae.ssgdx.ldi.util.SCSHttpClient;
import com.shinsegae.ssgdx.ldi.util.SocketUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * 
 * </pre>
 * 
 * @author : q93m9k
 * @version : 2024.01.08
 * @see :
 */
@Slf4j
@Getter
@Setter
@Scope("prototype")
@Service
public class RecvDmsService implements ISCSService {

	private final SqlSessionFactory mdsDbSqlSessionFactory;

	private final SqlSessionFactory dmsDbSqlSessionFactory;

	private final LdiUrlProperties ldiUrlProperties;

	private final SocketUtil socketUtil;

	private final SCSHttpClient httpClient;

	@Value("${ssgdx-opdms.client.socketRecvCharset: EUC-KR}")
	String socketRecvCharset;

	@Value("${ssgdx-opdms.client.socketSendCharset: EUC-KR}")
	String socketSendCharset;

	@Value("${ssgdx-opdms.client.httpRecvCharset: UTF-8}")
	String httpRecvCharset;

	@Value("${ssgdx-opdms.client.httpSendCharset: UTF-8}")
	String httpSendCharset;

	@Value("${ssgdx-opdms.client.connectTimeout: 3000}")
	int connectTimeout;

	@Value("${ssgdx-opdms.client.serviceTimeout: 10000}")
	int serviceTimeout;

	private String logMsg = "";

	private Map<String, Object> logMsgMap = new HashMap<>();

	public RecvDmsService(@Qualifier("mdsDbSqlSessionFactory") SqlSessionFactory mdsDbSqlSessionFactory,
			@Qualifier("dmsDbSqlSessionFactory") SqlSessionFactory dmsDbSqlSessionFactory,
			LdiUrlProperties ldiUrlProperties, SocketUtil socketUtil, SCSHttpClient httpClient) {
		this.mdsDbSqlSessionFactory = mdsDbSqlSessionFactory;
		this.dmsDbSqlSessionFactory = dmsDbSqlSessionFactory;
		this.ldiUrlProperties = ldiUrlProperties;
		this.socketUtil = socketUtil;
		this.httpClient = httpClient;
	}

	/**
	 * @param scsDaemon
	 * @param client
	 * @see com.shinsegae.ssgdx.ldi.service.ISCSService#business(com.shinsegae.ssgdx.ldi.util.SCSDaemon,
	 *      java.net.Socket)
	 */
	@Override
	public void business(SCSDaemon scsDaemon, Socket client) {
		// TODO Auto-generated method stub
		SqlSession ssMds = null;
		SqlSession ssDms = null;

		try {
			UUID uuid = UUID.randomUUID();

			MDC.put("guid", uuid.toString());

			log.info("ldi start...");

			// client 세션 유지로 변경
			// boolean bSession = true;
			// while (bSession) {
			int ret = -1;
			BufferedInputStream bis = null;
			BufferedOutputStream bos = null;
			int[] offSet = { 0 };
			EncHeader encHeaderReq = new EncHeader();
			DmsComHeader dmsComHeaderReq = new DmsComHeader();
			Aes128CipherUtil aes128CipherUtil = null;

			byte[] recvEncHeader = new byte[IEnumComIrt.DEF_ENC_HEADER_LEN];
			byte[] recvDmsComHeaderO = new byte[IEnumComIrt.DEF_DMS_COM_HEADER_LEN];
			byte[] recvEncData = null;
			byte[] recvIrtReqData = null;

			bis = new BufferedInputStream(client.getInputStream());
			bos = new BufferedOutputStream(client.getOutputStream());

			ssMds = mdsDbSqlSessionFactory.openSession();
			ssDms = dmsDbSqlSessionFactory.openSession();

			// [1] Enc Header 수신
			if (socketUtil.tcp_recv_b(bis, client, recvEncHeader, IEnumComIrt.DEF_ENC_HEADER_LEN) >= 0) {
				ret = 0;
			} else {
				// bSession = false;
				ret = -2;
				log.info("RecvDmsService -- Enc Header 수신 Error!!");
			}

			// [2] Enc Header Bytes --> Object 변경
			if (ret == 0 && (IrtParseUtil.irtMapToObj(recvEncHeader, offSet, encHeaderReq, socketRecvCharset) == 0)) {
				ret = 0;
			} else
				ret = -1;

			// [3] Enc Header->MSG_LEN 만큼 수신
			if (ret == 0) {
				int totLen = IrtParseUtil.convStrToInt(encHeaderReq.getTOTLEN());
				if (totLen > 0 && totLen > IEnumComIrt.DEF_ENC_HEADER_LEN) {
					int recvEncLen = totLen - IEnumComIrt.DEF_ENC_HEADER_LEN;

					recvEncData = new byte[recvEncLen];

					if ((ret = socketUtil.tcp_recv_b(bis, client, recvEncData, recvEncLen)) >= 0) {
						ret = 0;
					} else {
						// bSession = false;
						ret = -2;
						log.error("RecvDmsService -- MSG_LEN 만큼 수신 Error!!");
					}
				} else
					ret = -1;
			} else
				ret = -1;

			// [4] 암호화 전문 복호화
			if (ret == 0) {
				aes128CipherUtil = new Aes128CipherUtil(socketRecvCharset, encHeaderReq.getPOSNO().getBytes(),
						encHeaderReq.getTRANNO().getBytes(), encHeaderReq.getMSGVLI().getBytes());

				recvIrtReqData = aes128CipherUtil.decryptAes(recvEncData);

				// debug
				log.info("recvIrtReqData : [{}]", new String(recvIrtReqData, socketRecvCharset));

				// DMS Header Bytes Copy
				if (recvIrtReqData != null && recvIrtReqData.length > 0) {
					System.arraycopy(recvIrtReqData, 0, recvDmsComHeaderO, 0, IEnumComIrt.DEF_DMS_COM_HEADER_LEN);
					ret = 0;
				} else
					ret = -1;
			} else
				ret = -1;

			// Offset 초기화
			offSet[0] = 0;

			// [5] DMS Header Bytes --> Object 변경
			if (ret == 0
					&& (IrtParseUtil.irtMapToObj(recvDmsComHeaderO, offSet, dmsComHeaderReq, socketRecvCharset) == 0)) {
				ret = 0;
			} else
				ret = -1;

			// [6] 전문ID 별 처리
			if (ret == 0) {
				logMsgMap.put("saleDate", dmsComHeaderReq.getSALE_DATE());
				logMsgMap.put("storeNo", dmsComHeaderReq.getSTORE_CD());
				logMsgMap.put("posNo", dmsComHeaderReq.getPOS_NO());
				logMsgMap.put("tranNo", dmsComHeaderReq.getTRAN_NO());

				switch (dmsComHeaderReq.getMSG_ID()) {
				// DMS 수기 에누리 권한 조회
				case "DIRT0101":
					log.info("ldi -- DMS 수기 에누리 권한 조회");
					ret = msgIdDIRT0101(scsDaemon, client, bis, bos, encHeaderReq, dmsComHeaderReq, recvIrtReqData,
							offSet, ssMds, ssDms);
					break;
				// DMS 고객 대상행사 및 쿠폰조회
				case "DIRT0107":
					log.info("ldi -- DMS 고객 대상행사 및 쿠폰조회");
					ret = msgIdDIRT0107(scsDaemon, client, bis, bos, encHeaderReq, dmsComHeaderReq, recvIrtReqData,
							offSet, ssMds, ssDms);
					break;
				// DMS 에누리 행사 정보조회
				case "DIRT0103":
					log.info("ldi -- DMS 에누리 행사 정보조회");
					ret = msgIdDIRT0103(scsDaemon, client, bis, bos, encHeaderReq, dmsComHeaderReq, recvIrtReqData,
							offSet, ssMds, ssDms);
					break;
				// DMS 쿠폰 및 금액할인권 발급 예정 조회 8BIN
				case "DIRT0106":
					log.info("ldi -- DMS 쿠폰 및 금액할인권 발급 예정 조회 8BIN");
					ret = msgIdDIRT0106(scsDaemon, client, bis, bos, encHeaderReq, dmsComHeaderReq, recvIrtReqData,
							offSet, ssMds, ssDms);
					break;
				// DMS 쿠폰 및 금액할인권 사용 확정 조회
				case "DIRT0105":
					log.info("ldi -- DMS 쿠폰 및 금액할인권 사용 확정 조회");
					ret = msgIdDIRT0105(scsDaemon, client, bis, bos, encHeaderReq, dmsComHeaderReq, recvIrtReqData,
							offSet, ssMds, ssDms);
					break;
				default:
					ret = -1;
					break;
				}
			} else
				ret = -1;

			// socket 에러일때 종료
			if (ret == -2) {
				// bSession = false;
				log.info("ldi -- 소켓 에러로 연결 세션 종료!!");
			}
			// } // while
		} catch (Exception e) {
			log.error("ldi -- Exception. {}", e);
		} finally {
			// 응답 전문 전송이 정상 처리 되지 않은 모든 에러 발생시 응답 처리 (단 전송 타임아웃이 아닐 때)
			try {
				if(ssMds != null) {
					ssMds.rollback();
					ssMds.close();
				}
				if(ssDms != null) {
					ssDms.rollback();
					ssDms.close();
				}
				
				/*****************************************************************/
				MDC.clear();
				/*****************************************************************/
			} catch (Exception e) {
				log.error("ldi -- finally Exception. {}", e);
			}

			// 클라이언트 연결 총료 처리
			try {
				// 소켓 세션 리스트에서 현재 소켓 정보 제거
				scsDaemon.removeClientSocketList(client);

				scsDaemon.setCliCnt();
				if (client != null) {
					client.close();
				}
				log.info("ldi end... Connected Client Count: [{}]", scsDaemon.getCliCnt());
			} catch (Exception e) {
				log.error("ldi -- 클라이언트 연결 총료 처리 Exception. {}", e);
			}
		}
	}

	/**
	 * <pre>
	 * DMS 수기 에누리 권한 조회
	 * </pre>
	 *
	 * @param scsDaemon
	 * @param client
	 * @param startTime
	 * @param remainTimeOut
	 * @param bis
	 * @param bos
	 * @param encHeaderReq
	 * @param dmsComHeaderReq
	 * @param recvIrtReqData
	 * @param offSet
	 * @return
	 */
	public int msgIdDIRT0101(SCSDaemon scsDaemon, Socket client, BufferedInputStream bis, BufferedOutputStream bos,
			EncHeader encHeaderReq, DmsComHeader dmsComHeaderReq, byte[] recvIrtReqData, int[] offSet, SqlSession ssMds,
			SqlSession ssDms) {
		int ret = -1;
		Aes128CipherUtil aes128CipherUtil = null;

		// 구 전문 요청/응답
		RetrieveHwrtEnrAthReqO retrieveHwrtEnrAthReqO = null;
		RetrieveHwrtEnrAthResO retrieveHwrtEnrAthResO = null;

		// 신 전문 요청/응답
		RetrieveHwrtEnrAthReqN retrieveHwrtEnrAthReqN = null;
		RetrieveHwrtEnrAthResN retrieveHwrtEnrAthResN = null;

		// 구전문 Object --> Bytes 변환
		ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();

		try {
			// [1] 구 전문 --> 신 전문 변환
			// [1-1] 구 전문 요청 Bytes --> Object 변경
			retrieveHwrtEnrAthReqO = new RetrieveHwrtEnrAthReqO();
			if (IrtParseUtil.irtMapToObj(recvIrtReqData, offSet, retrieveHwrtEnrAthReqO, socketRecvCharset) == 0) {
				ret = 0;
			} else {
				log.error("DMS 수기 에누리 권한 조회 -- 구 전문 --> 신 전문 변환 오류!!");
				ret = -1;
			}

			// [1-2] 구 전문 --> 신 전문 변환
			retrieveHwrtEnrAthReqN = new RetrieveHwrtEnrAthReqN();
			if (ret == 0) {
				retrieveHwrtEnrAthReqN.setIrtType(EnmIrtTypeWrap.Irt_10.getIrtTypeN());
				retrieveHwrtEnrAthReqN.setCASHIER_ATTR(retrieveHwrtEnrAthReqO.getCASHIER_ATTR());
				retrieveHwrtEnrAthReqN.setCASHIER_NO(retrieveHwrtEnrAthReqO.getCASHIER_NO());
				retrieveHwrtEnrAthReqN.setAUTH_TYPE(retrieveHwrtEnrAthReqO.getAUTH_TYPE());
				retrieveHwrtEnrAthReqN.setORT_TRAN_DATE(retrieveHwrtEnrAthReqO.getORT_TRAN_DATE());
				retrieveHwrtEnrAthReqN.setORG_POS_NO(retrieveHwrtEnrAthReqO.getORG_POS_NO());
				retrieveHwrtEnrAthReqN.setORG_TRAN_NO(retrieveHwrtEnrAthReqO.getORG_TRAN_NO());
				retrieveHwrtEnrAthReqN.setORG_CASHIER_NO(retrieveHwrtEnrAthReqO.getORG_CASHIER_NO());
				retrieveHwrtEnrAthReqN.setFILLER(retrieveHwrtEnrAthReqO.getFILLER());

				ret = 0;
			} else
				ret = -1;

			// [2] 신 전문 API 전송
			if (ret == 0) {
				IrtReqNJson irtReqNJson = new IrtReqNJson();
				// DMS Header --> Com Header 생성
				makeComHeaderFromDmsHeader(dmsComHeaderReq, irtReqNJson.getCOMM_HEADER());
				irtReqNJson.setIRT_REQ(retrieveHwrtEnrAthReqN);

				String jsonIrt = JsonUtil.getMapper().writeValueAsString(irtReqNJson);

				// debug
				log.info("신 전문 API 전송 :\n{}",
						JsonUtil.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(irtReqNJson));

				logMsg = String.format("%s : 신 전문 API 전송\n%s", "[LDI][DIRT0101][수기 에누리 권한 조회]",
						JsonUtil.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(irtReqNJson));
				logMsgMap.put("logMsg", logMsg);
				insertTTRLOG_N(logMsgMap, ssMds, ssDms);

				Map<String, String> responseMap = new HashMap<String, String>();
				String severUrl = "", irtUrl = "";
				severUrl = ldiUrlProperties.getUrl().get("sever-url");
				irtUrl = ldiUrlProperties.getUrl().get("retrieveHwrtEnrAth");

				// 전송 및 응답 수신
				if (httpClient.sendPostData(severUrl + irtUrl, jsonIrt, responseMap, httpRecvCharset, httpSendCharset,
						connectTimeout, serviceTimeout) == 0) {

					IrtResNJson irtResNJson = JsonUtil.getMapper().readValue(responseMap.get("response"),
							IrtResNJson.class);

					if (irtResNJson != null) {
						// debug
						log.info("recv 차세대 응답 : \n{}\n",
								JsonUtil.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(irtResNJson));

						logMsg = String.format("%s : recv 차세대 응답\n%s", "[LDI][DIRT0101][수기 에누리 권한 조회]",
								JsonUtil.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(irtResNJson));
						logMsgMap.put("logMsg", logMsg);
						insertTTRLOG_N(logMsgMap, ssMds, ssDms);

						retrieveHwrtEnrAthResN = JsonUtil.getMapper().convertValue(irtResNJson.getIRT_RES(),
								RetrieveHwrtEnrAthResN.class);
						ret = 0;
					} else
						ret = -1;
				} else
					ret = -1;
			} else
				ret = -1;

			// [3] 신 전문 --> 구 전문 변환
			if (ret == 0) {
				int[] offSetRecv = { 0 };
				byte[] encHeaderBytes = null, dmsComHeaderBytes = null, retrieveHwrtEnrAthResOBytes = null,
						sendDmsHdBodyBytes = null, sendEncDmsHdBodyBytes = null, snedResBytes = null;
				EncHeader encHeaderRes = new EncHeader();
				DmsComHeader dmsComHeaderRes = new DmsComHeader();
				retrieveHwrtEnrAthResO = new RetrieveHwrtEnrAthResO();

				log.info("차세대 응답코드 : {}", retrieveHwrtEnrAthResN.getRES_CODE());
				// 정상일때만 데이터 들어온다
				if ("0000".equals(retrieveHwrtEnrAthResN.getRES_CODE())) {

					// [3-1] 응답 전문 설정
					// 권한여부
					retrieveHwrtEnrAthResO.setAUTH_YN(retrieveHwrtEnrAthResN.getAUTH_YN());
					// 점포코드
					retrieveHwrtEnrAthResO.setMANUAL_STR_CD(IrtParseUtil.getLpadFillZeroStringForTgFld(
							retrieveHwrtEnrAthResO, "MANUAL_STR_CD", retrieveHwrtEnrAthResN.getMANUAL_STR_CD()));
					// 행사년월
					retrieveHwrtEnrAthResO.setMANUAL_YM(IrtParseUtil.getRpadFillSpaceStringForTgFld(
							retrieveHwrtEnrAthResO, "MANUAL_YM", retrieveHwrtEnrAthResN.getMANUAL_YM()));

					// 행사번호 (9 -> 6) 행사번호값은 ASIS-TOBE 6자리로 동일
					String sMANUAL_EVT_NO = retrieveHwrtEnrAthResN.getMANUAL_EVT_NO();
					if (sMANUAL_EVT_NO != null) {
						sMANUAL_EVT_NO = sMANUAL_EVT_NO.trim();
						if (sMANUAL_EVT_NO.length() == 9) {
							sMANUAL_EVT_NO = sMANUAL_EVT_NO.substring(3, 9);
						} else
							sMANUAL_EVT_NO = "";
					} else
						sMANUAL_EVT_NO = "";

					retrieveHwrtEnrAthResO.setMANUAL_EVT_NO(IrtParseUtil
							.getRpadFillSpaceStringForTgFld(retrieveHwrtEnrAthResO, "MANUAL_EVT_NO", sMANUAL_EVT_NO));

					log.info("행사번호 : [{}]", retrieveHwrtEnrAthResO.getMANUAL_EVT_NO());

					// 행사종류코드
					retrieveHwrtEnrAthResO.setMANUAL_EVT_CD(IrtParseUtil.getRpadFillSpaceStringForTgFld(
							retrieveHwrtEnrAthResO, "MANUAL_EVT_CD", retrieveHwrtEnrAthResN.getMANUAL_EVT_CD()));

					// 정상에누리율
					retrieveHwrtEnrAthResO.setNOR_ENU_RATE(IrtParseUtil.getLpadFillZeroStringForTgFld(
							retrieveHwrtEnrAthResO, "NOR_ENU_RATE", retrieveHwrtEnrAthResN.getNOR_ENU_RATE()));

					// 행사에누리율
					retrieveHwrtEnrAthResO.setEVT_ENU_RATE(IrtParseUtil.getLpadFillZeroStringForTgFld(
							retrieveHwrtEnrAthResO, "NOR_ENU_RATE", retrieveHwrtEnrAthResN.getEVT_ENU_RATE()));

					// 원거래 수기에누리 금액 (15 -> 10)
					retrieveHwrtEnrAthResO.setORG_ENURI_AMT(IrtParseUtil.getLpadFillZeroStringForTgFld(
							retrieveHwrtEnrAthResO, "ORG_ENURI_AMT", retrieveHwrtEnrAthResN.getORG_ENURI_AMT()));

					// 권한적용범위FLAG
					// 2 매장이면 3 MD로 내린다
					String cancelEnuriFlag = "";
					cancelEnuriFlag = retrieveHwrtEnrAthResN.getENURI_FLAG();
					if ("2".equals(cancelEnuriFlag)) {
						cancelEnuriFlag = "3";
						log.info("권한적용범위FLAG : 2:매장이면 --> 3:MD로 내린다");
					}
					retrieveHwrtEnrAthResO.setCANCEL_ENURI_FLAG(IrtParseUtil.getLpadFillSpaceStringForTgFld(
							retrieveHwrtEnrAthResO, "CANCEL_ENURI_FLAG", cancelEnuriFlag));

					// 권한적용범위MD/CLASS (10 -> 6)
					// 매장이면 매장에 매핑된 MD코드값을 내린다 (해당 매장에 유효한 MD 한개, MT_SHOP_MD_M)
					String mdCd = retrieveHwrtEnrAthResN.getAUTH_SHOP();
					if ("3".equals(cancelEnuriFlag)) {
						String strCd = retrieveHwrtEnrAthResN.getMANUAL_STR_CD();
						if (strCd != null)
							strCd = strCd.trim();
						else
							strCd = "";
						String shopCd = retrieveHwrtEnrAthResN.getAUTH_SHOP();
						if (shopCd != null)
							shopCd = shopCd.trim();
						else
							shopCd = "";

						Map<String, Object> paramMap = new HashMap<>();
						paramMap.put("strCd", strCd);
						paramMap.put("shopCd", shopCd);
						mdCd = selectMdCd(paramMap, ssMds, ssDms);
						log.info("권한적용범위MD/CLASS : selectMdCd : [{}]", mdCd);
					}
					retrieveHwrtEnrAthResO.setAUTH_MD_CLASS(
							IrtParseUtil.getRpadFillSpaceStringForTgFld(retrieveHwrtEnrAthResO, "AUTH_MD_CLASS", mdCd));

					// 예비
					String filler = "";
					if (retrieveHwrtEnrAthResN.getFILLER() != null) {
						filler = IrtParseUtil.getFixLengthByteValueConvStr(
								retrieveHwrtEnrAthResN.getFILLER().trim().getBytes(socketSendCharset),
								IrtParseUtil.getFieldSize(retrieveHwrtEnrAthResO, "FILLER"), EnmIrtFieldType.LSTR,
								socketSendCharset);
					} else
						filler = IrtParseUtil.getRpadFillSpaceStringForTgFld(retrieveHwrtEnrAthResO, "FILLER", "");

					retrieveHwrtEnrAthResO.setFILLER(filler);
				}

				// [3-2] DMS Header 설정
				IrtParseUtil.copySrcDmsHdToTgDmsHd(dmsComHeaderReq, dmsComHeaderRes);
				// [3-2-1] DMS Header : 전문길이 설정
				int dmsBodyLen = 0;
				int dmsHdMsgLen = 0;
				String dmsHdMsgLenLpad = "";

				if ("0000".equals(retrieveHwrtEnrAthResN.getRES_CODE())) {
					dmsBodyLen = IrtParseUtil.getIrtTotLen(retrieveHwrtEnrAthResO);
					dmsHdMsgLen = dmsBodyLen + IEnumComIrt.DEF_DMS_COM_HEADER_LEN;
					dmsHdMsgLenLpad = IrtParseUtil.getLpadFillZeroStringForTgFld(dmsComHeaderRes, "MSG_LEN",
							dmsHdMsgLen);
					dmsComHeaderRes.setMSG_LEN(dmsHdMsgLenLpad);

					// [3-2-2] DMS Header : 응답코드 설정
					dmsComHeaderRes.setRES_CODE(retrieveHwrtEnrAthResN.getRES_CODE());
					// [3-2-3] DMS Header : 응답 메시지 설정
					String resMsg = "";
					if (retrieveHwrtEnrAthResN.getRES_MSG() != null) {
						resMsg = IrtParseUtil.getFixLengthByteValueConvStr(
								retrieveHwrtEnrAthResN.getRES_MSG().trim().getBytes(socketSendCharset),
								IrtParseUtil.getFieldSize(dmsComHeaderRes, "RES_MSG"), EnmIrtFieldType.LSTR,
								socketSendCharset);
					} else
						resMsg = IrtParseUtil.getRpadFillSpaceStringForTgFld(dmsComHeaderRes, "RES_MSG", "");

					dmsComHeaderRes.setRES_MSG(resMsg);

					// [3-3] 응답 데이터 bytes 변환
					// [3-3-1] DMS Header bytes 변환
					dmsComHeaderBytes = new byte[IEnumComIrt.DEF_DMS_COM_HEADER_LEN];
					ret = IrtParseUtil.irtMapToBytes(dmsComHeaderBytes, offSetRecv, dmsComHeaderRes, socketSendCharset);

					// [3-3-2] DMS 수기 에누리 권한 조회 응답 Body bytes 변환
					if (ret == 0) {
						offSetRecv[0] = 0;
						retrieveHwrtEnrAthResOBytes = new byte[dmsBodyLen];

						if ((ret = IrtParseUtil.irtMapToBytes(retrieveHwrtEnrAthResOBytes, offSetRecv,
								retrieveHwrtEnrAthResO, socketSendCharset)) == 0) {
							if ((sendDmsHdBodyBytes = IrtParseUtil.byteArraysConcat(dmsComHeaderBytes,
									retrieveHwrtEnrAthResOBytes)) == null)
								ret = -1;
						}
					} else
						ret = -1;
				} else {
					dmsBodyLen = 0;
					dmsHdMsgLen = dmsBodyLen + IEnumComIrt.DEF_DMS_COM_HEADER_LEN;
					dmsHdMsgLenLpad = IrtParseUtil.getLpadFillZeroStringForTgFld(dmsComHeaderRes, "MSG_LEN",
							dmsHdMsgLen);
					dmsComHeaderRes.setMSG_LEN(dmsHdMsgLenLpad);

					// [3-2-2] DMS Header : 응답코드 설정
					dmsComHeaderRes.setRES_CODE(retrieveHwrtEnrAthResN.getRES_CODE());
					// [3-2-3] DMS Header : 응답 메시지 설정
					String resMsg = "";
					if (retrieveHwrtEnrAthResN.getRES_MSG() != null) {
						resMsg = IrtParseUtil.getFixLengthByteValueConvStr(
								retrieveHwrtEnrAthResN.getRES_MSG().trim().getBytes(socketSendCharset),
								IrtParseUtil.getFieldSize(dmsComHeaderRes, "RES_MSG"), EnmIrtFieldType.LSTR,
								socketSendCharset);
					} else
						resMsg = IrtParseUtil.getRpadFillSpaceStringForTgFld(dmsComHeaderRes, "RES_MSG", "");

					dmsComHeaderRes.setRES_MSG(resMsg);

					// [3-3] 응답 데이터 bytes 변환
					// [3-3-1] DMS Header bytes 변환
					dmsComHeaderBytes = new byte[IEnumComIrt.DEF_DMS_COM_HEADER_LEN];
					ret = IrtParseUtil.irtMapToBytes(dmsComHeaderBytes, offSetRecv, dmsComHeaderRes, socketSendCharset);

					// [3-3-2] DMS 수기 에누리 권한 조회 응답 Body bytes 변환
					if (ret == 0) {
						if ((sendDmsHdBodyBytes = IrtParseUtil.byteArraysConcat(dmsComHeaderBytes)) == null)
							ret = -1;
					} else
						ret = -1;
				}

				// [3-4] 응답 데이터 Body 암호화
				if (ret == 0) {
					log.info("Send POS DMS Header : \n{}\n",
							JsonUtil.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(dmsComHeaderRes));
					// debug
					log.info("Send POS Body : \n{}\n", JsonUtil.getMapper().writerWithDefaultPrettyPrinter()
							.writeValueAsString(retrieveHwrtEnrAthResO));

					logMsg = String.format("%s : Send POS Body\n%s", "[LDI][DIRT0101][수기 에누리 권한 조회]", JsonUtil
							.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(retrieveHwrtEnrAthResO));
					logMsgMap.put("logMsg", logMsg);
					insertTTRLOG_N(logMsgMap, ssMds, ssDms);

					log.info("pos 응답전문 길이 : [{}], body : [{}]", dmsHdMsgLen, new String(sendDmsHdBodyBytes));

					aes128CipherUtil = new Aes128CipherUtil(socketSendCharset, encHeaderReq.getPOSNO().getBytes(),
							encHeaderReq.getTRANNO().getBytes(), dmsHdMsgLenLpad.getBytes());

					sendEncDmsHdBodyBytes = aes128CipherUtil.encryptAes(sendDmsHdBodyBytes);
				} else
					ret = -1;

				// [3-5] 암호화 헤더 설정
				int totLen = 0;
				if (ret == 0) {
					IrtParseUtil.copySrcEncHdToTgEncHd(encHeaderReq, encHeaderRes);
					totLen = sendEncDmsHdBodyBytes.length + IEnumComIrt.DEF_ENC_HEADER_LEN;
					String totLenLpad = IrtParseUtil.getLpadFillZeroStringForTgFld(encHeaderRes, "TOTLEN", totLen);

					// 전체전문길이 설정
					encHeaderRes.setTOTLEN(totLenLpad);
					// 원문길이 설정
					encHeaderRes.setMSGVLI(dmsHdMsgLenLpad);

					offSetRecv[0] = 0;
					encHeaderBytes = new byte[IEnumComIrt.DEF_ENC_HEADER_LEN];
					ret = IrtParseUtil.irtMapToBytes(encHeaderBytes, offSetRecv, encHeaderRes, socketSendCharset);
				} else
					ret = -1;

				// [3-6] 응답 전문 전송
				if (ret == 0) {
					if ((snedResBytes = IrtParseUtil.byteArraysConcat(encHeaderBytes, sendEncDmsHdBodyBytes)) != null) {
						ret = 0;
						if (socketUtil.tcp_send_b(bos, client, snedResBytes, totLen) == 0) {
							log.debug("send Reply Ok...");
						} else {
							ret = -2;
							log.error("send Reply Error...");
						}
					} else
						ret = -1;
				} else
					ret = -1;
			} else
				ret = -1;

			if (ret != 0) {
				logMsg = String.format("%s : ERROR", "[LDI][DIRT0101][수기 에누리 권한 조회]");
				logMsgMap.put("logMsg", logMsg);
				insertTTRLOG_N(logMsgMap, ssMds, ssDms);
			}
		} catch (Exception e) {
			log.error("DMS 수기 에누리 권한 조회 Exception. {}", e);
			ret = -1;
		} finally {
			// 응답 전문 전송이 정상 처리 되지 않은 모든 에러 발생시 응답 처리 (단 전송 타임아웃이 아닐 때)
			try {
				if (outByteStream != null)
					outByteStream.close();

				if (ret != 0 && ret != -2) {
					sendReplyUnhandledError(scsDaemon, client, bis, bos, encHeaderReq, dmsComHeaderReq);
				}
			} catch (Exception e) {
				log.error("DMS 수기 에누리 권한 조회 finally Exception. {}", e);
			}
		}

		return ret;
	}

	/**
	 * <pre>
	 * DMS 고객 대상행사 및 쿠폰조회
	 * </pre>
	 *
	 * @param scsDaemon
	 * @param client
	 * @param startTime
	 * @param remainTimeOut
	 * @param bis
	 * @param bos
	 * @param encHeaderReq
	 * @param dmsComHeaderReq
	 * @param recvIrtReqData
	 * @param offSet
	 * @return
	 */
	public int msgIdDIRT0107(SCSDaemon scsDaemon, Socket client, BufferedInputStream bis, BufferedOutputStream bos,
			EncHeader encHeaderReq, DmsComHeader dmsComHeaderReq, byte[] recvIrtReqData, int[] offSet, SqlSession ssMds,
			SqlSession ssDms) {
		int ret = -1;
		Aes128CipherUtil aes128CipherUtil = null;

		// 구 전문 요청/응답
		RetrieveCstTgetEvnCpnReqO retrieveCstTgetEvnCpnReqO = null;
		RetrieveCstTgetEvnCpnResO retrieveCstTgetEvnCpnResO = null;

		// 신 전문 요청/응답
		RetrieveCstTgetEvnCpnReqN retrieveCstTgetEvnCpnReqN = null;
		RetrieveCstTgetEvnCpnResN retrieveCstTgetEvnCpnResN = null;

		// 구전문 Object --> Bytes 변환
		ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();

		try {
			// [1] 구 전문 --> 신 전문 변환
			// [1-1] 구 전문 요청 Bytes --> Object 변경
			retrieveCstTgetEvnCpnReqO = new RetrieveCstTgetEvnCpnReqO();
			retrieveCstTgetEvnCpnReqN = new RetrieveCstTgetEvnCpnReqN();

			// 전문 전체를 한번에 리플렉션 할 수 없음(리스트 반복이 항목 값으로 되어 있기 때문에 반복 값을 읽어서 처리)
			// 전문Type
			retrieveCstTgetEvnCpnReqN.setIrtType(IEnumComIrt.EnmIrtTypeWrap.Irt_16.getIrtTypeN());

			// 고객번호(4)
			ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, retrieveCstTgetEvnCpnReqO, "CUST_ID",
					socketRecvCharset);
			retrieveCstTgetEvnCpnReqN.setCUST_ID(retrieveCstTgetEvnCpnReqO.getCUST_ID());

			// 총구매금액(10->15)
			ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, retrieveCstTgetEvnCpnReqO, "BUY_AMT",
					socketRecvCharset);
			retrieveCstTgetEvnCpnReqN.setBUY_AMT(IrtParseUtil.getLpadFillZeroStringForTgFld(retrieveCstTgetEvnCpnReqN,
					"BUY_AMT", retrieveCstTgetEvnCpnReqO.getBUY_AMT()));

			// 신세계포인트보유쿠폰개수(3)
			ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, retrieveCstTgetEvnCpnReqO,
					"SPOINT_CUP_CNT", socketRecvCharset);
			retrieveCstTgetEvnCpnReqN.setSPOINT_CUP_CNT(IrtParseUtil.getLpadFillZeroStringForTgFld(
					retrieveCstTgetEvnCpnReqN, "SPOINT_CUP_CNT", retrieveCstTgetEvnCpnReqO.getSPOINT_CUP_CNT()));

			// 고객유형코드수(3)
			ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, retrieveCstTgetEvnCpnReqO, "CUST_TYPE_CNT",
					socketRecvCharset);
			retrieveCstTgetEvnCpnReqN.setCUST_TYPE_CNT(IrtParseUtil.getLpadFillZeroStringForTgFld(
					retrieveCstTgetEvnCpnReqN, "CUST_TYPE_CNT", retrieveCstTgetEvnCpnReqO.getCUST_TYPE_CNT()));

			// MD코드수(3)
			ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, retrieveCstTgetEvnCpnReqO, "MD_CD_CNT",
					socketRecvCharset);
			retrieveCstTgetEvnCpnReqN.setMD_CD_CNT(IrtParseUtil.getLpadFillZeroStringForTgFld(retrieveCstTgetEvnCpnReqN,
					"MD_CD_CNT", retrieveCstTgetEvnCpnReqO.getMD_CD_CNT()));

			// 실시간금액할인권(2)
			ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, retrieveCstTgetEvnCpnReqO, "RT_CP_ISS_CD",
					socketRecvCharset);
			retrieveCstTgetEvnCpnReqN.setCP_ISSUE(retrieveCstTgetEvnCpnReqO.getRT_CP_ISS_CD());

			// 스마일클럽여부(1)
			ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, retrieveCstTgetEvnCpnReqO, "SMP_CLUB_YN",
					socketRecvCharset);
			retrieveCstTgetEvnCpnReqN.setSMP_CLUB_YN(retrieveCstTgetEvnCpnReqO.getSMP_CLUB_YN());

			// 스마일페이회원번호
			ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, retrieveCstTgetEvnCpnReqO, "SMP_CUST_ID",
					socketRecvCharset);
			retrieveCstTgetEvnCpnReqN.setSMP_CUST_ID(retrieveCstTgetEvnCpnReqO.getSMP_CUST_ID());

			// 세부결제코드
			ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, retrieveCstTgetEvnCpnReqO, "DETAIL_PAY_CD",
					socketRecvCharset);
			retrieveCstTgetEvnCpnReqN.setDETAIL_PAY_CD(retrieveCstTgetEvnCpnReqO.getDETAIL_PAY_CD());

			// 예비(72 -> 100)
			ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, retrieveCstTgetEvnCpnReqO, "FILLER",
					socketRecvCharset);
			retrieveCstTgetEvnCpnReqN.setFILLER(IrtParseUtil.getRpadFillSpaceStringForTgFld(retrieveCstTgetEvnCpnReqN,
					"FILLER", retrieveCstTgetEvnCpnReqO.getFILLER()));

			// CustTypeList
			int custTypeListCount = 0;
			if (retrieveCstTgetEvnCpnReqO.getCUST_TYPE_CNT() != null)
				custTypeListCount = IrtParseUtil.convStrToInt(retrieveCstTgetEvnCpnReqO.getCUST_TYPE_CNT().trim());

			for (int i = 0; i < custTypeListCount; i++) {
				// CustTypeList
				RetrieveCstTgetEvnCpnReqO.CustTypeList custTypeListO = new RetrieveCstTgetEvnCpnReqO.CustTypeList();
				RetrieveCstTgetEvnCpnReqN.CustTypeList custTypeListN = new RetrieveCstTgetEvnCpnReqN.CustTypeList();

				// 고객유형코드(2)
				ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, custTypeListO, "CUST_TYPE",
						socketRecvCharset);
				custTypeListN.setCUST_TYPE(custTypeListO.getCUST_TYPE());

				retrieveCstTgetEvnCpnReqO.getCustTypeList().add(custTypeListO);
				retrieveCstTgetEvnCpnReqN.getCustTypeList().add(custTypeListN);
			}

			// MDList
			int mdListCount = 0;
			if (retrieveCstTgetEvnCpnReqO.getMD_CD_CNT() != null)
				mdListCount = IrtParseUtil.convStrToInt(retrieveCstTgetEvnCpnReqO.getMD_CD_CNT().trim());

			for (int i = 0; i < mdListCount; i++) {

				// MDList
				RetrieveCstTgetEvnCpnReqO.MDList mdListO = new RetrieveCstTgetEvnCpnReqO.MDList();
				RetrieveCstTgetEvnCpnReqN.MDList mdListN = new RetrieveCstTgetEvnCpnReqN.MDList();

				// MD유형코드
				ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, mdListO, "MD_TYPE_CD",
						socketRecvCharset);
				mdListN.setMD_TYPE_CD(mdListO.getMD_TYPE_CD());

				// 신 : 매장코드 (ASIS 없음)
				mdListN.setSHOP_CD(IrtParseUtil.getLpadFillSpaceStringForTgFld(mdListN, "SHOP_CD", ""));

				// 신 : 단품/비단품 구분코드 (ASIS는 MD로만 올라오기때문에 0 : 비단품으로 처리)
				mdListN.setPIPD_NPPD_DVS_CD("0");

				// MD코드
				ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, mdListO, "MD_CD", socketRecvCharset);
				mdListN.setMD_CD(mdListO.getMD_CD());

				// 신 : 단품코드 (ASIS 없음)
				mdListN.setPIPD_CD(IrtParseUtil.getLpadFillSpaceStringForTgFld(mdListN, "PIPD_CD", ""));

				// MD별구매금액(10 -> 15)
				ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, mdListO, "MD_BUY_AMT",
						socketRecvCharset);
				mdListN.setPIPD_BUY_AMT(
						IrtParseUtil.getLpadFillZeroStringForTgFld(mdListN, "PIPD_BUY_AMT", mdListO.getMD_BUY_AMT()));

				// 할특적용여부
				ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, mdListO, "ADD_ENU_YN",
						socketRecvCharset);
				mdListN.setADD_ENU_YN(mdListO.getADD_ENU_YN());

				retrieveCstTgetEvnCpnReqO.getMdList().add(mdListO);
				retrieveCstTgetEvnCpnReqN.getMdList().add(mdListN);
			}

			// [2] 신 전문 API 전송
			if (ret == 0) {
				IrtReqNJson irtReqNJson = new IrtReqNJson();
				// DMS Header --> Com Header 생성
				makeComHeaderFromDmsHeader(dmsComHeaderReq, irtReqNJson.getCOMM_HEADER());
				irtReqNJson.setIRT_REQ(retrieveCstTgetEvnCpnReqN);

				String jsonIrt = JsonUtil.getMapper().writeValueAsString(irtReqNJson);

				// debug
				log.info("신 전문 API 전송 :\n{}",
						JsonUtil.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(irtReqNJson));

				logMsg = String.format("%s : 신 전문 API 전송\n%s", "[LDI][DIRT0107][고객 대상행사 및 쿠폰조회]",
						JsonUtil.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(irtReqNJson));
				logMsgMap.put("logMsg", logMsg);
				insertTTRLOG_N(logMsgMap, ssMds, ssDms);

				Map<String, String> responseMap = new HashMap<String, String>();
				String severUrl = "", irtUrl = "";
				severUrl = ldiUrlProperties.getUrl().get("sever-url");
				irtUrl = ldiUrlProperties.getUrl().get("retrieveCstTgetEvnCpn");

				// 전송 및 응답 수신
				if (httpClient.sendPostData(severUrl + irtUrl, jsonIrt, responseMap, httpRecvCharset, httpSendCharset,
						connectTimeout, serviceTimeout) == 0) {
					IrtResNJson irtResNJson = JsonUtil.getMapper().readValue(responseMap.get("response"),
							IrtResNJson.class);

					if (irtResNJson != null) {
						// debug
						log.info("recv 차세대 응답 : \n{}\n",
								JsonUtil.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(irtResNJson));

						logMsg = String.format("%s : recv 차세대 응답\n%s", "[LDI][DIRT0107][고객 대상행사 및 쿠폰조회]",
								JsonUtil.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(irtResNJson));
						logMsgMap.put("logMsg", logMsg);
						insertTTRLOG_N(logMsgMap, ssMds, ssDms);

						retrieveCstTgetEvnCpnResN = JsonUtil.getMapper().convertValue(irtResNJson.getIRT_RES(),
								RetrieveCstTgetEvnCpnResN.class);
						ret = 0;
					} else
						ret = -1;
				} else
					ret = -1;
			} else
				ret = -1;

			// [3] 신 전문 --> 구 전문 변환
			if (ret == 0) {
				int[] offSetRecv = { 0 };
				byte[] encHeaderBytes = null, dmsComHeaderBytes = null, retrieveCstTgetEvnCpnResOBytes = null,
						sendDmsHdBodyBytes = null, sendEncDmsHdBodyBytes = null, snedResBytes = null;
				EncHeader encHeaderRes = new EncHeader();
				DmsComHeader dmsComHeaderRes = new DmsComHeader();
				retrieveCstTgetEvnCpnResO = new RetrieveCstTgetEvnCpnResO();

				log.info("차세대 응답코드 : {}", retrieveCstTgetEvnCpnResN.getRES_CODE());

				// [3-1] 응답 전문 설정
				// 정상일때만 데이터 들어온다
				if ("0000".equals(retrieveCstTgetEvnCpnResN.getRES_CODE())) {
					// IRT ID
					retrieveCstTgetEvnCpnResO.setIRT_ID(retrieveCstTgetEvnCpnResN.getIRT_ID());
					outByteStream.write(retrieveCstTgetEvnCpnResO.getIRT_ID().getBytes(socketSendCharset));

					// 에누리행사코드수
					retrieveCstTgetEvnCpnResO.setENURI_CD_CNT(IrtParseUtil.getLpadFillZeroStringForTgFld(
							retrieveCstTgetEvnCpnResO, "ENURI_CD_CNT", retrieveCstTgetEvnCpnResN.getENURI_CD_CNT()));
					outByteStream.write(retrieveCstTgetEvnCpnResO.getENURI_CD_CNT().getBytes(socketSendCharset));
					// 쿠폰/금액할인권수
					retrieveCstTgetEvnCpnResO.setCP_CD_CNT(IrtParseUtil.getLpadFillZeroStringForTgFld(
							retrieveCstTgetEvnCpnResO, "CP_CD_CNT", retrieveCstTgetEvnCpnResN.getCP_CD_CNT()));
					outByteStream.write(retrieveCstTgetEvnCpnResO.getCP_CD_CNT().getBytes(socketSendCharset));

					// 쿠폰 발행 대항 거래 여부
					retrieveCstTgetEvnCpnResO.setCP_ISS_YN(IrtParseUtil.getLpadFillSpaceStringForTgFld(
							retrieveCstTgetEvnCpnResO, "CP_ISS_YN", retrieveCstTgetEvnCpnResN.getCP_ISSUE()));
					outByteStream.write(retrieveCstTgetEvnCpnResO.getCP_ISS_YN().getBytes(socketSendCharset));

					// 예비(50 -> 13)
					retrieveCstTgetEvnCpnResO.setFILLER(
							IrtParseUtil.getLpadFillSpaceStringForTgFld(retrieveCstTgetEvnCpnResO, "FILLER", ""));
					outByteStream.write(retrieveCstTgetEvnCpnResO.getFILLER().getBytes(socketSendCharset));

					// 에누리행사 반복
					for (RetrieveCstTgetEvnCpnResN.EnuriList enuriListN : retrieveCstTgetEvnCpnResN.getEnuriList()) {
						// EnuriList
						RetrieveCstTgetEvnCpnResO.EnuriList enuriListO = new RetrieveCstTgetEvnCpnResO.EnuriList();

						// 점포코드
						enuriListO.setENURI_STR_CD(IrtParseUtil.getRpadFillSpaceStringForTgFld(enuriListO,
								"ENURI_STR_CD", enuriListN.getENURI_STR_CD()));
						outByteStream.write(enuriListO.getENURI_STR_CD().getBytes(socketSendCharset));

						// 행사년월
						enuriListO.setENURI_YM(IrtParseUtil.getLpadFillSpaceStringForTgFld(enuriListO, "ENURI_YM",
								enuriListN.getENURI_YM()));
						outByteStream.write(enuriListO.getENURI_YM().getBytes(socketSendCharset));

						// 행사번호 (9 -> 6) 상시에누리인경우 ASIS 행사값 자체는 2자리
						// SUBSTR(ENURI_EVN_NO,8)
						String sENURI_EVT_NO = enuriListN.getENURI_EVT_NO();
						if (sENURI_EVT_NO != null) {
							sENURI_EVT_NO = enuriListN.getENURI_EVT_NO().substring(7);
						} else
							sENURI_EVT_NO = "";
						enuriListO.setENURI_EVT_NO(
								IrtParseUtil.getRpadFillSpaceStringForTgFld(enuriListO, "ENURI_EVT_NO", sENURI_EVT_NO));
						outByteStream.write(enuriListO.getENURI_EVT_NO().getBytes(socketSendCharset));
						log.info("행사번호 : [{}]", enuriListO.getENURI_EVT_NO());

						// 예비
						enuriListO.setENURI_FILLER(
								IrtParseUtil.getLpadFillSpaceStringForTgFld(enuriListO, "ENURI_FILLER", ""));
						outByteStream.write(enuriListO.getENURI_FILLER().getBytes(socketSendCharset));

						retrieveCstTgetEvnCpnResO.getEnuriList().add(enuriListO);
					}

					// 쿠폰/금액할인권 반복
					Map<String, Object> paramMap = new HashMap<>();
					for (RetrieveCstTgetEvnCpnResN.CpList cpListN : retrieveCstTgetEvnCpnResN.getCpList()) {
						// EnuriList
						RetrieveCstTgetEvnCpnResO.CpList cpListO = new RetrieveCstTgetEvnCpnResO.CpList();

						// 점포코드
						cpListO.setCP_STR_CD(IrtParseUtil.getRpadFillSpaceStringForTgFld(cpListO, "CP_STR_CD",
								cpListN.getCP_STR_CD()));
						outByteStream.write(cpListO.getCP_STR_CD().getBytes(socketSendCharset));
						// 행사년월
						cpListO.setCP_YM(
								IrtParseUtil.getLpadFillSpaceStringForTgFld(cpListO, "CP_YM", cpListN.getCP_YM()));
						outByteStream.write(cpListO.getCP_YM().getBytes(socketSendCharset));
						// 행사번호 (9 -> 6) DMS쿠폰인경우 ASIS 행사번호값 자체는 6자리
						// SUBSTR(CP_EVT_NO,4)
						String sCP_EVT_NO = cpListN.getCP_EVT_NO();
						if (sCP_EVT_NO != null) {
							if ("999912".equals(cpListO.getCP_YM()))
								sCP_EVT_NO = cpListN.getCP_EVT_NO().substring(7);
							else
								sCP_EVT_NO = cpListN.getCP_EVT_NO().substring(3);
						} else
							sCP_EVT_NO = "";

						cpListO.setCP_EVT_NO(
								IrtParseUtil.getRpadFillSpaceStringForTgFld(cpListO, "CP_EVT_NO", sCP_EVT_NO));
						outByteStream.write(cpListO.getCP_EVT_NO().getBytes(socketSendCharset));
						log.info("행사번호 : [{}]", cpListO.getCP_EVT_NO());

						// 쿠폰/할인권발급번호 (20 -> 6) PSTR_EVN_CPN_NO에 해당하는
						// MK_CPN_ISSU_P.CPN_ISSU_SNO 를 내림
						// 상시에누리 + 신세게 포인트 쿠폰인지 체크
						if ("999912".equals(cpListO.getCP_YM())) {
							String sSelCP_ISS_NO = "";
							if (cpListN.getCP_ISS_NO() != null) {
								sSelCP_ISS_NO = cpListN.getCP_ISS_NO().trim();
								sSelCP_ISS_NO = sSelCP_ISS_NO.substring(sSelCP_ISS_NO.length() - 6,
										sSelCP_ISS_NO.length());
							}

							cpListO.setCP_ISS_NO(
									IrtParseUtil.getLpadFillZeroStringForTgFld(cpListO, "CP_ISS_NO", sSelCP_ISS_NO));
							outByteStream.write(cpListO.getCP_ISS_NO().getBytes(socketSendCharset));
						} else {
							String custId = "";
							if (retrieveCstTgetEvnCpnReqN.getCUST_ID() != null)
								custId = retrieveCstTgetEvnCpnReqN.getCUST_ID().trim();
							String cpIssNo = "";
							if (cpListN.getCP_ISS_NO() != null)
								cpIssNo = cpListN.getCP_ISS_NO().trim();
							paramMap.clear();
							paramMap.put("custId", custId);
							paramMap.put("cpIssNo", cpIssNo);
							String sSelCP_ISS_NO = selectCpnIssuSno(paramMap, ssMds, ssDms);
							cpListO.setCP_ISS_NO(
									IrtParseUtil.getLpadFillZeroStringForTgFld(cpListO, "CP_ISS_NO", sSelCP_ISS_NO));
							outByteStream.write(cpListO.getCP_ISS_NO().getBytes(socketSendCharset));
						}

						// 쿠폰/할인권번호
						cpListO.setCP_NO(IrtParseUtil.getLpadFillSpaceStringForTgFld(cpListO, "CP_NO", ""));
						outByteStream.write(cpListO.getCP_NO().getBytes(socketSendCharset));

						// 쿠폰구분코드
						String cpKindCdN = cpListN.getCP_KIND_CD();
						String cpKindCdO = "";
						switch (cpKindCdN) {
						case "10":
						case "11":
						case "70":
							cpKindCdO = "10";
							break;
						case "20":
						case "21":
						case "80":
							cpKindCdO = "20";
							break;
						case "30":
						case "40":
						case "50":
						case "60":
							cpKindCdO = cpKindCdN;
							break;
						default:
							cpKindCdO = IrtParseUtil.getLpadFillSpaceStringForTgFld(cpListO, "CP_KIND_CD", "");
							break;
						}
						cpListO.setCP_KIND_CD(cpKindCdO);
						outByteStream.write(cpListO.getCP_KIND_CD().getBytes(socketSendCharset));

						// 발급일자
						cpListO.setCP_ISS_YMD(IrtParseUtil.getLpadFillSpaceStringForTgFld(cpListO, "CP_ISS_YMD",
								cpListN.getCP_ISS_YMD()));
						outByteStream.write(cpListO.getCP_ISS_YMD().getBytes(socketSendCharset));
						// 사용시작일자
						cpListO.setCP_USE_START_YMD(IrtParseUtil.getLpadFillSpaceStringForTgFld(cpListO,
								"CP_USE_START_YMD", cpListN.getCP_USE_START_YMD()));
						outByteStream.write(cpListO.getCP_USE_START_YMD().getBytes(socketSendCharset));
						// 사용종료일자
						cpListO.setCP_USE_END_YMD(IrtParseUtil.getLpadFillSpaceStringForTgFld(cpListO, "CP_USE_END_YMD",
								cpListN.getCP_USE_END_YMD()));
						outByteStream.write(cpListO.getCP_USE_END_YMD().getBytes(socketSendCharset));
						// 할인구분코드
						cpListO.setCP_DCNT_GB_CD(IrtParseUtil.getLpadFillSpaceStringForTgFld(cpListO, "CP_DCNT_GB_CD",
								cpListN.getCP_DCNT_GB_CD()));
						outByteStream.write(cpListO.getCP_DCNT_GB_CD().getBytes(socketSendCharset));

						// 에누리값
						String cpEnuriVal = "";
						int cpDcntAmt = IrtParseUtil.convStrToInt(cpListN.getNRM_CP_DCNT_AMT());

						switch (cpKindCdN) {
						case "10":
							cpEnuriVal = IrtParseUtil.getLpadFillZeroStringForTgFld(cpListO, "CP_ENURI_VAL", 0);
							break;
						case "11":
						case "30":
						case "50":
						case "70": {
							String sCpDcrt = "";
							for (CpMdList cpMdList : cpListN.getCpMdList()) {
								if (cpMdList.getCP_NRM_CP_DCRT() != null)
									sCpDcrt = IrtParseUtil.getLpadFillZeroString(cpMdList.getCP_NRM_CP_DCRT(), 2);
								if (cpMdList.getCP_EVN_CP_DCRT() != null)
									sCpDcrt += IrtParseUtil.getLpadFillZeroString(cpMdList.getCP_EVN_CP_DCRT(), 2);
								break;
							}
							if (sCpDcrt == null)
								sCpDcrt = "";
							cpEnuriVal = IrtParseUtil.getLpadFillZeroStringForTgFld(cpListO, "CP_ENURI_VAL", sCpDcrt);
						}
							break;
						case "20":
						case "21":
						case "40":
						case "60":
						case "80":
							cpEnuriVal = IrtParseUtil.getLpadFillZeroStringForTgFld(cpListO, "CP_ENURI_VAL", cpDcntAmt);
							break;
						default:
							cpEnuriVal = IrtParseUtil.getLpadFillZeroStringForTgFld(cpListO, "CP_ENURI_VAL", "");
							break;
						}
						log.info("cpKindCdN:{}, cpEnuriVal:{}", cpKindCdN, cpEnuriVal);

						cpListO.setCP_ENURI_VAL(cpEnuriVal);
						outByteStream.write(cpListO.getCP_ENURI_VAL().getBytes(socketSendCharset));

						// 보유쿠폰개수
						cpListO.setCP_CNT(
								IrtParseUtil.getLpadFillZeroStringForTgFld(cpListO, "CP_CNT", cpListN.getCP_CNT()));
						outByteStream.write(cpListO.getCP_CNT().getBytes(socketSendCharset));
						// 사용가능(MD기준)여부
						cpListO.setCP_ABLE_YN(IrtParseUtil.getLpadFillSpaceStringForTgFld(cpListO, "CP_ABLE_YN",
								cpListN.getCP_ABLE_YN()));
						outByteStream.write(cpListO.getCP_ABLE_YN().getBytes(socketSendCharset));

						// 사용불가사유
						String sCP_ABLE_MSG = "";
						if (cpListN.getCP_ABLE_MSG() != null)
							sCP_ABLE_MSG = IrtParseUtil.getFixLengthByteValueConvStr(
									cpListN.getCP_ABLE_MSG().trim().getBytes(socketSendCharset),
									IrtParseUtil.getFieldSize(cpListO, "CP_ABLE_MSG"), EnmIrtFieldType.LSTR,
									socketSendCharset);
						else
							sCP_ABLE_MSG = IrtParseUtil.getRpadFillSpaceStringForTgFld(cpListO, "CP_ABLE_MSG", "");

						cpListO.setCP_ABLE_MSG(sCP_ABLE_MSG);
						outByteStream.write(cpListO.getCP_ABLE_MSG().getBytes(socketSendCharset));

						// 발급형태코드
						cpListO.setCP_ISS_WAY_CD(IrtParseUtil.getLpadFillSpaceStringForTgFld(cpListO, "CP_ISS_WAY_CD",
								cpListN.getCP_ISSUE_TYPE()));
						outByteStream.write(cpListO.getCP_ISS_WAY_CD().getBytes(socketSendCharset));

						// 정율최대할인금액
						cpListO.setCP_RT_MAX_AMT(IrtParseUtil.getLpadFillZeroStringForTgFld(cpListO, "CP_RT_MAX_AMT",
								cpListN.getCP_USE_MAX_ENURI_AMT()));
						outByteStream.write(cpListO.getCP_RT_MAX_AMT().getBytes(socketSendCharset));

						// 캠페인행사종류코드
						cpListO.setCP_CAMP_KIND_CD(IrtParseUtil.getRpadFillSpaceStringForTgFld(cpListO,
								"CP_CAMP_KIND_CD", cpListN.getCP_CAMP_KIND_CD()));
						outByteStream.write(cpListO.getCP_CAMP_KIND_CD().getBytes(socketSendCharset));

						// 예비
						cpListO.setCP_FILLER(IrtParseUtil.getLpadFillSpaceStringForTgFld(cpListO, "CP_FILLER", ""));
						outByteStream.write(cpListO.getCP_FILLER().getBytes(socketSendCharset));

						retrieveCstTgetEvnCpnResO.getCpList().add(cpListO);
					}
				}

				retrieveCstTgetEvnCpnResOBytes = outByteStream.toByteArray();
				outByteStream.close();
				outByteStream = null;

				// [3-2] DMS Header 설정
				IrtParseUtil.copySrcDmsHdToTgDmsHd(dmsComHeaderReq, dmsComHeaderRes);
				// [3-2-1] DMS Header : 전문길이 설정
				int dmsBodyLen = retrieveCstTgetEvnCpnResOBytes.length;
				int dmsHdMsgLen = dmsBodyLen + IEnumComIrt.DEF_DMS_COM_HEADER_LEN;
				String dmsHdMsgLenLpad = IrtParseUtil.getLpadFillZeroStringForTgFld(dmsComHeaderRes, "MSG_LEN",
						dmsHdMsgLen);
				dmsComHeaderRes.setMSG_LEN(dmsHdMsgLenLpad);

				// [3-2-2] DMS Header : 응답코드 설정
				dmsComHeaderRes.setRES_CODE(retrieveCstTgetEvnCpnResN.getRES_CODE());

				// [3-2-3] DMS Header : 응답 메시지 설정
				String resMsg = "";
				if (retrieveCstTgetEvnCpnResN.getRES_MSG() != null)
					resMsg = IrtParseUtil.getFixLengthByteValueConvStr(
							retrieveCstTgetEvnCpnResN.getRES_MSG().trim().getBytes(socketSendCharset),
							IrtParseUtil.getFieldSize(dmsComHeaderRes, "RES_MSG"), EnmIrtFieldType.LSTR,
							socketSendCharset);
				else
					resMsg = IrtParseUtil.getRpadFillSpaceStringForTgFld(dmsComHeaderRes, "RES_MSG", "");

				dmsComHeaderRes.setRES_MSG(resMsg);

				// [3-3] 응답 데이터 bytes 변환
				// [3-3-1] DMS Header bytes 변환
				dmsComHeaderBytes = new byte[IEnumComIrt.DEF_DMS_COM_HEADER_LEN];
				ret = IrtParseUtil.irtMapToBytes(dmsComHeaderBytes, offSetRecv, dmsComHeaderRes, socketSendCharset);

				// [3-3-2] DMS Header + Body Bytes Concat
				if (ret == 0) {
					if ((sendDmsHdBodyBytes = IrtParseUtil.byteArraysConcat(dmsComHeaderBytes,
							retrieveCstTgetEvnCpnResOBytes)) == null)
						ret = -1;
				} else
					ret = -1;

				// [3-4] 응답 데이터 Body 암호화
				if (ret == 0) {
					// debug
					log.info("Send POS DMS Header : \n{}\n",
							JsonUtil.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(dmsComHeaderRes));
					// debug
					log.info("Send POS Body : \n{}\n", JsonUtil.getMapper().writerWithDefaultPrettyPrinter()
							.writeValueAsString(retrieveCstTgetEvnCpnResO));

					logMsg = String.format("%s : Send POS Body\n%s", "[LDI][DIRT0107][고객 대상행사 및 쿠폰조회]",
							JsonUtil.getMapper().writerWithDefaultPrettyPrinter()
									.writeValueAsString(retrieveCstTgetEvnCpnResO));
					logMsgMap.put("logMsg", logMsg);
					insertTTRLOG_N(logMsgMap, ssMds, ssDms);

					log.info("pos 응답전문 길이 : [{}], body : [{}]", dmsHdMsgLen, new String(sendDmsHdBodyBytes));

					aes128CipherUtil = new Aes128CipherUtil(socketSendCharset, encHeaderReq.getPOSNO().getBytes(),
							encHeaderReq.getTRANNO().getBytes(), dmsHdMsgLenLpad.getBytes());

					sendEncDmsHdBodyBytes = aes128CipherUtil.encryptAes(sendDmsHdBodyBytes);
				} else
					ret = -1;

				// [3-5] 암호화 헤더 설정
				int totLen = 0;
				if (ret == 0) {
					IrtParseUtil.copySrcEncHdToTgEncHd(encHeaderReq, encHeaderRes);
					totLen = sendEncDmsHdBodyBytes.length + IEnumComIrt.DEF_ENC_HEADER_LEN;
					String totLenLpad = IrtParseUtil.getLpadFillZeroStringForTgFld(encHeaderRes, "TOTLEN", totLen);

					// 전체전문길이 설정
					encHeaderRes.setTOTLEN(totLenLpad);
					// 원문길이 설정
					encHeaderRes.setMSGVLI(dmsHdMsgLenLpad);

					offSetRecv[0] = 0;
					encHeaderBytes = new byte[IEnumComIrt.DEF_ENC_HEADER_LEN];
					ret = IrtParseUtil.irtMapToBytes(encHeaderBytes, offSetRecv, encHeaderRes, socketSendCharset);
				} else
					ret = -1;

				// [3-6] 응답 전문 전송
				if (ret == 0) {
					if ((snedResBytes = IrtParseUtil.byteArraysConcat(encHeaderBytes, sendEncDmsHdBodyBytes)) != null) {
						ret = 0;
						if (socketUtil.tcp_send_b(bos, client, snedResBytes, totLen) == 0) {
							log.debug("send Reply Ok...");
						} else {
							ret = -2;
							log.error("send Reply Error...");
						}
					} else
						ret = -1;
				} else
					ret = -1;
			} else
				ret = -1;

			if (ret != 0) {
				logMsg = String.format("%s : ERROR", "[LDI][DIRT0107][고객 대상행사 및 쿠폰조회]");
				logMsgMap.put("logMsg", logMsg);
				insertTTRLOG_N(logMsgMap, ssMds, ssDms);
			}
		} catch (Exception e) {
			log.error("DMS 고객 대상행사 및 쿠폰조회 Exception. {}", e);
			ret = -1;
		} finally {
			// 응답 전문 전송이 정상 처리 되지 않은 모든 에러 발생시 응답 처리 (단 전송 타임아웃이 아닐 때)
			try {
				if (outByteStream != null)
					outByteStream.close();

				if (ret != 0 && ret != -2) {
					sendReplyUnhandledError(scsDaemon, client, bis, bos, encHeaderReq, dmsComHeaderReq);
				}
			} catch (Exception e) {
				log.error("DMS 고객 대상행사 및 쿠폰조회 finally Exception. {}", e);
			}
		}

		return ret;
	}

	/**
	 * <pre>
	 * DMS 에누리 행사 정보조회
	 * </pre>
	 *
	 * @param scsDaemon
	 * @param client
	 * @param startTime
	 * @param remainTimeOut
	 * @param bis
	 * @param bos
	 * @param encHeaderReq
	 * @param dmsComHeaderReq
	 * @param recvIrtReqData
	 * @param offSet
	 * @return
	 */
	public int msgIdDIRT0103(SCSDaemon scsDaemon, Socket client, BufferedInputStream bis, BufferedOutputStream bos,
			EncHeader encHeaderReq, DmsComHeader dmsComHeaderReq, byte[] recvIrtReqData, int[] offSet, SqlSession ssMds,
			SqlSession ssDms) {
		int ret = -1;
		Aes128CipherUtil aes128CipherUtil = null;

		// 구 전문 요청/응답
		RetrieveEnrEvnInfoReqO retrieveEnrEvnInfoReqO = null;
		RetrieveEnrEvnInfoResO retrieveEnrEvnInfoResO = null;

		// 신 전문 요청/응답
		RetrieveEnrEvnInfoReqN retrieveEnrEvnInfoReqN = null;
		RetrieveEnrEvnInfoResN retrieveEnrEvnInfoResN = null;

		// 구전문 Object --> Bytes 변환
		ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();

		try {
			// [1] 구 전문 --> 신 전문 변환
			// [1-1] 구 전문 요청 Bytes --> Object 변경
			retrieveEnrEvnInfoReqO = new RetrieveEnrEvnInfoReqO();
			retrieveEnrEvnInfoReqN = new RetrieveEnrEvnInfoReqN();

			// 전문 전체를 한번에 리플렉션 할 수 없음(리스트 반복이 항목 값으로 되어 있기 때문에 반복 값을 읽어서 처리)
			// 전문Type
			retrieveEnrEvnInfoReqN.setIrtType(IEnumComIrt.EnmIrtTypeWrap.Irt_12.getIrtTypeN());

			// 점포코드
			ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, retrieveEnrEvnInfoReqO, "ORG_ENURI_STR_CD",
					socketRecvCharset);
			retrieveEnrEvnInfoReqN.setORG_ENURI_STR_CD(IrtParseUtil.getRpadFillSpaceStringConvIntForTgFld(
					retrieveEnrEvnInfoReqN, "ORG_ENURI_STR_CD", retrieveEnrEvnInfoReqO.getORG_ENURI_STR_CD()));

			// 행사년월
			ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, retrieveEnrEvnInfoReqO, "ORG_ENURI_YM",
					socketRecvCharset);
			retrieveEnrEvnInfoReqN.setORG_ENURI_YM(retrieveEnrEvnInfoReqO.getORG_ENURI_YM());

			// 행사번호(6 -> 9) 행사번호값 자체는 2자리 '0000000' + TRIM(ORG_ENURI_EVN_NO)
			ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, retrieveEnrEvnInfoReqO, "ORG_ENURI_EVT_NO",
					socketRecvCharset);
			String sORG_ENURI_EVT_NO = retrieveEnrEvnInfoReqO.getORG_ENURI_EVT_NO();
			if (sORG_ENURI_EVT_NO != null) {
				sORG_ENURI_EVT_NO = sORG_ENURI_EVT_NO.trim();
				if (sORG_ENURI_EVT_NO.length() > 2)
					sORG_ENURI_EVT_NO = sORG_ENURI_EVT_NO.substring(0, 2);
			} else
				sORG_ENURI_EVT_NO = "";
			sORG_ENURI_EVT_NO = IrtParseUtil.getRpadFillSpaceString(sORG_ENURI_EVT_NO, 2);

			retrieveEnrEvnInfoReqN.setORG_ENURI_EVT_NO("0000000" + sORG_ENURI_EVT_NO);
			log.info("행사번호 : [{}]", retrieveEnrEvnInfoReqN.getORG_ENURI_EVT_NO());

			// 신 : 쿠폰코드(17)
			retrieveEnrEvnInfoReqN
					.setORG_CP_CD(IrtParseUtil.getLpadFillSpaceStringForTgFld(retrieveEnrEvnInfoReqN, "ORG_CP_CD", ""));

			// 예비
			ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, retrieveEnrEvnInfoReqO, "FILLER",
					socketRecvCharset);
			retrieveEnrEvnInfoReqN.setFILLER(retrieveEnrEvnInfoReqO.getFILLER());

			// [2] 신 전문 API 전송
			if (ret == 0) {
				IrtReqNJson irtReqNJson = new IrtReqNJson();
				// DMS Header --> Com Header 생성
				makeComHeaderFromDmsHeader(dmsComHeaderReq, irtReqNJson.getCOMM_HEADER());
				irtReqNJson.setIRT_REQ(retrieveEnrEvnInfoReqN);

				String jsonIrt = JsonUtil.getMapper().writeValueAsString(irtReqNJson);

				// debug
				log.info("신 전문 API 전송 :\n{}",
						JsonUtil.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(irtReqNJson));

				logMsg = String.format("%s : 신 전문 API 전송\n%s", "[LDI][DIRT0103][에누리 행사 정보조회]",
						JsonUtil.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(irtReqNJson));
				logMsgMap.put("logMsg", logMsg);
				insertTTRLOG_N(logMsgMap, ssMds, ssDms);

				Map<String, String> responseMap = new HashMap<String, String>();
				String severUrl = "", irtUrl = "";
				severUrl = ldiUrlProperties.getUrl().get("sever-url");
				irtUrl = ldiUrlProperties.getUrl().get("retrieveEnrEvnInfo");

				// 전송 및 응답 수신
				if (httpClient.sendPostData(severUrl + irtUrl, jsonIrt, responseMap, httpRecvCharset, httpSendCharset,
						connectTimeout, serviceTimeout) == 0) {
					IrtResNJson irtResNJson = JsonUtil.getMapper().readValue(responseMap.get("response"),
							IrtResNJson.class);

					if (irtResNJson != null) {
						// debug
						log.info("recv 차세대 응답 : \n{}\n",
								JsonUtil.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(irtResNJson));

						logMsg = String.format("%s : recv 차세대 응답\n%s", "[LDI][DIRT0103][에누리 행사 정보조회]",
								JsonUtil.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(irtResNJson));
						logMsgMap.put("logMsg", logMsg);
						insertTTRLOG_N(logMsgMap, ssMds, ssDms);

						retrieveEnrEvnInfoResN = JsonUtil.getMapper().convertValue(irtResNJson.getIRT_RES(),
								RetrieveEnrEvnInfoResN.class);
						ret = 0;
					} else
						ret = -1;
				} else
					ret = -1;
			} else
				ret = -1;

			// [3] 신 전문 --> 구 전문 변환
			if (ret == 0) {
				int[] offSetRecv = { 0 };
				byte[] encHeaderBytes = null, dmsComHeaderBytes = null, retrieveEnrEvnInfoResOBytes = null,
						sendDmsHdBodyBytes = null, sendEncDmsHdBodyBytes = null, snedResBytes = null;
				EncHeader encHeaderRes = new EncHeader();
				DmsComHeader dmsComHeaderRes = new DmsComHeader();
				retrieveEnrEvnInfoResO = new RetrieveEnrEvnInfoResO();

				log.info("차세대 응답코드 : {}", retrieveEnrEvnInfoResN.getRES_CODE());

				// [3-1] 응답 전문 설정
				// 정상일때만 데이터 들어온다
				if ("0000".equals(retrieveEnrEvnInfoResN.getRES_CODE())) {
					// 행사명
					String sEVT_NM = "";
					if (retrieveEnrEvnInfoResN.getEVT_NM() != null)
						sEVT_NM = IrtParseUtil.getFixLengthByteValueConvStr(
								retrieveEnrEvnInfoResN.getEVT_NM().trim().getBytes(socketSendCharset),
								IrtParseUtil.getFieldSize(retrieveEnrEvnInfoResO, "EVT_NM"), EnmIrtFieldType.LSTR,
								socketSendCharset);
					else
						sEVT_NM = IrtParseUtil.getRpadFillSpaceStringForTgFld(retrieveEnrEvnInfoResO, "EVT_NM", "");

					retrieveEnrEvnInfoResO.setEVT_NM(sEVT_NM);
					outByteStream.write(retrieveEnrEvnInfoResO.getEVT_NM().getBytes(socketSendCharset));

					// 행사종류코드
					retrieveEnrEvnInfoResO.setEVT_KIND_CD(IrtParseUtil.getLpadFillSpaceStringForTgFld(
							retrieveEnrEvnInfoResO, "EVT_KIND_CD", retrieveEnrEvnInfoResN.getEVT_KIND_CD()));
					outByteStream.write(retrieveEnrEvnInfoResO.getEVT_KIND_CD().getBytes(socketSendCharset));

					// 최소구매금액 (15 -> 10)
					retrieveEnrEvnInfoResO.setEVT_MIN_BUYAMT(IrtParseUtil.getLpadFillZeroStringForTgFld(
							retrieveEnrEvnInfoResO, "EVT_MIN_BUYAMT", retrieveEnrEvnInfoResN.getEVT_MIN_BUYAMT()));
					outByteStream.write(retrieveEnrEvnInfoResO.getEVT_MIN_BUYAMT().getBytes(socketSendCharset));

					// 전체MD여부
					retrieveEnrEvnInfoResO.setMDALL_YN(IrtParseUtil.getLpadFillSpaceStringForTgFld(
							retrieveEnrEvnInfoResO, "MDALL_YN", retrieveEnrEvnInfoResN.getMDALL_YN()));
					outByteStream.write(retrieveEnrEvnInfoResO.getMDALL_YN().getBytes(socketSendCharset));

					// 사은행사참여가능여부
					retrieveEnrEvnInfoResO.setPR_JOIN_YN(IrtParseUtil.getLpadFillSpaceStringForTgFld(
							retrieveEnrEvnInfoResO, "PR_JOIN_YN", retrieveEnrEvnInfoResN.getPR_JOIN_YN()));
					outByteStream.write(retrieveEnrEvnInfoResO.getPR_JOIN_YN().getBytes(socketSendCharset));

					// 할특중복가능여부
					retrieveEnrEvnInfoResO.setADD_ENU_YN(IrtParseUtil.getLpadFillSpaceStringForTgFld(
							retrieveEnrEvnInfoResO, "ADD_ENU_YN", retrieveEnrEvnInfoResN.getADD_ENU_YN()));
					outByteStream.write(retrieveEnrEvnInfoResO.getADD_ENU_YN().getBytes(socketSendCharset));

					// 전액결제여부
					retrieveEnrEvnInfoResO.setONEPAY_YN(IrtParseUtil.getLpadFillSpaceStringForTgFld(
							retrieveEnrEvnInfoResO, "ONEPAY_YN", retrieveEnrEvnInfoResN.getONEPAY_YN()));
					outByteStream.write(retrieveEnrEvnInfoResO.getONEPAY_YN().getBytes(socketSendCharset));

					// 사용전체MD여부
					retrieveEnrEvnInfoResO.setUSE_MDALL_YN(retrieveEnrEvnInfoResN.getUSE_MDALL_YN());
					outByteStream.write(retrieveEnrEvnInfoResO.getUSE_MDALL_YN().getBytes(socketSendCharset));

					// 사용전액결제여부
					retrieveEnrEvnInfoResO.setUSE_ONEPAY_YN(IrtParseUtil.getLpadFillSpaceStringForTgFld(
							retrieveEnrEvnInfoResO, "USE_ONEPAY_YN", retrieveEnrEvnInfoResN.getUSE_ONEPAY_YN()));
					outByteStream.write(retrieveEnrEvnInfoResO.getUSE_ONEPAY_YN().getBytes(socketSendCharset));

					// 사용가능최소구매금액
					retrieveEnrEvnInfoResO.setUSE_MIN_BUYAMT(IrtParseUtil.getLpadFillZeroStringForTgFld(
							retrieveEnrEvnInfoResO, "USE_MIN_BUYAMT", retrieveEnrEvnInfoResN.getUSE_MIN_BUYAMT()));
					outByteStream.write(retrieveEnrEvnInfoResO.getUSE_MIN_BUYAMT().getBytes(socketSendCharset));

					// POS대상MD개수
					retrieveEnrEvnInfoResO.setMD_MST_CNT(IrtParseUtil.getLpadFillZeroStringForTgFld(
							retrieveEnrEvnInfoResO, "MD_MST_CNT", retrieveEnrEvnInfoResN.getMD_MST_CNT()));
					outByteStream.write(retrieveEnrEvnInfoResO.getMD_MST_CNT().getBytes(socketSendCharset));

					// POS대상결제수단개수
					retrieveEnrEvnInfoResO.setPAY_MST_CNT(IrtParseUtil.getLpadFillZeroStringForTgFld(
							retrieveEnrEvnInfoResO, "PAY_MST_CNT", retrieveEnrEvnInfoResN.getPAY_MST_CNT()));
					outByteStream.write(retrieveEnrEvnInfoResO.getPAY_MST_CNT().getBytes(socketSendCharset));

					// POS대상카드빈개수
					retrieveEnrEvnInfoResO.setBIN_MST_CNT(IrtParseUtil.getLpadFillZeroStringForTgFld(
							retrieveEnrEvnInfoResO, "BIN_MST_CNT", retrieveEnrEvnInfoResN.getBIN_MST_CNT()));
					outByteStream.write(retrieveEnrEvnInfoResO.getBIN_MST_CNT().getBytes(socketSendCharset));

					// 예비
					String sFILLER = "";
					if (retrieveEnrEvnInfoResN.getFILLER() != null)
						sFILLER = IrtParseUtil.getFixLengthByteValueConvStr(
								retrieveEnrEvnInfoResN.getFILLER().trim().getBytes(socketSendCharset),
								IrtParseUtil.getFieldSize(retrieveEnrEvnInfoResO, "FILLER"), EnmIrtFieldType.LSTR,
								socketSendCharset);
					else
						sFILLER = IrtParseUtil.getRpadFillSpaceStringForTgFld(retrieveEnrEvnInfoResO, "FILLER", "");

					retrieveEnrEvnInfoResO.setFILLER(sFILLER);
					outByteStream.write(retrieveEnrEvnInfoResO.getFILLER().getBytes(socketSendCharset));

					// MdList
					for (RetrieveEnrEvnInfoResN.MdList mdListN : retrieveEnrEvnInfoResN.getMdList()) {
						// MdList
						RetrieveEnrEvnInfoResO.MdList mdListO = new RetrieveEnrEvnInfoResO.MdList();

						// MD구분코드
						mdListO.setMD_GB_CD(IrtParseUtil.getLpadFillSpaceStringForTgFld(mdListO, "MD_GB_CD",
								mdListN.getMD_GB_CD()));
						outByteStream.write(mdListO.getMD_GB_CD().getBytes(socketSendCharset));

						// MD코드
						mdListO.setMD_CD(
								IrtParseUtil.getLpadFillSpaceStringForTgFld(mdListO, "MD_CD", mdListN.getMD_CD()));
						outByteStream.write(mdListO.getMD_CD().getBytes(socketSendCharset));

						// 할인구분코드
						mdListO.setDCNT_GB_CD(IrtParseUtil.getLpadFillSpaceStringForTgFld(mdListO, "DCNT_GB_CD",
								mdListN.getDCNT_GB_CD()));
						outByteStream.write(mdListO.getDCNT_GB_CD().getBytes(socketSendCharset));

						// 정상에누리
						mdListO.setNOR_ENURI_VAL(IrtParseUtil.getLpadFillZeroStringForTgFld(mdListO, "NOR_ENURI_VAL",
								mdListN.getNOR_ENURI_VAL()));
						outByteStream.write(mdListO.getNOR_ENURI_VAL().getBytes(socketSendCharset));

						// 행사에누리
						mdListO.setEVT_ENURI_VAL(IrtParseUtil.getLpadFillZeroStringForTgFld(mdListO, "EVT_ENURI_VAL",
								mdListN.getEVT_ENURI_VAL()));
						outByteStream.write(mdListO.getEVT_ENURI_VAL().getBytes(socketSendCharset));

						// 예비
						if (mdListN.getMD_FILLER() != null)
							sFILLER = IrtParseUtil.getFixLengthByteValueConvStr(
									mdListN.getMD_FILLER().trim().getBytes(socketSendCharset),
									IrtParseUtil.getFieldSize(mdListO, "MD_FILLER"), EnmIrtFieldType.LSTR,
									socketSendCharset);
						else
							sFILLER = IrtParseUtil.getRpadFillSpaceStringForTgFld(mdListO, "MD_FILLER", "");

						mdListO.setMD_FILLER(sFILLER);
						outByteStream.write(mdListO.getMD_FILLER().getBytes(socketSendCharset));

						retrieveEnrEvnInfoResO.getMdList().add(mdListO);
					}

					// PayList
					for (RetrieveEnrEvnInfoResN.PayList payListN : retrieveEnrEvnInfoResN.getPayList()) {
						// MdList
						RetrieveEnrEvnInfoResO.PayList payListO = new RetrieveEnrEvnInfoResO.PayList();

						// 결제수단코드
						payListO.setPAY_CD(
								IrtParseUtil.getLpadFillSpaceStringForTgFld(payListO, "PAY_CD", payListN.getPAY_CD()));
						outByteStream.write(payListO.getPAY_CD().getBytes(socketSendCharset));

						// 예비
						if (payListN.getPAY_FILLER() != null)
							sFILLER = IrtParseUtil.getFixLengthByteValueConvStr(
									payListN.getPAY_FILLER().trim().getBytes(socketSendCharset),
									IrtParseUtil.getFieldSize(payListO, "PAY_FILLER"), EnmIrtFieldType.LSTR,
									socketSendCharset);
						else
							sFILLER = IrtParseUtil.getRpadFillSpaceStringForTgFld(payListO, "PAY_FILLER", "");

						payListO.setPAY_FILLER(sFILLER);
						outByteStream.write(payListO.getPAY_FILLER().getBytes(socketSendCharset));

						retrieveEnrEvnInfoResO.getPayList().add(payListO);
					}

					// BinList
					for (RetrieveEnrEvnInfoResN.BinList binListN : retrieveEnrEvnInfoResN.getBinList()) {
						// MdList
						RetrieveEnrEvnInfoResO.BinList binListO = new RetrieveEnrEvnInfoResO.BinList();

						// 카드빈시작
						binListO.setBIN_START(IrtParseUtil.getLpadFillSpaceStringForTgFld(binListO, "BIN_START",
								binListN.getBIN_START()));
						outByteStream.write(binListO.getBIN_START().getBytes(socketSendCharset));

						// 카드빈종료
						binListO.setBIN_END(IrtParseUtil.getLpadFillSpaceStringForTgFld(binListO, "BIN_END",
								binListN.getBIN_END()));
						outByteStream.write(binListO.getBIN_END().getBytes(socketSendCharset));

						// 행사카드명
						String sEVT_CARD_NM = "";
						if (binListN.getEVT_CARD_NM() != null)
							sEVT_CARD_NM = IrtParseUtil.getFixLengthByteValueConvStr(
									binListN.getEVT_CARD_NM().trim().getBytes(socketSendCharset),
									IrtParseUtil.getFieldSize(binListO, "EVT_CARD_NM"), EnmIrtFieldType.LSTR,
									socketSendCharset);
						else
							sEVT_CARD_NM = IrtParseUtil.getRpadFillSpaceStringForTgFld(binListO, "EVT_CARD_NM", "");

						binListO.setEVT_CARD_NM(sEVT_CARD_NM);
						outByteStream.write(binListO.getEVT_CARD_NM().getBytes(socketSendCharset));

						// 카드빈시작_8자리
						binListO.setBIN8_START(IrtParseUtil.getRpadFillZeroStringForTgFld(binListO, "BIN8_START",
								binListN.getBIN8_START()));
						outByteStream.write(binListO.getBIN8_START().getBytes(socketSendCharset));

						// 카드빈종료_8자리
						binListO.setBIN8_END(IrtParseUtil.getRpadFillZeroStringForTgFld(binListO, "BIN8_END",
								binListN.getBIN8_END()));
						outByteStream.write(binListO.getBIN8_END().getBytes(socketSendCharset));

						// 예비
						if (binListN.getBIN_FILLER() != null)
							sFILLER = IrtParseUtil.getFixLengthByteValueConvStr(
									binListN.getBIN_FILLER().trim().getBytes(socketSendCharset),
									IrtParseUtil.getFieldSize(binListO, "BIN_FILLER"), EnmIrtFieldType.LSTR,
									socketSendCharset);
						else
							sFILLER = IrtParseUtil.getRpadFillSpaceStringForTgFld(binListO, "BIN_FILLER", "");

						binListO.setBIN_FILLER(sFILLER);
						outByteStream.write(binListO.getBIN_FILLER().getBytes(socketSendCharset));

						retrieveEnrEvnInfoResO.getBinList().add(binListO);
					}
				}

				retrieveEnrEvnInfoResOBytes = outByteStream.toByteArray();
				outByteStream.close();
				outByteStream = null;

				// [3-2] DMS Header 설정
				IrtParseUtil.copySrcDmsHdToTgDmsHd(dmsComHeaderReq, dmsComHeaderRes);
				// [3-2-1] DMS Header : 전문길이 설정
				int dmsBodyLen = retrieveEnrEvnInfoResOBytes.length;
				int dmsHdMsgLen = dmsBodyLen + IEnumComIrt.DEF_DMS_COM_HEADER_LEN;
				String dmsHdMsgLenLpad = IrtParseUtil.getLpadFillZeroStringForTgFld(dmsComHeaderRes, "MSG_LEN",
						dmsHdMsgLen);
				dmsComHeaderRes.setMSG_LEN(dmsHdMsgLenLpad);

				// [3-2-2] DMS Header : 응답코드 설정
				dmsComHeaderRes.setRES_CODE(retrieveEnrEvnInfoResN.getRES_CODE());
				// [3-2-3] DMS Header : 응답 메시지 설정
				String resMsg = "";
				if (retrieveEnrEvnInfoResN.getRES_MSG() != null)
					resMsg = IrtParseUtil.getFixLengthByteValueConvStr(
							retrieveEnrEvnInfoResN.getRES_MSG().trim().getBytes(socketSendCharset),
							IrtParseUtil.getFieldSize(dmsComHeaderRes, "RES_MSG"), EnmIrtFieldType.LSTR,
							socketSendCharset);
				else
					resMsg = IrtParseUtil.getRpadFillSpaceStringForTgFld(dmsComHeaderRes, "RES_MSG", "");

				dmsComHeaderRes.setRES_MSG(resMsg);

				// [3-3] 응답 데이터 bytes 변환
				// [3-3-1] DMS Header bytes 변환
				dmsComHeaderBytes = new byte[IEnumComIrt.DEF_DMS_COM_HEADER_LEN];
				ret = IrtParseUtil.irtMapToBytes(dmsComHeaderBytes, offSetRecv, dmsComHeaderRes, socketSendCharset);

				// [3-3-2] DMS Header + Body Bytes Concat
				if (ret == 0) {
					if ((sendDmsHdBodyBytes = IrtParseUtil.byteArraysConcat(dmsComHeaderBytes,
							retrieveEnrEvnInfoResOBytes)) == null)
						ret = -1;
				} else
					ret = -1;

				// [3-4] 응답 데이터 Body 암호화
				if (ret == 0) {
					// debug
					log.info("Send POS DMS Header : \n{}\n",
							JsonUtil.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(dmsComHeaderRes));
					// debug
					log.info("Send POS Body : \n{}\n", JsonUtil.getMapper().writerWithDefaultPrettyPrinter()
							.writeValueAsString(retrieveEnrEvnInfoResO));

					logMsg = String.format("%s : Send POS Body\n%s", "[LDI][DIRT0103][에누리 행사 정보조회]", JsonUtil
							.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(retrieveEnrEvnInfoResO));
					logMsgMap.put("logMsg", logMsg);
					insertTTRLOG_N(logMsgMap, ssMds, ssDms);

					log.info("pos 응답전문 길이 : [{}], body : [{}]", dmsHdMsgLen, new String(sendDmsHdBodyBytes));

					aes128CipherUtil = new Aes128CipherUtil(socketSendCharset, encHeaderReq.getPOSNO().getBytes(),
							encHeaderReq.getTRANNO().getBytes(), dmsHdMsgLenLpad.getBytes());

					sendEncDmsHdBodyBytes = aes128CipherUtil.encryptAes(sendDmsHdBodyBytes);
				} else
					ret = -1;

				// [3-5] 암호화 헤더 설정
				int totLen = 0;
				if (ret == 0) {
					IrtParseUtil.copySrcEncHdToTgEncHd(encHeaderReq, encHeaderRes);
					totLen = sendEncDmsHdBodyBytes.length + IEnumComIrt.DEF_ENC_HEADER_LEN;
					String totLenLpad = IrtParseUtil.getLpadFillZeroStringForTgFld(encHeaderRes, "TOTLEN", totLen);

					// 전체전문길이 설정
					encHeaderRes.setTOTLEN(totLenLpad);
					// 원문길이 설정
					encHeaderRes.setMSGVLI(dmsHdMsgLenLpad);

					offSetRecv[0] = 0;
					encHeaderBytes = new byte[IEnumComIrt.DEF_ENC_HEADER_LEN];
					ret = IrtParseUtil.irtMapToBytes(encHeaderBytes, offSetRecv, encHeaderRes, socketSendCharset);
				} else
					ret = -1;

				// [3-6] 응답 전문 전송
				if (ret == 0) {
					if ((snedResBytes = IrtParseUtil.byteArraysConcat(encHeaderBytes, sendEncDmsHdBodyBytes)) != null) {
						ret = 0;
						if (socketUtil.tcp_send_b(bos, client, snedResBytes, totLen) == 0) {
							log.debug("send Reply Ok...");
						} else {
							ret = -2;
							log.error("send Reply Error...");
						}
					} else
						ret = -1;
				} else
					ret = -1;
			} else
				ret = -1;

			if (ret != 0) {
				logMsg = String.format("%s : ERROR", "[LDI][DIRT0103][에누리 행사 정보조회]");
				logMsgMap.put("logMsg", logMsg);
				insertTTRLOG_N(logMsgMap, ssMds, ssDms);
			}
		} catch (Exception e) {
			log.error("DMS 에누리 행사 정보조회 Exception. {}", e);
			ret = -1;
		} finally {
			// 응답 전문 전송이 정상 처리 되지 않은 모든 에러 발생시 응답 처리 (단 전송 타임아웃이 아닐 때)
			try {
				if (outByteStream != null)
					outByteStream.close();

				if (ret != 0 && ret != -2) {
					sendReplyUnhandledError(scsDaemon, client, bis, bos, encHeaderReq, dmsComHeaderReq);
				}
			} catch (Exception e) {
				log.error("DMS 에누리 행사 정보조회 finally Exception. {}", e);
			}
		}

		return ret;
	}

	/**
	 * <pre>
	 * DMS 쿠폰 및 금액할인권 발급 예정 조회 8BIN
	 * </pre>
	 *
	 * @param scsDaemon
	 * @param client
	 * @param startTime
	 * @param remainTimeOut
	 * @param bis
	 * @param bos
	 * @param encHeaderReq
	 * @param dmsComHeaderReq
	 * @param recvIrtReqData
	 * @param offSet
	 * @return
	 */
	public int msgIdDIRT0106(SCSDaemon scsDaemon, Socket client, BufferedInputStream bis, BufferedOutputStream bos,
			EncHeader encHeaderReq, DmsComHeader dmsComHeaderReq, byte[] recvIrtReqData, int[] offSet, SqlSession ssMds,
			SqlSession ssDms) {
		int ret = -1;
		Aes128CipherUtil aes128CipherUtil = null;

		// 구 전문 요청/응답
		RetrieveCpnAmtDctkIssuPlndReqO retrieveCpnAmtDctkIssuPlndReqO = null;
		RetrieveCpnAmtDctkIssuPlndResO retrieveCpnAmtDctkIssuPlndResO = null;

		// 신 전문 요청/응답
		RetrieveCpnAmtDctkIssuPlndReqN retrieveCpnAmtDctkIssuPlndReqN = null;
		RetrieveCpnAmtDctkIssuPlndResN retrieveCpnAmtDctkIssuPlndResN = null;

		// 구전문 Object --> Bytes 변환
		ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();

		try {
			// [1] 구 전문 --> 신 전문 변환
			// [1-1] 구 전문 요청 Bytes --> Object 변경
			retrieveCpnAmtDctkIssuPlndReqO = new RetrieveCpnAmtDctkIssuPlndReqO();
			retrieveCpnAmtDctkIssuPlndReqN = new RetrieveCpnAmtDctkIssuPlndReqN();

			// 전문 전체를 한번에 리플렉션 할 수 없음(리스트 반복이 항목 값으로 되어 있기 때문에 반복 값을 읽어서 처리)
			// 전문Type
			retrieveCpnAmtDctkIssuPlndReqN.setIrtType(IEnumComIrt.EnmIrtTypeWrap.Irt_15.getIrtTypeN());

			// 고객번호
			ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, retrieveCpnAmtDctkIssuPlndReqO, "CUST_ID",
					socketRecvCharset);
			retrieveCpnAmtDctkIssuPlndReqN.setCUST_ID(retrieveCpnAmtDctkIssuPlndReqO.getCUST_ID());

			// IRT ID
			ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, retrieveCpnAmtDctkIssuPlndReqO, "IRT_ID",
					socketRecvCharset);
			retrieveCpnAmtDctkIssuPlndReqN.setIRT_ID(retrieveCpnAmtDctkIssuPlndReqO.getIRT_ID());

			// 총구매액
			ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, retrieveCpnAmtDctkIssuPlndReqO, "BUY_AMT",
					socketRecvCharset);
			retrieveCpnAmtDctkIssuPlndReqN.setBUY_AMT(retrieveCpnAmtDctkIssuPlndReqO.getBUY_AMT());

			// 고객유형코드수
			ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, retrieveCpnAmtDctkIssuPlndReqO,
					"CUST_TYPE_CNT", socketRecvCharset);
			retrieveCpnAmtDctkIssuPlndReqN.setCUST_CNT(retrieveCpnAmtDctkIssuPlndReqO.getCUST_TYPE_CNT());

			// MD코드수
			ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, retrieveCpnAmtDctkIssuPlndReqO,
					"MD_CD_CNT", socketRecvCharset);
			retrieveCpnAmtDctkIssuPlndReqN.setMD_CNT(retrieveCpnAmtDctkIssuPlndReqO.getMD_CD_CNT());

			// 결제수단개수
			ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, retrieveCpnAmtDctkIssuPlndReqO, "PAY_CNT",
					socketRecvCharset);
			retrieveCpnAmtDctkIssuPlndReqN.setPAY_CNT(retrieveCpnAmtDctkIssuPlndReqO.getPAY_CNT());

			// 예비
			ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, retrieveCpnAmtDctkIssuPlndReqO, "FILLER",
					socketRecvCharset);
			retrieveCpnAmtDctkIssuPlndReqN.setFILLER(retrieveCpnAmtDctkIssuPlndReqO.getFILLER());

			// CustList
			int custListCount = 0;
			if (retrieveCpnAmtDctkIssuPlndReqO.getCUST_TYPE_CNT() != null)
				custListCount = IrtParseUtil.convStrToInt(retrieveCpnAmtDctkIssuPlndReqO.getCUST_TYPE_CNT().trim());
			for (int i = 0; i < custListCount; i++) {
				// CustList
				RetrieveCpnAmtDctkIssuPlndReqO.CustList custListO = new RetrieveCpnAmtDctkIssuPlndReqO.CustList();
				RetrieveCpnAmtDctkIssuPlndReqN.CustList custListN = new RetrieveCpnAmtDctkIssuPlndReqN.CustList();

				// 고객유형코드
				ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, custListO, "CUST_TYPE",
						socketRecvCharset);
				custListN.setCUST_TYPE(custListO.getCUST_TYPE());

				// 예비
				ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, custListO, "CUST_FILLER",
						socketRecvCharset);
				custListN.setCUST_FILLER(custListO.getCUST_FILLER());

				retrieveCpnAmtDctkIssuPlndReqO.getCustList().add(custListO);
				retrieveCpnAmtDctkIssuPlndReqN.getCustList().add(custListN);
			}

			// MDList
			int mdListCount = 0;
			if (retrieveCpnAmtDctkIssuPlndReqO.getMD_CD_CNT() != null)
				mdListCount = IrtParseUtil.convStrToInt(retrieveCpnAmtDctkIssuPlndReqO.getMD_CD_CNT().trim());
			for (int i = 0; i < mdListCount; i++) {
				// MDList
				RetrieveCpnAmtDctkIssuPlndReqO.MDList mdListO = new RetrieveCpnAmtDctkIssuPlndReqO.MDList();
				RetrieveCpnAmtDctkIssuPlndReqN.MDList mdListN = new RetrieveCpnAmtDctkIssuPlndReqN.MDList();

				// MD유형코드
				ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, mdListO, "MD_TYPE_CD",
						socketRecvCharset);
				mdListN.setMD_TYPE_CD(mdListO.getMD_TYPE_CD());

				// MD코드
				ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, mdListO, "MD_CD", socketRecvCharset);
				mdListN.setMD_CD(mdListO.getMD_CD());

				// MD별구매금액
				ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, mdListO, "MD_BUY_AMT",
						socketRecvCharset);
				mdListN.setMD_BUY_AMT(mdListO.getMD_BUY_AMT());

				// 할특적용여부
				ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, mdListO, "ADD_ENU_YN",
						socketRecvCharset);
				mdListN.setADD_ENU_YN(mdListO.getADD_ENU_YN());

				// 예비
				ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, mdListO, "MD_FILLER",
						socketRecvCharset);
				mdListN.setMD_FILLER(mdListO.getMD_FILLER());

				retrieveCpnAmtDctkIssuPlndReqO.getMdList().add(mdListO);
				retrieveCpnAmtDctkIssuPlndReqN.getMdList().add(mdListN);
			}

			// PayList
			int payListCount = 0;
			if (retrieveCpnAmtDctkIssuPlndReqO.getPAY_CNT() != null)
				payListCount = IrtParseUtil.convStrToInt(retrieveCpnAmtDctkIssuPlndReqO.getPAY_CNT().trim());
			for (int i = 0; i < payListCount; i++) {
				// PayList
				RetrieveCpnAmtDctkIssuPlndReqO.PayList payListO = new RetrieveCpnAmtDctkIssuPlndReqO.PayList();
				RetrieveCpnAmtDctkIssuPlndReqN.PayList payListN = new RetrieveCpnAmtDctkIssuPlndReqN.PayList();

				// 결제수단코드
				ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, payListO, "ENURI_PAY_CD",
						socketRecvCharset);
				payListN.setENURI_PAY_CD(payListO.getENURI_PAY_CD());

				// 결제수단별결제금액
				ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, payListO, "ENURI_PAY_AMT",
						socketRecvCharset);
				payListN.setENURI_PAY_AMT(payListO.getENURI_PAY_AMT());

				// 카드빈
				ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, payListO, "ENURI_BIN",
						socketRecvCharset);

				// 카드빈8
				ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, payListO, "ENURI_BIN8",
						socketRecvCharset);
				payListN.setENURI_BIN8(payListO.getENURI_BIN8());

				// 예비
				ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, payListO, "ENURI_FILLER",
						socketRecvCharset);
				payListN.setENURI_FILLER(payListO.getENURI_FILLER());

				retrieveCpnAmtDctkIssuPlndReqO.getPayList().add(payListO);
				retrieveCpnAmtDctkIssuPlndReqN.getPayList().add(payListN);
			}

			// [2] 신 전문 API 전송
			if (ret == 0) {
				IrtReqNJson irtReqNJson = new IrtReqNJson();
				// DMS Header --> Com Header 생성
				makeComHeaderFromDmsHeader(dmsComHeaderReq, irtReqNJson.getCOMM_HEADER());
				irtReqNJson.setIRT_REQ(retrieveCpnAmtDctkIssuPlndReqN);

				String jsonIrt = JsonUtil.getMapper().writeValueAsString(irtReqNJson);

				// debug
				log.info("신 전문 API 전송 :\n{}",
						JsonUtil.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(irtReqNJson));

				logMsg = String.format("%s : 신 전문 API 전송\n%s", "[LDI][DIRT0106][쿠폰 및 금액할인권 발급 예정 조회 8BIN]",
						JsonUtil.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(irtReqNJson));
				logMsgMap.put("logMsg", logMsg);
				insertTTRLOG_N(logMsgMap, ssMds, ssDms);

				Map<String, String> responseMap = new HashMap<String, String>();
				String severUrl = "", irtUrl = "";
				severUrl = ldiUrlProperties.getUrl().get("sever-url");
				irtUrl = ldiUrlProperties.getUrl().get("retrieveCpnAmtDctkIssuPlnd");

				// 전송 및 응답 수신
				if (httpClient.sendPostData(severUrl + irtUrl, jsonIrt, responseMap, httpRecvCharset, httpSendCharset,
						connectTimeout, serviceTimeout) == 0) {
					IrtResNJson irtResNJson = JsonUtil.getMapper().readValue(responseMap.get("response"),
							IrtResNJson.class);

					if (irtResNJson != null) {
						// debug
						log.info("recv 차세대 응답 : \n{}\n",
								JsonUtil.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(irtResNJson));

						logMsg = String.format("%s : recv 차세대 응답\n%s", "[LDI][DIRT0106][쿠폰 및 금액할인권 발급 예정 조회 8BIN]",
								JsonUtil.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(irtResNJson));
						logMsgMap.put("logMsg", logMsg);
						insertTTRLOG_N(logMsgMap, ssMds, ssDms);

						retrieveCpnAmtDctkIssuPlndResN = JsonUtil.getMapper().convertValue(irtResNJson.getIRT_RES(),
								RetrieveCpnAmtDctkIssuPlndResN.class);
						ret = 0;
					} else
						ret = -1;
				} else
					ret = -1;
			} else
				ret = -1;

			// [3] 신 전문 --> 구 전문 변환
			if (ret == 0) {
				int[] offSetRecv = { 0 };
				byte[] encHeaderBytes = null, dmsComHeaderBytes = null, retrieveCpnAmtDctkIssuPlndResOBytes = null,
						sendDmsHdBodyBytes = null, sendEncDmsHdBodyBytes = null, snedResBytes = null;
				EncHeader encHeaderRes = new EncHeader();
				DmsComHeader dmsComHeaderRes = new DmsComHeader();
				retrieveCpnAmtDctkIssuPlndResO = new RetrieveCpnAmtDctkIssuPlndResO();

				log.info("차세대 응답코드 : {}", retrieveCpnAmtDctkIssuPlndResN.getRES_CODE());

				// [3-1] 응답 전문 설정
				String sPRT_TOT_CNT = retrieveCpnAmtDctkIssuPlndResN.getPRT_TOT_CNT();
				if (sPRT_TOT_CNT != null)
					sPRT_TOT_CNT = "0";

				// 출력라인총개수
				retrieveCpnAmtDctkIssuPlndResO.setPRT_TOT_CNT(IrtParseUtil
						.getLpadFillZeroStringForTgFld(retrieveCpnAmtDctkIssuPlndResO, "PRT_TOT_CNT", sPRT_TOT_CNT));
				outByteStream.write(retrieveCpnAmtDctkIssuPlndResO.getPRT_TOT_CNT().getBytes(socketSendCharset));

				// 예비
				String filler = retrieveCpnAmtDctkIssuPlndResN.getFILLER();
				if (filler == null)
					filler = "";

				filler = IrtParseUtil.getFixLengthByteValueConvStr(filler.trim().getBytes(socketSendCharset),
						IrtParseUtil.getFieldSize(retrieveCpnAmtDctkIssuPlndResO, "FILLER"), EnmIrtFieldType.LSTR,
						socketSendCharset);
				retrieveCpnAmtDctkIssuPlndResO.setFILLER(filler);
				outByteStream.write(retrieveCpnAmtDctkIssuPlndResO.getFILLER().getBytes(socketSendCharset));

				// 정상일때만 데이터 들어온다
				if ("0000".equals(retrieveCpnAmtDctkIssuPlndResN.getRES_CODE())) {
					// PrintList
					for (RetrieveCpnAmtDctkIssuPlndResN.PrintList printListN : retrieveCpnAmtDctkIssuPlndResN
							.getPrintList()) {
						// PrintList
						RetrieveCpnAmtDctkIssuPlndResO.PrintList printListO = new RetrieveCpnAmtDctkIssuPlndResO.PrintList();

						// 순번
						printListO.setPRT_SEQ(IrtParseUtil.getLpadFillZeroStringForTgFld(printListO, "PRT_SEQ",
								printListN.getPRT_SEQ()));
						outByteStream.write(printListO.getPRT_SEQ().getBytes(socketSendCharset));

						// 제어문자
						printListO.setPRT_CTL(IrtParseUtil.getLpadFillSpaceStringForTgFld(printListO, "PRT_CTL",
								printListN.getPRT_CTL()));
						outByteStream.write(printListO.getPRT_CTL().getBytes(socketSendCharset));

						// 내용
						String sPRT_MSG = "";
						if (printListN.getPRT_MSG() != null)
							sPRT_MSG = IrtParseUtil.getFixLengthByteValueConvStr(
									printListN.getPRT_MSG().trim().getBytes(socketSendCharset),
									IrtParseUtil.getFieldSize(printListO, "PRT_MSG"), EnmIrtFieldType.LSTR,
									socketSendCharset);
						else
							sPRT_MSG = IrtParseUtil.getRpadFillSpaceStringForTgFld(printListO, "PRT_MSG", "");

						printListO.setPRT_MSG(sPRT_MSG);
						outByteStream.write(printListO.getPRT_MSG().getBytes(socketSendCharset));

						// 예비
						String sFILLER = "";
						if (printListN.getPRT_FILLER() != null)
							sFILLER = IrtParseUtil.getFixLengthByteValueConvStr(
									printListN.getPRT_FILLER().trim().getBytes(socketSendCharset),
									IrtParseUtil.getFieldSize(printListO, "PRT_FILLER"), EnmIrtFieldType.LSTR,
									socketSendCharset);
						else
							sFILLER = IrtParseUtil.getRpadFillSpaceStringForTgFld(printListO, "PRT_FILLER", "");

						printListO.setPRT_FILLER(sFILLER);
						outByteStream.write(printListO.getPRT_FILLER().getBytes(socketSendCharset));

						retrieveCpnAmtDctkIssuPlndResO.getPrintList().add(printListO);
					}
				}

				retrieveCpnAmtDctkIssuPlndResOBytes = outByteStream.toByteArray();
				outByteStream.close();
				outByteStream = null;

				// [3-2] DMS Header 설정
				IrtParseUtil.copySrcDmsHdToTgDmsHd(dmsComHeaderReq, dmsComHeaderRes);
				// [3-2-1] DMS Header : 전문길이 설정
				int dmsBodyLen = retrieveCpnAmtDctkIssuPlndResOBytes.length;
				int dmsHdMsgLen = dmsBodyLen + IEnumComIrt.DEF_DMS_COM_HEADER_LEN;
				String dmsHdMsgLenLpad = IrtParseUtil.getLpadFillZeroStringForTgFld(dmsComHeaderRes, "MSG_LEN",
						dmsHdMsgLen);
				dmsComHeaderRes.setMSG_LEN(dmsHdMsgLenLpad);

				// [3-2-2] DMS Header : 응답코드 설정
				dmsComHeaderRes.setRES_CODE(retrieveCpnAmtDctkIssuPlndResN.getRES_CODE());
				// [3-2-3] DMS Header : 응답 메시지 설정
				String resMsg = "";
				if (retrieveCpnAmtDctkIssuPlndResN.getRES_MSG() != null)
					resMsg = IrtParseUtil.getFixLengthByteValueConvStr(
							retrieveCpnAmtDctkIssuPlndResN.getRES_MSG().trim().getBytes(socketSendCharset),
							IrtParseUtil.getFieldSize(dmsComHeaderRes, "RES_MSG"), EnmIrtFieldType.LSTR,
							socketSendCharset);
				else
					resMsg = IrtParseUtil.getRpadFillSpaceStringForTgFld(dmsComHeaderRes, "RES_MSG", "");

				dmsComHeaderRes.setRES_MSG(resMsg);

				// [3-3] 응답 데이터 bytes 변환
				// [3-3-1] DMS Header bytes 변환
				dmsComHeaderBytes = new byte[IEnumComIrt.DEF_DMS_COM_HEADER_LEN];
				ret = IrtParseUtil.irtMapToBytes(dmsComHeaderBytes, offSetRecv, dmsComHeaderRes, socketSendCharset);

				// [3-3-2] DMS Header + Body Bytes Concat
				if (ret == 0) {
					if ((sendDmsHdBodyBytes = IrtParseUtil.byteArraysConcat(dmsComHeaderBytes,
							retrieveCpnAmtDctkIssuPlndResOBytes)) == null)
						ret = -1;
				} else
					ret = -1;

				// [3-4] 응답 데이터 Body 암호화
				if (ret == 0) {
					// debug
					log.info("Send POS DMS Header : \n{}\n",
							JsonUtil.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(dmsComHeaderRes));
					// debug
					log.info("Send POS Body : \n{}\n", JsonUtil.getMapper().writerWithDefaultPrettyPrinter()
							.writeValueAsString(retrieveCpnAmtDctkIssuPlndResO));

					logMsg = String.format("%s : Send POS Body\n%s", "[LDI][DIRT0106][쿠폰 및 금액할인권 발급 예정 조회 8BIN]",
							JsonUtil.getMapper().writerWithDefaultPrettyPrinter()
									.writeValueAsString(retrieveCpnAmtDctkIssuPlndResO));
					logMsgMap.put("logMsg", logMsg);
					insertTTRLOG_N(logMsgMap, ssMds, ssDms);

					log.info("pos 응답전문 길이 : [{}], body : [{}]", dmsHdMsgLen, new String(sendDmsHdBodyBytes));

					aes128CipherUtil = new Aes128CipherUtil(socketSendCharset, encHeaderReq.getPOSNO().getBytes(),
							encHeaderReq.getTRANNO().getBytes(), dmsHdMsgLenLpad.getBytes());

					sendEncDmsHdBodyBytes = aes128CipherUtil.encryptAes(sendDmsHdBodyBytes);
				} else
					ret = -1;

				// [3-5] 암호화 헤더 설정
				int totLen = 0;
				if (ret == 0) {
					IrtParseUtil.copySrcEncHdToTgEncHd(encHeaderReq, encHeaderRes);
					totLen = sendEncDmsHdBodyBytes.length + IEnumComIrt.DEF_ENC_HEADER_LEN;
					String totLenLpad = IrtParseUtil.getLpadFillZeroStringForTgFld(encHeaderRes, "TOTLEN", totLen);

					// 전체전문길이 설정
					encHeaderRes.setTOTLEN(totLenLpad);
					// 원문길이 설정
					encHeaderRes.setMSGVLI(dmsHdMsgLenLpad);

					offSetRecv[0] = 0;
					encHeaderBytes = new byte[IEnumComIrt.DEF_ENC_HEADER_LEN];
					ret = IrtParseUtil.irtMapToBytes(encHeaderBytes, offSetRecv, encHeaderRes, socketSendCharset);
				} else
					ret = -1;

				// [3-6] 응답 전문 전송
				if (ret == 0) {
					if ((snedResBytes = IrtParseUtil.byteArraysConcat(encHeaderBytes, sendEncDmsHdBodyBytes)) != null) {
						ret = 0;
						if (socketUtil.tcp_send_b(bos, client, snedResBytes, totLen) == 0) {
							log.debug("send Reply Ok...");
						} else {
							ret = -2;
							log.error("send Reply Error...");
						}
					} else
						ret = -1;
				} else
					ret = -1;
			} else
				ret = -1;

			if (ret != 0) {
				logMsg = String.format("%s : ERROR", "[LDI][DIRT0106][쿠폰 및 금액할인권 발급 예정 조회 8BIN]");
				logMsgMap.put("logMsg", logMsg);
				insertTTRLOG_N(logMsgMap, ssMds, ssDms);
			}
		} catch (Exception e) {
			log.error("DMS 쿠폰 및 금액할인권 발급 예정 조회 8BIN Exception. {}", e);
			ret = -1;
		} finally {
			// 응답 전문 전송이 정상 처리 되지 않은 모든 에러 발생시 응답 처리 (단 전송 타임아웃이 아닐 때)
			try {
				if (outByteStream != null)
					outByteStream.close();

				if (ret != 0 && ret != -2) {
					sendReplyUnhandledError(scsDaemon, client, bis, bos, encHeaderReq, dmsComHeaderReq);
				}
			} catch (Exception e) {
				log.error("DMS 쿠폰 및 금액할인권 발급 예정 조회 8BIN finally Exception. {}", e);
			}
		}

		return ret;
	}

	/**
	 * <pre>
	 * DMS 쿠폰 및 금액할인권 사용 확정 조회
	 * </pre>
	 *
	 * @param scsDaemon
	 * @param client
	 * @param startTime
	 * @param remainTimeOut
	 * @param bis
	 * @param bos
	 * @param encHeaderReq
	 * @param dmsComHeaderReq
	 * @param recvIrtReqData
	 * @param offSet
	 * @return
	 */
	public int msgIdDIRT0105(SCSDaemon scsDaemon, Socket client, BufferedInputStream bis, BufferedOutputStream bos,
			EncHeader encHeaderReq, DmsComHeader dmsComHeaderReq, byte[] recvIrtReqData, int[] offSet, SqlSession ssMds,
			SqlSession ssDms) {
		int ret = -1;
		Aes128CipherUtil aes128CipherUtil = null;

		// 구 전문 요청/응답
		RetrieveCpnAmtDctkUsgCfmtReqO retrieveCpnAmtDctkUsgCfmtReqO = null;
		RetrieveCpnAmtDctkUsgCfmtResO retrieveCpnAmtDctkUsgCfmtResO = null;

		// 신 전문 요청/응답
		RetrieveCpnAmtDctkUsgCfmtReqN retrieveCpnAmtDctkUsgCfmtReqN = null;
		RetrieveCpnAmtDctkUsgCfmtResN retrieveCpnAmtDctkUsgCfmtResN = null;

		// 구전문 Object --> Bytes 변환
		ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();

		try {
			// [1] 구 전문 --> 신 전문 변환
			// [1-1] 구 전문 요청 Bytes --> Object 변경
			retrieveCpnAmtDctkUsgCfmtReqO = new RetrieveCpnAmtDctkUsgCfmtReqO();
			retrieveCpnAmtDctkUsgCfmtReqN = new RetrieveCpnAmtDctkUsgCfmtReqN();

			// 전문 전체를 한번에 리플렉션 할 수 없음(리스트 반복이 항목 값으로 되어 있기 때문에 반복 값을 읽어서 처리)
			// 전문Type
			retrieveCpnAmtDctkUsgCfmtReqN.setIrtType(IEnumComIrt.EnmIrtTypeWrap.Irt_14.getIrtTypeN());

			// 고객번호
			ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, retrieveCpnAmtDctkUsgCfmtReqO, "CUST_ID",
					socketRecvCharset);
			retrieveCpnAmtDctkUsgCfmtReqN.setCUST_ID(retrieveCpnAmtDctkUsgCfmtReqO.getCUST_ID());

			// IRT ID
			ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, retrieveCpnAmtDctkUsgCfmtReqO, "IRT_ID",
					socketRecvCharset);
			retrieveCpnAmtDctkUsgCfmtReqN.setIRT_ID(retrieveCpnAmtDctkUsgCfmtReqO.getIRT_ID());

			// 사용 쿠폰/금액할인권 개수
			ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, retrieveCpnAmtDctkUsgCfmtReqO, "CP_CNT",
					socketRecvCharset);
			retrieveCpnAmtDctkUsgCfmtReqN.setCP_CNT(retrieveCpnAmtDctkUsgCfmtReqO.getCP_CNT());

			// 예비
			ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, retrieveCpnAmtDctkUsgCfmtReqO, "FILLER",
					socketRecvCharset);
			retrieveCpnAmtDctkUsgCfmtReqN.setFILLER(retrieveCpnAmtDctkUsgCfmtReqO.getFILLER());

			// CouponList
			Map<String, Object> paramMap = new HashMap<>();
			int couponListCount = 0;
			if (retrieveCpnAmtDctkUsgCfmtReqO.getCP_CNT() != null)
				couponListCount = IrtParseUtil.convStrToInt(retrieveCpnAmtDctkUsgCfmtReqO.getCP_CNT().trim());
			for (int i = 0; i < couponListCount; i++) {
				RetrieveCpnAmtDctkUsgCfmtReqO.CouponList couponListO = new RetrieveCpnAmtDctkUsgCfmtReqO.CouponList();
				RetrieveCpnAmtDctkUsgCfmtReqN.CouponList couponListN = new RetrieveCpnAmtDctkUsgCfmtReqN.CouponList();

				// 점포코드
				ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, couponListO, "CP_STR_CD",
						socketRecvCharset);

				couponListN.setCP_STR_CD(IrtParseUtil.getRpadFillSpaceStringConvIntForTgFld(couponListN, "CP_STR_CD",
						couponListO.getCP_STR_CD()));

				// 행사년월
				ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, couponListO, "CP_YM",
						socketRecvCharset);
				couponListN.setCP_YM(couponListO.getCP_YM());

				// 행사번호 (9 -> 6) SUBSTR(CP_EVT_NO,4)
				ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, couponListO, "CP_EVT_NO",
						socketRecvCharset);

				String sEvnRegStrCd = couponListN.getCP_STR_CD().substring(0, 2);
				String sCP_EVT_NO = couponListO.getCP_EVT_NO();

				if (sCP_EVT_NO != null) {
					if ("999912".equals(couponListO.getCP_YM()))
						sCP_EVT_NO = IrtParseUtil.getLpadFillZeroString(sCP_EVT_NO, 9);
					else {
						if ("7".equals(sCP_EVT_NO.substring(0, 1))) {
							sCP_EVT_NO = "000" + sCP_EVT_NO;
						} else {
							sCP_EVT_NO = "D" + sEvnRegStrCd + sCP_EVT_NO;
						}
					}
				} else
					sCP_EVT_NO = "";

				couponListN.setCP_EVT_NO(
						IrtParseUtil.getRpadFillSpaceStringForTgFld(couponListN, "CP_EVT_NO", sCP_EVT_NO));

				// 쿠폰코드
				String sCP_YM = couponListN.getCP_YM();
				if (sCP_YM == null)
					sCP_YM = "";
				String sCP_STR_CD = couponListN.getCP_STR_CD();
				if (sCP_STR_CD == null)
					sCP_STR_CD = "";

				paramMap.clear();
				paramMap.put("cpYm", sCP_YM);
				paramMap.put("cpStrCd", sCP_STR_CD);
				paramMap.put("cpEvtNo", sCP_EVT_NO);
				String sCP_CD = "";

				if ("999912".equals(couponListO.getCP_YM()))
					sCP_CD = "99999999999999999";
				else
					sCP_CD = selectDmsItemCpCd(paramMap, ssMds, ssDms);

				log.info("점포코드 : [{}], 행사년월 : [{}], 행사번호 : [{}], 쿠폰코드(조회) : [{}]", sCP_STR_CD, sCP_YM, sCP_EVT_NO,
						sCP_CD);
				couponListN.setCP_CD(IrtParseUtil.getLpadFillSpaceStringForTgFld(couponListN, "CP_CD", sCP_CD));

				// 쿠폰/할인권발급번호
				ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, couponListO, "CP_ISS_NO",
						socketRecvCharset);
				String custId = retrieveCpnAmtDctkUsgCfmtReqO.getCUST_ID();
				if (custId != null)
					custId = custId.trim();
				else
					custId = "";
				paramMap.clear();
				paramMap.put("custId", custId);
				int nCP_ISS_NO = IrtParseUtil.convStrToInt(couponListO.getCP_ISS_NO());
				log.info("nCP_ISS_NO : {}", nCP_ISS_NO);
				paramMap.put("cpIssNo", nCP_ISS_NO);

				if ("999912".equals(couponListO.getCP_YM()))
					couponListN.setCP_ISS_NO(IrtParseUtil.getLpadFillZeroStringForTgFld(couponListN, "CP_ISS_NO",
							couponListO.getCP_ISS_NO()));
				else
					couponListN.setCP_ISS_NO(selectDpstrEvnCpnNo(paramMap, ssMds, ssDms));
				log.info("CP_ISS_NO(조회) : [{}]", couponListN.getCP_ISS_NO());

				// 쿠폰/할인권번호
				ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, couponListO, "CP_NO",
						socketRecvCharset);

				// 쿠폰구분코드
				ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, couponListO, "CP_KIND_CD",
						socketRecvCharset);
				couponListN.setCP_KIND_NO(couponListO.getCP_KIND_CD());

				// 예비
				ret = IrtParseUtil.irtMapToObjFromObjFld(recvIrtReqData, offSet, couponListO, "CP_FILLER",
						socketRecvCharset);
				couponListN.setCP_FILLER(couponListO.getCP_FILLER());

				retrieveCpnAmtDctkUsgCfmtReqO.getCouponList().add(couponListO);
				retrieveCpnAmtDctkUsgCfmtReqN.getCouponList().add(couponListN);
			}

			// [2] 신 전문 API 전송
			if (ret == 0) {
				IrtReqNJson irtReqNJson = new IrtReqNJson();
				// DMS Header --> Com Header 생성
				makeComHeaderFromDmsHeader(dmsComHeaderReq, irtReqNJson.getCOMM_HEADER());
				irtReqNJson.setIRT_REQ(retrieveCpnAmtDctkUsgCfmtReqN);

				String jsonIrt = JsonUtil.getMapper().writeValueAsString(irtReqNJson);

				// debug
				log.info("신 전문 API 전송 :\n{}",
						JsonUtil.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(irtReqNJson));

				logMsg = String.format("%s : 신 전문 API 전송\n%s", "[LDI][DIRT0105][쿠폰 및 금액할인권 사용 확정 조회]",
						JsonUtil.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(irtReqNJson));
				logMsgMap.put("logMsg", logMsg);
				insertTTRLOG_N(logMsgMap, ssMds, ssDms);

				Map<String, String> responseMap = new HashMap<String, String>();
				String severUrl = "", irtUrl = "";
				severUrl = ldiUrlProperties.getUrl().get("sever-url");
				irtUrl = ldiUrlProperties.getUrl().get("retrieveCpnAmtDctkUsgCfmt");

				// 전송 및 응답 수신
				if (httpClient.sendPostData(severUrl + irtUrl, jsonIrt, responseMap, httpRecvCharset, httpSendCharset,
						connectTimeout, serviceTimeout) == 0) {
					IrtResNJson irtResNJson = JsonUtil.getMapper().readValue(responseMap.get("response"),
							IrtResNJson.class);

					if (irtResNJson != null) {
						// debug
						log.info("recv 차세대 응답 : \n{}\n",
								JsonUtil.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(irtResNJson));

						logMsg = String.format("%s : recv 차세대 응답\n%s", "[LDI][DIRT0105][쿠폰 및 금액할인권 사용 확정 조회]",
								JsonUtil.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(irtResNJson));
						logMsgMap.put("logMsg", logMsg);
						insertTTRLOG_N(logMsgMap, ssMds, ssDms);

						retrieveCpnAmtDctkUsgCfmtResN = JsonUtil.getMapper().convertValue(irtResNJson.getIRT_RES(),
								RetrieveCpnAmtDctkUsgCfmtResN.class);
						ret = 0;
					} else
						ret = -1;
				} else
					ret = -1;
			} else
				ret = -1;

			// [3] 신 전문 --> 구 전문 변환
			if (ret == 0) {
				int[] offSetRecv = { 0 };
				byte[] encHeaderBytes = null, dmsComHeaderBytes = null, retrieveCpnAmtDctkUsgCfmtResOBytes = null,
						sendDmsHdBodyBytes = null, sendEncDmsHdBodyBytes = null, snedResBytes = null;
				EncHeader encHeaderRes = new EncHeader();
				DmsComHeader dmsComHeaderRes = new DmsComHeader();
				retrieveCpnAmtDctkUsgCfmtResO = new RetrieveCpnAmtDctkUsgCfmtResO();

				// [3-1] 응답 전문 설정
				outByteStream.close();
				outByteStream = null;

				// [3-2] DMS Header 설정
				IrtParseUtil.copySrcDmsHdToTgDmsHd(dmsComHeaderReq, dmsComHeaderRes);
				// [3-2-1] DMS Header : 전문길이 설정
				int dmsBodyLen = 0;
				int dmsHdMsgLen = dmsBodyLen + IEnumComIrt.DEF_DMS_COM_HEADER_LEN;
				String dmsHdMsgLenLpad = IrtParseUtil.getLpadFillZeroStringForTgFld(dmsComHeaderRes, "MSG_LEN",
						dmsHdMsgLen);
				dmsComHeaderRes.setMSG_LEN(dmsHdMsgLenLpad);

				// [3-2-2] DMS Header : 응답코드 설정
				dmsComHeaderRes.setRES_CODE(retrieveCpnAmtDctkUsgCfmtResN.getRES_CODE());
				// [3-2-3] DMS Header : 응답 메시지 설정
				String resMsg = "";
				if (retrieveCpnAmtDctkUsgCfmtResN.getRES_MSG() != null)
					resMsg = IrtParseUtil.getFixLengthByteValueConvStr(
							retrieveCpnAmtDctkUsgCfmtResN.getRES_MSG().trim().getBytes(socketSendCharset),
							IrtParseUtil.getFieldSize(dmsComHeaderRes, "RES_MSG"), EnmIrtFieldType.LSTR,
							socketSendCharset);
				else
					resMsg = IrtParseUtil.getRpadFillSpaceStringForTgFld(dmsComHeaderRes, "RES_MSG", "");

				dmsComHeaderRes.setRES_MSG(resMsg);

				// [3-3] 응답 데이터 bytes 변환
				// [3-3-1] DMS Header bytes 변환
				dmsComHeaderBytes = new byte[IEnumComIrt.DEF_DMS_COM_HEADER_LEN];
				ret = IrtParseUtil.irtMapToBytes(dmsComHeaderBytes, offSetRecv, dmsComHeaderRes, socketSendCharset);

				// [3-3-2] DMS Header + Body Bytes Concat
				if (ret == 0) {
					if ((sendDmsHdBodyBytes = IrtParseUtil.byteArraysConcat(dmsComHeaderBytes)) == null)
						ret = -1;
				} else
					ret = -1;

				// [3-4] 응답 데이터 Body 암호화
				if (ret == 0) {
					log.info("Send POS DMS Header : \n{}\n",
							JsonUtil.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(dmsComHeaderRes));

					logMsg = String.format("%s : Send POS DMS Header\n%s", "[LDI][DIRT0105][쿠폰 및 금액할인권 사용 확정 조회]",
							JsonUtil.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(dmsComHeaderRes));
					logMsgMap.put("logMsg", logMsg);
					insertTTRLOG_N(logMsgMap, ssMds, ssDms);

					log.info("pos 응답전문 길이 : [{}], body : [{}]", dmsHdMsgLen, new String(sendDmsHdBodyBytes));

					aes128CipherUtil = new Aes128CipherUtil(socketSendCharset, encHeaderReq.getPOSNO().getBytes(),
							encHeaderReq.getTRANNO().getBytes(), dmsHdMsgLenLpad.getBytes());

					sendEncDmsHdBodyBytes = aes128CipherUtil.encryptAes(sendDmsHdBodyBytes);
				} else
					ret = -1;

				// [3-5] 암호화 헤더 설정
				int totLen = 0;
				if (ret == 0) {
					IrtParseUtil.copySrcEncHdToTgEncHd(encHeaderReq, encHeaderRes);
					totLen = sendEncDmsHdBodyBytes.length + IEnumComIrt.DEF_ENC_HEADER_LEN;
					String totLenLpad = IrtParseUtil.getLpadFillZeroStringForTgFld(encHeaderRes, "TOTLEN", totLen);

					// 전체전문길이 설정
					encHeaderRes.setTOTLEN(totLenLpad);
					// 원문길이 설정
					encHeaderRes.setMSGVLI(dmsHdMsgLenLpad);

					offSetRecv[0] = 0;
					encHeaderBytes = new byte[IEnumComIrt.DEF_ENC_HEADER_LEN];
					ret = IrtParseUtil.irtMapToBytes(encHeaderBytes, offSetRecv, encHeaderRes, socketSendCharset);
				} else
					ret = -1;

				// [3-6] 응답 전문 전송
				if (ret == 0) {
					if ((snedResBytes = IrtParseUtil.byteArraysConcat(encHeaderBytes, sendEncDmsHdBodyBytes)) != null) {
						ret = 0;
						if (socketUtil.tcp_send_b(bos, client, snedResBytes, totLen) == 0) {
							log.debug("send Reply Ok...");
						} else {
							ret = -2;
							log.error("send Reply Error...");
						}
					} else
						ret = -1;
				} else
					ret = -1;
			} else
				ret = -1;

			if (ret != 0) {
				logMsg = String.format("%s : ERROR", "[LDI][DIRT0105][쿠폰 및 금액할인권 사용 확정 조회]");
				logMsgMap.put("logMsg", logMsg);
				insertTTRLOG_N(logMsgMap, ssMds, ssDms);
			}
		} catch (Exception e) {
			log.error("DMS 쿠폰 및 금액할인권 사용 확정 조회 Exception. {}", e);
			ret = -1;
		} finally {
			// 응답 전문 전송이 정상 처리 되지 않은 모든 에러 발생시 응답 처리 (단 전송 타임아웃이 아닐 때)
			try {
				if (outByteStream != null)
					outByteStream.close();

				if (ret != 0 && ret != -2) {
					sendReplyUnhandledError(scsDaemon, client, bis, bos, encHeaderReq, dmsComHeaderReq);
				}
			} catch (Exception e) {
				log.error("DMS 쿠폰 및 금액할인권 사용 확정 조회 finally Exception. {}", e);
			}
		}

		return ret;
	}

	/**
	 * <pre>
	 * Make ComHeader From DmsHeader
	 * </pre>
	 *
	 * @param srcDmsComHeader
	 * @param tgComHeader
	 * @throws Exception
	 */
	public void makeComHeaderFromDmsHeader(DmsComHeader srcDmsComHeader, ComHeader tgComHeader) throws Exception {
		// 메시지길이
		// tgComHeader.setMsgLength(srcDmsComHeader.getMSG_LEN());
		// 메시지경로
		tgComHeader.setMsgPath("PN");
		// 메시지타입 01: 트란, 03: IRT
		tgComHeader.setMsgType("03");
		// 메시지종류 Default : 00
		tgComHeader.setMsgKind("00");
		// 영업일자
		tgComHeader.setSaleDate(srcDmsComHeader.getSALE_DATE());
		// 점코드
		tgComHeader.setStoreNo(IrtParseUtil.getRpadFillSpaceStringForTgFld(tgComHeader, "StoreNo",
				srcDmsComHeader.getSTORE_CD().trim()));
		// POS번호
		tgComHeader.setPosNo(srcDmsComHeader.getPOS_NO());
		// 거래번호
		tgComHeader.setTranNo(srcDmsComHeader.getTRAN_NO());
		// 시스템날짜
		tgComHeader.setSendDate(srcDmsComHeader.getCOM_DATE());
		// 시스템시간
		tgComHeader.setSendTime(srcDmsComHeader.getCOM_TIME());
		// 新점포코드
		tgComHeader.setExtStoreNo(srcDmsComHeader.getSTORE_CD());
		// 교육모드
		tgComHeader.setTrainMode(" ");
		// 신/구전문구분
		tgComHeader.setVersionFlag("B");
		// 사용자 정보
		tgComHeader.setUserInfo(IrtParseUtil.getLpadFillSpaceStringForTgFld(tgComHeader, "UserInfo", ""));
		// 응답코드
		tgComHeader.setReplyCode("0000");
	}

	/**
	 * <pre>
	 * 응답 전문 전송이 정상 처리 되지 않은 모든 에러 발생시 응답 처리 (단 전송 타임아웃이 아닐 때)
	 * </pre>
	 *
	 * @param dmsComHeaderReq
	 * @param bos
	 * @param client
	 * @param remainTimeOut
	 */
	public void sendReplyUnhandledError(SCSDaemon scsDaemon, Socket client, BufferedInputStream bis,
			BufferedOutputStream bos, EncHeader encHeaderReq, DmsComHeader dmsComHeaderReq) {
		int ret = -1;
		int[] offSet = { 0 };
		try {
			// [1] DMS Header 설정
			DmsComHeader dmsComHeaderRes = new DmsComHeader();
			IrtParseUtil.copySrcDmsHdToTgDmsHd(dmsComHeaderReq, dmsComHeaderRes);

			// DMS Header : 전문길이 설정
			int dmsBodyLen = 0;
			int dmsHdMsgLen = dmsBodyLen + IEnumComIrt.DEF_DMS_COM_HEADER_LEN;
			String dmsHdMsgLenLpad = IrtParseUtil.getLpadFillZeroStringForTgFld(dmsComHeaderRes, "MSG_LEN",
					dmsHdMsgLen);
			dmsComHeaderRes.setMSG_LEN(dmsHdMsgLenLpad);
			// DMS Header : 응답코드 설정
			dmsComHeaderRes.setRES_CODE(IEnumComIrt.EnmIrtDmsResCode.C9888.getResCode());
			// DMS Header : 응답 메시지 설정
			String resMsg = "";
			if (IEnumComIrt.EnmIrtDmsResCode.C9888.getResMsg() != null)
				resMsg = IrtParseUtil.getFixLengthByteValueConvStr(
						IEnumComIrt.EnmIrtDmsResCode.C9888.getResMsg().trim().getBytes(socketSendCharset),
						IrtParseUtil.getFieldSize(dmsComHeaderRes, "RES_MSG"), EnmIrtFieldType.LSTR, socketSendCharset);
			else
				resMsg = IrtParseUtil.getRpadFillSpaceStringForTgFld(dmsComHeaderRes, "RES_MSG", "");

			dmsComHeaderRes.setRES_MSG(resMsg);

			// [2] DMS Header bytes 변환
			byte[] dmsComHeaderBytes = new byte[IEnumComIrt.DEF_DMS_COM_HEADER_LEN];
			IrtParseUtil.irtMapToBytes(dmsComHeaderBytes, offSet, dmsComHeaderRes, socketSendCharset);

			// [3] DMS 수기 에누리 권한 조회 응답 Body bytes 변환

			// [4] 응답 데이터 암호화
			Aes128CipherUtil aes128CipherUtil = new Aes128CipherUtil(socketSendCharset,
					encHeaderReq.getPOSNO().getBytes(), encHeaderReq.getTRANNO().getBytes(),
					dmsHdMsgLenLpad.getBytes());

			byte[] sendEncDmsHdBodyBytes = aes128CipherUtil.encryptAes(dmsComHeaderBytes);

			// [5] 암호화 헤더 설정
			EncHeader encHeaderRes = new EncHeader();
			IrtParseUtil.copySrcEncHdToTgEncHd(encHeaderReq, encHeaderRes);

			int totLen = sendEncDmsHdBodyBytes.length + IEnumComIrt.DEF_ENC_HEADER_LEN;
			String totLenLpad = IrtParseUtil.getLpadFillZeroStringForTgFld(encHeaderRes, "TOTLEN", totLen);

			// 전체전문길이 설정
			encHeaderRes.setTOTLEN(totLenLpad);
			// 원문길이 설정
			encHeaderRes.setMSGVLI(dmsHdMsgLenLpad);

			// [6] 암호화 Header bytes 변환
			offSet[0] = 0;
			byte[] encHeaderBytes = new byte[IEnumComIrt.DEF_ENC_HEADER_LEN];
			IrtParseUtil.irtMapToBytes(encHeaderBytes, offSet, encHeaderRes, socketSendCharset);

			// [7] 응답 전문 전송
			byte[] snedResBytes = null;
			if ((snedResBytes = IrtParseUtil.byteArraysConcat(encHeaderBytes, sendEncDmsHdBodyBytes)) != null) {
				if (socketUtil.tcp_send_b(bos, client, snedResBytes, totLen) == 0) {
					ret = 0;
				}
			}
		} catch (Exception e) {
			log.error("sendReplyUnhandledError Exception. {}", e);
		} finally {
			if (ret == 0)
				log.debug("sendReplyUnhandledError Ok...");
			else
				log.error("sendReplyUnhandledError Error...");
		}
	}

	/**
	 * <pre>
	 * MD 코드 조회
	 * </pre>
	 *
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public String selectMdCd(Map<String, Object> paramMap, SqlSession ssMds, SqlSession ssDms) throws Exception {
		String mdCd = "";

		mdCd = ssMds.selectOne("ldi.Ldi0001.selectMdCd", paramMap);

		log.debug("selectMdCd -- mdCd : [{}]", mdCd);
		if (mdCd == null) {
			log.info("\nEselectMdCd -- MD코드 미존재 -- strCd:[{}], shopCd:[{}]", paramMap.get("strCd"),
					paramMap.get("shopCd"));
			// throw new Exception("EselectMdCd -- MD코드 미존재 Exception!!");
			mdCd = "";
		} else {
			log.info("SselectMdCd -- MD코드 -- strCd:[{}], shopCd:[{}], mdCd:[{}]", paramMap.get("strCd"),
					paramMap.get("shopCd"), mdCd);
		}

		return mdCd;
	}

	/**
	 * <pre>
	 * 백화점행사쿠폰번호 조회
	 * </pre>
	 *
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public String selectDpstrEvnCpnNo(Map<String, Object> paramMap, SqlSession ssMds, SqlSession ssDms)
			throws Exception {
		String dpstrEvnCpnNo = "";

		dpstrEvnCpnNo = ssDms.selectOne("ldi.Ldi0001.selectDpstrEvnCpnNo", paramMap);

		log.debug("selectDpstrEvnCpnNo -- dpstrEvnCpnNo : [{}]", dpstrEvnCpnNo);
		if (dpstrEvnCpnNo == null) {
			log.info("\nEselectDpstrEvnCpnNo -- 백화점행사쿠폰번호 미존재 -- custId:[{}], cpIssNo:[{}]", paramMap.get("custId"),
					paramMap.get("cpIssNo"));
			// throw new Exception("EselectDpstrEvnCpnNo -- 백화점행사쿠폰번호 미존재
			// Exception!!");
			dpstrEvnCpnNo = "";
		} else {
			log.info("SselectDpstrEvnCpnNo -- 백화점행사쿠폰번호 -- custId:[{}], cpIssNo:[{}], dpstrEvnCpnNo:[{}]",
					paramMap.get("custId"), paramMap.get("cpIssNo"), dpstrEvnCpnNo);
		}

		return dpstrEvnCpnNo;
	}

	/**
	 * <pre>
	 * 쿠폰발급일련번호 조회
	 * </pre>
	 *
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public String selectCpnIssuSno(Map<String, Object> paramMap, SqlSession ssMds, SqlSession ssDms) throws Exception {
		String cpnIssuSno = "";

		cpnIssuSno = ssDms.selectOne("ldi.Ldi0001.selectCpnIssuSno", paramMap);

		log.debug("selectCpnIssuSno -- cpnIssuSno : [{}]", cpnIssuSno);
		if (cpnIssuSno == null) {
			log.info("\nEselectCpnIssuSno -- 쿠폰발급일련번호 미존재 -- custId:[{}], cpIssNo:[{}]", paramMap.get("custId"),
					paramMap.get("cpIssNo"));
			// throw new Exception("EselectCpnIssuSno -- 쿠폰발급일련번호 미존재
			// Exception!!");
			cpnIssuSno = "";
		} else {
			log.info("SselectCpnIssuSno -- 쿠폰발급일련번호 -- custId:[{}], cpIssNo:[{}], cpnIssuSno:[{}]",
					paramMap.get("custId"), paramMap.get("cpIssNo"), cpnIssuSno);
		}

		return cpnIssuSno;
	}

	public String selectDmsItemCpCd(Map<String, Object> paramMap, SqlSession ssMds, SqlSession ssDms) throws Exception {
		String cpCd = "";

		cpCd = ssDms.selectOne("ldi.Ldi0001.selectDmsItemCpCd", paramMap);

		log.debug("selectDmsItemCpCd -- selectDmsItemCpCd -- cpCd :[{}]", cpCd);
		if (cpCd == null) {
			log.info("\nEselectDmsItemCpCd -- 쿠폰코드 미존재 -- enuriEvtYm:[{}], enuriStrCd:[{}], enuriEvtNo:[{}]",
					paramMap.get("enuriEvtYm"), paramMap.get("enuriStrCd"), paramMap.get("enuriEvtNo"));

			cpCd = "";
		} else {
			log.info("SselectDmsItemCpCd -- 쿠폰코드 -- enuriEvtYm:[{}], enuriStrCd:[{}], enuriEvtNo:[{}]",
					paramMap.get("enuriEvtYm"), paramMap.get("enuriStrCd"), paramMap.get("enuriEvtNo"));
		}

		return cpCd;
	}

	public void insertTTRLOG_N(Map<String, Object> paramMap, SqlSession ssMds, SqlSession ssDms) throws Exception {
		try {
			ssMds.insert("ldi.Ldi0001.insertTTRLOG_N", paramMap);
			ssMds.commit();
		} catch (Exception e) {
			log.info("insertTTRLOG_N Exception. {}", e.toString());
			ssMds.rollback();
		}
	}
}
