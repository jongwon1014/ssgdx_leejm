/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.ldi.model.newirt
 * @fileName : RetrieveEnrEvnInfoResN.java
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
 * DMS 에누리 행사 정보조회
 * </pre>
 * 
 * @author : q93m9k
 * @version : 2024.01.23
 * @see :
 */
@Getter
@Setter
public class RetrieveEnrEvnInfoResN {

    @IIrtFldMeta(size = 2)
    @JsonProperty("IrtType")
    private String IrtType; // 전문Type

    @IIrtFldMeta(size = 4)
    @JsonProperty("RES_CODE")
    private String RES_CODE; // 응답 코드*

    @IIrtFldMeta(size = 80)
    @JsonProperty("RES_MSG")
    private String RES_MSG; // 응답 메시지

    @IIrtFldMeta(size = 300)
    @JsonProperty("EVT_NM")
    private String EVT_NM; // 행사명

    @IIrtFldMeta(size = 2)
    @JsonProperty("EVT_KIND_CD")
    private String EVT_KIND_CD; // 행사종류코드

    @IIrtFldMeta(size = 15)
    @JsonProperty("EVT_MIN_BUYAMT")
    private String EVT_MIN_BUYAMT; // 최소구매금액

    @IIrtFldMeta(size = 1)
    @JsonProperty("MDALL_YN")
    private String MDALL_YN; // 전체MD여부

    @IIrtFldMeta(size = 1)
    @JsonProperty("PR_JOIN_YN")
    private String PR_JOIN_YN; // 사은행사참여가능여부

    @IIrtFldMeta(size = 1)
    @JsonProperty("ADD_ENU_YN")
    private String ADD_ENU_YN; // 할특중복가능여부

    @IIrtFldMeta(size = 1)
    @JsonProperty("ONEPAY_YN")
    private String ONEPAY_YN; // 전액결제여부

    @IIrtFldMeta(size = 1)
    @JsonProperty("USE_MDALL_YN")
    private String USE_MDALL_YN; // 사용전체MD여부

    @IIrtFldMeta(size = 1)
    @JsonProperty("USE_ONEPAY_YN")
    private String USE_ONEPAY_YN; // 사용전액결제여부

    @IIrtFldMeta(size = 15)
    @JsonProperty("USE_MIN_BUYAMT")
    private String USE_MIN_BUYAMT; // 사용가능최소구매금액

    @IIrtFldMeta(size = 5)
    @JsonProperty("MD_MST_CNT")
    private String MD_MST_CNT; // POS대상MD개수

    @IIrtFldMeta(size = 3)
    @JsonProperty("PAY_MST_CNT")
    private String PAY_MST_CNT; // POS대상결제수단개수

    @IIrtFldMeta(size = 3)
    @JsonProperty("BIN_MST_CNT")
    private String BIN_MST_CNT; // POS대상카드빈개수

    @IIrtFldMeta(size = 41)
    @JsonProperty("FILLER")
    private String FILLER; // 예비

    @JsonProperty("MdList")
    private List<MdList> mdList = new ArrayList<MdList>(); // MdList

    @JsonProperty("PayList")
    private List<PayList> payList = new ArrayList<PayList>(); // PayList

    @JsonProperty("BinList")
    private List<BinList> binList = new ArrayList<BinList>(); // BinList

    // MdList
    @Getter
    @Setter
    public static class MdList {

        @IIrtFldMeta(size = 2)
        @JsonProperty("MD_GB_CD")
        private String MD_GB_CD; // MD구분코드

        @IIrtFldMeta(size = 6)
        @JsonProperty("MD_CD")
        private String MD_CD; // MD코드

        @IIrtFldMeta(size = 10)
        @JsonProperty("SHOP_CD")
        private String SHOP_CD; // 매장코드

        @IIrtFldMeta(size = 2)
        @JsonProperty("DCNT_GB_CD")
        private String DCNT_GB_CD; // 할인구분코드

        @IIrtFldMeta(size = 3)
        @JsonProperty("NOR_ENURI_VAL")
        private String NOR_ENURI_VAL; // 정상에누리

        @IIrtFldMeta(size = 3)
        @JsonProperty("EVT_ENURI_VAL")
        private String EVT_ENURI_VAL; // 행사에누리

        @IIrtFldMeta(size = 15)
        @JsonProperty("DCNT_AMT")
        private String DCNT_AMT; // 할인금액

        @IIrtFldMeta(size = 20)
        @JsonProperty("MD_FILLER")
        private String MD_FILLER; // 예비
    }

    // PayList
    @Getter
    @Setter
    public static class PayList {

        @IIrtFldMeta(size = 2)
        @JsonProperty("PAY_CD")
        private String PAY_CD; // 결제수단코드

        @IIrtFldMeta(size = 18)
        @JsonProperty("PAY_FILLER")
        private String PAY_FILLER; // 예비
    }

    // BinList
    @Getter
    @Setter
    public static class BinList {

        @IIrtFldMeta(size = 6)
        @JsonProperty("BIN_START")
        private String BIN_START; // 카드빈시작

        @IIrtFldMeta(size = 6)
        @JsonProperty("BIN_END")
        private String BIN_END; // 카드빈종료

        @IIrtFldMeta(size = 100)
        @JsonProperty("EVT_CARD_NM")
        private String EVT_CARD_NM; // 행사카드명

        @IIrtFldMeta(size = 8)
        @JsonProperty("BIN8_START")
        private String BIN8_START; // 카드빈시작_8자리

        @IIrtFldMeta(size = 8)
        @JsonProperty("BIN8_END")
        private String BIN8_END; // 카드빈종료_8자리

        @IIrtFldMeta(size = 22)
        @JsonProperty("PAY_FILLER")
        private String BIN_FILLER; // 예비
    }
}
