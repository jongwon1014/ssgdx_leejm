/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.ldi.model.oldirt
 * @fileName : RetrieveCstTgetEvnCpnReqO.java
 * @author : q93m9k
 * @date : 2024.01.23
 * @description :
 * 
 * COPYRIGHT ©2024 SHINSEGAE. ALL RIGHTS RESERVED.
 * 
 * <pre>
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024.01.23       q93m9k              최초 생성
 * ===========================================================
 * </pre>
 */
package com.shinsegae.ssgdx.ldi.model.oldirt;

import java.util.ArrayList;
import java.util.List;

import com.shinsegae.ssgdx.ldi.util.IIrtFldMeta;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * DMS 고객 대상행사 및 쿠폰조회
 * </pre>
 * 
 * @author : q93m9k
 * @version : 2024.01.23
 * @see :
 */
@Getter
@Setter
public class RetrieveCstTgetEvnCpnReqO {

    @IIrtFldMeta(size = 9)
    @JsonProperty("CUST_ID")
    private String CUST_ID; // 고객번호

    @IIrtFldMeta(size = 10)
    @JsonProperty("BUY_AMT")
    private String BUY_AMT; // 총구매금액

    @IIrtFldMeta(size = 3)
    @JsonProperty("SPOINT_CUP_CNT")
    private String SPOINT_CUP_CNT; // 신세계포인트보유쿠폰개수

    @IIrtFldMeta(size = 3)
    @JsonProperty("CUST_TYPE_CNT")
    private String CUST_TYPE_CNT; // 고객유형코드수

    @IIrtFldMeta(size = 3)
    @JsonProperty("MD_CD_CNT")
    private String MD_CD_CNT; // MD코드수

    @IIrtFldMeta(size = 2)
    @JsonProperty("RT_CP_ISS_CD")
    private String RT_CP_ISS_CD; // 실시간금액할인권

    @IIrtFldMeta(size = 1)
    @JsonProperty("SMP_CLUB_YN")
    private String SMP_CLUB_YN; // 스마일클럽여부

    @IIrtFldMeta(size = 20)
    @JsonProperty("SMP_CUST_ID")
    private String SMP_CUST_ID; // 스마일페이회원번호

    @IIrtFldMeta(size = 4)
    @JsonProperty("DETAIL_PAY_CD")
    private String DETAIL_PAY_CD; // 세부결제코드

    @IIrtFldMeta(size = 72)
    @JsonProperty("FILLER")
    private String FILLER; // 예비

    @JsonProperty("CustTypeList")
    private List<CustTypeList> custTypeList = new ArrayList<CustTypeList>(); // CustTypeList

    @JsonProperty("MDList")
    private List<MDList> mdList = new ArrayList<MDList>(); // MDList

    // CustTypeList
    @Getter
    @Setter
    public static class CustTypeList {

        @IIrtFldMeta(size = 2)
        @JsonProperty("CUST_TYPE")
        private String CUST_TYPE; // 고객유형코드
    }

    // MDList
    @Getter
    @Setter
    public static class MDList {

        @IIrtFldMeta(size = 4)
        @JsonProperty("MD_TYPE_CD")
        private String MD_TYPE_CD; // MD유형코드

        @IIrtFldMeta(size = 6)
        @JsonProperty("MD_CD")
        private String MD_CD; // MD코드

        @IIrtFldMeta(size = 10)
        @JsonProperty("MD_BUY_AMT")
        private String MD_BUY_AMT; // MD별구매금액

        @IIrtFldMeta(size = 1)
        @JsonProperty("ADD_ENU_YN")
        private String ADD_ENU_YN; // 할특적용여부
    }
}
