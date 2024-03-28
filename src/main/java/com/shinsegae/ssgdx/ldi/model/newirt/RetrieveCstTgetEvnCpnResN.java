/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.ldi.model.newirt
 * @fileName : RetrieveCstTgetEvnCpnResN.java
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
package com.shinsegae.ssgdx.ldi.model.newirt;

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
public class RetrieveCstTgetEvnCpnResN {

    @IIrtFldMeta(size = 2)
    @JsonProperty("IrtType")
    private String IrtType; // 전문Type

    @IIrtFldMeta(size = 4)
    @JsonProperty("RES_CODE")
    private String RES_CODE; // 응답 코드

    @IIrtFldMeta(size = 80)
    @JsonProperty("RES_MSG")
    private String RES_MSG; // 응답 메시지

    @IIrtFldMeta(size = 10)
    @JsonProperty("IRT_ID")
    private String IRT_ID; // IRT ID

    @IIrtFldMeta(size = 3)
    @JsonProperty("ENURI_CD_CNT")
    private String ENURI_CD_CNT; // 에누리행사코드수

    @IIrtFldMeta(size = 3)
    @JsonProperty("CP_CD_CNT")
    private String CP_CD_CNT; // 쿠폰/금액할인권수

    @IIrtFldMeta(size = 1)
    @JsonProperty("CP_ISSUE")
    private String CP_ISSUE; // 쿠폰 발급 대상 여부

    @IIrtFldMeta(size = 50)
    @JsonProperty("FILLER")
    private String FILLER; // 예비

    @JsonProperty("EnuriList")
    private List<EnuriList> enuriList = new ArrayList<EnuriList>(); // 에누리행사 반복

    @JsonProperty("CpList")
    private List<CpList> cpList = new ArrayList<CpList>(); // 쿠폰/금액할인권 반복

    // EnuriList
    @Getter
    @Setter
    public static class EnuriList {

        @IIrtFldMeta(size = 4)
        @JsonProperty("ENURI_STR_CD")
        private String ENURI_STR_CD; // 점포코드

        @IIrtFldMeta(size = 6)
        @JsonProperty("ENURI_YM")
        private String ENURI_YM; // 행사년월

        @IIrtFldMeta(size = 9)
        @JsonProperty("ENURI_EVT_NO")
        private String ENURI_EVT_NO; // 행사번호

        @IIrtFldMeta(size = 17)
        @JsonProperty("CP_CD")
        private String CP_CD; // 쿠폰코드

        @IIrtFldMeta(size = 20)
        @JsonProperty("ENURI_FILLER")
        private String ENURI_FILLER; // 에누리행사 예비필드
    }

    // CpList
    @Getter
    @Setter
    public static class CpList {

        @IIrtFldMeta(size = 4)
        @JsonProperty("CP_STR_CD")
        private String CP_STR_CD; // 점포코드

        @IIrtFldMeta(size = 6)
        @JsonProperty("CP_YM")
        private String CP_YM; // 행사년월

        @IIrtFldMeta(size = 9)
        @JsonProperty("CP_EVT_NO")
        private String CP_EVT_NO; // 행사번호

        @IIrtFldMeta(size = 17)
        @JsonProperty("CP_CD")
        private String CP_CD; // 쿠폰코드

        @IIrtFldMeta(size = 20)
        @JsonProperty("CP_ISS_NO")
        private String CP_ISS_NO; // 쿠폰/할인권발급번호

        @IIrtFldMeta(size = 2)
        @JsonProperty("CP_KIND_CD")
        private String CP_KIND_CD; // 쿠폰구분코드

        @IIrtFldMeta(size = 8)
        @JsonProperty("CP_ISS_YMD")
        private String CP_ISS_YMD; // 발급일자

        @IIrtFldMeta(size = 8)
        @JsonProperty("CP_USE_START_YMD")
        private String CP_USE_START_YMD; // 사용시작일자

        @IIrtFldMeta(size = 8)
        @JsonProperty("CP_USE_END_YMD")
        private String CP_USE_END_YMD; // 사용종료일자

        @IIrtFldMeta(size = 2)
        @JsonProperty("CP_DCNT_GB_CD")
        private String CP_DCNT_GB_CD; // 할인구분코드

        @IIrtFldMeta(size = 2)
        @JsonProperty("PCHS_TGET_CRI_DVS_CD")
        private String PCHS_TGET_CRI_DVS_CD; // 구매대상 기준 구분코드

        @IIrtFldMeta(size = 3)
        @JsonProperty("NRM_CP_DCRT")
        private String NRM_CP_DCRT; // 정상쿠폰할인율

        @IIrtFldMeta(size = 3)
        @JsonProperty("EVN_CP_DCRT")
        private String EVN_CP_DCRT; // 행사쿠폰할인율

        @IIrtFldMeta(size = 15)
        @JsonProperty("NRM_CP_DCNT_AMT")
        private String NRM_CP_DCNT_AMT; // 정상쿠폰할인금액

