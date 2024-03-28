/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.ldi.model.oldirt
 * @fileName : RetrieveCstTgetEvnCpnResO.java
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
public class RetrieveCstTgetEvnCpnResO {

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
    @JsonProperty("CP_ISS_YN")
    private String CP_ISS_YN; // 쿠폰 발행 대항 거래 여부

    @IIrtFldMeta(size = 13)
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

        @IIrtFldMeta(size = 6)
        @JsonProperty("ENURI_EVT_NO")
        private String ENURI_EVT_NO; // 행사번호

        @IIrtFldMeta(size = 14)
        @JsonProperty("ENURI_FILLER")
        private String ENURI_FILLER; // 예비
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

        @IIrtFldMeta(size = 6)
        @JsonProperty("CP_EVT_NO")
        private String CP_EVT_NO; // 행사번호

        @IIrtFldMeta(size = 6)
        @JsonProperty("CP_ISS_NO")
        private String CP_ISS_NO; // 쿠폰/할인권발급번호

        @IIrtFldMeta(size = 10)
        @JsonProperty("CP_NO")
        private String CP_NO; // 쿠폰/할인권번호

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

        @IIrtFldMeta(size = 10)
        @JsonProperty("CP_ENURI_VAL")
        private String CP_ENURI_VAL; // 에누리값

        @IIrtFldMeta(size = 3)
        @JsonProperty("CP_CNT")
        private String CP_CNT; // 보유쿠폰개수

        @IIrtFldMeta(size = 1)
        @JsonProperty("CP_ABLE_YN")
        private String CP_ABLE_YN; // 사용가능(MD기준)여부

        @IIrtFldMeta(size = 50)
        @JsonProperty("CP_ABLE_MSG")
        private String CP_ABLE_MSG; // 사용불가사유

        @IIrtFldMeta(size = 2)
        @JsonProperty("CP_ISS_WAY_CD")
        private String CP_ISS_WAY_CD; // 발급형태코드

        @IIrtFldMeta(size = 10)
        @JsonProperty("CP_RT_MAX_AMT")
        private String CP_RT_MAX_AMT; // 정율최대할인금액

        @IIrtFldMeta(size = 2)
        @JsonProperty("CP_CAMP_KIND_CD")
        private String CP_CAMP_KIND_CD; // 캠페인행사종류코드

        @IIrtFldMeta(size = 2)
        @JsonProperty("CP_FILLER")
        private String CP_FILLER; // 예비
    }
}