        @IIrtFldMeta(size = 15)
        @JsonProperty("EVN_CP_DCNT_AMT")
        private String EVN_CP_DCNT_AMT; // 행사쿠폰할인금액

        @IIrtFldMeta(size = 3)
        @JsonProperty("CP_CNT")
        private String CP_CNT; // 보유쿠폰개수

        @IIrtFldMeta(size = 1)
        @JsonProperty("CP_ABLE_YN")
        private String CP_ABLE_YN; // 사용가능(MD기준)여부

        @IIrtFldMeta(size = 50)
        @JsonProperty("CP_ABLE_MSG")
        private String CP_ABLE_MSG; // 사용불가 사유

        @IIrtFldMeta(size = 2)
        @JsonProperty("CP_ISSUE_TYPE")
        private String CP_ISSUE_TYPE; // 쿠폰발급형태

        @IIrtFldMeta(size = 2)
        @JsonProperty("CP_CAMP_KIND_CD")
        private String CP_CAMP_KIND_CD; // 캠페인행사종류코드

        @IIrtFldMeta(size = 300)
        @JsonProperty("CP_EVT_NM")
        private String CP_EVT_NM; // 행사명

        @IIrtFldMeta(size = 2)
        @JsonProperty("CP_EVT_KIND_CD")
        private String CP_EVT_KIND_CD; // 행사구분코드

        @IIrtFldMeta(size = 1)
        @JsonProperty("CP_PR_JOIN_YN")
        private String CP_PR_JOIN_YN; // 사은행사참여가능여부

        @IIrtFldMeta(size = 1)
        @JsonProperty("CP_ADD_ENU_YN")
        private String CP_ADD_ENU_YN; // 할특시추가에누리가능여부

        @IIrtFldMeta(size = 1)
        @JsonProperty("CP_USE_MDALL_YN")
        private String CP_USE_MDALL_YN; // 사용 전체MD여부

        @IIrtFldMeta(size = 15)
        @JsonProperty("CP_USE_MIN_BUYAMT")
        private String CP_USE_MIN_BUYAMT; // 사용 최소구매액

        @IIrtFldMeta(size = 1)
        @JsonProperty("CP_USE_ONEPAY_YN")
        private String CP_USE_ONEPAY_YN; // 사용 전액결제여부

        @IIrtFldMeta(size = 15)
        @JsonProperty("CP_USE_MAX_ENURI_AMT")
        private String CP_USE_MAX_ENURI_AMT; // 사용 정률최대할인금액

        @IIrtFldMeta(size = 3)
        @JsonProperty("CP_MD_CNT")
        private String CP_MD_CNT; // 상품수

        @IIrtFldMeta(size = 2)
        @JsonProperty("CP_PAY_CNT")
        private String CP_PAY_CNT; // 결제수단갯수

        @IIrtFldMeta(size = 3)
        @JsonProperty("CP_S_RATE")
        private String CP_S_RATE; // 신세계분담율

        @IIrtFldMeta(size = 3)
        @JsonProperty("CP_B_RATE")
        private String CP_B_RATE; // 협력회사분담율

        @IIrtFldMeta(size = 3)
        @JsonProperty("CP_C_RATE")
        private String CP_C_RATE; // 카드사분담율

        @IIrtFldMeta(size = 8)
        @JsonProperty("CP_CACL_VALID_DATE")
        private String CP_CACL_VALID_DATE; // 취소유효기간

        @IIrtFldMeta(size = 50)
        @JsonProperty("CP_FILLER")
        private String CP_FILLER; // 쿠폰 예비필드

        @JsonProperty("CpMdList")
        private List<CpMdList> cpMdList = new ArrayList<CpMdList>(); // 상품 반복

        @JsonProperty("CpPayList")
        private List<CpPayList> cpPayList = new ArrayList<CpPayList>(); // 결제수단
                                                                        // 반복

        // CpMdList
        @Getter
        @Setter
        public static class CpMdList {

            @IIrtFldMeta(size = 10)
            @JsonProperty("CP_SHOP_CD")
            private String CP_SHOP_CD; // 매장코드

            @IIrtFldMeta(size = 6)
            @JsonProperty("CP_MD_CD")
            private String CP_MD_CD; // MD코드

            @IIrtFldMeta(size = 13)
            @JsonProperty("CP_PIPD_CD")
            private String CP_PIPD_CD; // 단품코드

            @IIrtFldMeta(size = 3)
            @JsonProperty("CP_NRM_CP_DCRT")
            private String CP_NRM_CP_DCRT; // 정상쿠폰할인율

            @IIrtFldMeta(size = 3)
            @JsonProperty("CP_EVN_CP_DCRT")
            private String CP_EVN_CP_DCRT; // 행사쿠폰할인율

            @IIrtFldMeta(size = 1)
            @JsonProperty("CP_USE_YN")
            private String CP_USE_YN; // 대상여부
        }

        // CpPayList
        @Getter
        @Setter
        public static class CpPayList {

            @IIrtFldMeta(size = 2)
            @JsonProperty("CP_PAY_CD")
            private String CP_PAY_CD; // 결제수단
        }
    }

}
