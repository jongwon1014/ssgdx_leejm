/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.ldi.model.newirt
 * @fileName : RetrieveCpnAmtDctkIssuPlndReqN.java
 * @author : q93m9k
 * @date : 2024.01.24
 * @description :
 * 
 * COPYRIGHT ©2024 SHINSEGAE. ALL RIGHTS RESERVED.
 * 
 * <pre>
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024.01.24       q93m9k              최초 생성
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
 * DMS 쿠폰 및 금액할인권 발급 예정 조회 8BIN
 * </pre>
 * 
 * @author : q93m9k
 * @version : 2024.01.24
 * @see :
 */
@Getter
@Setter
public class RetrieveCpnAmtDctkIssuPlndReqN {

    @IIrtFldMeta(size = 2)
    @JsonProperty("IrtType")
    private String IrtType; // 전문Type

    @IIrtFldMeta(size = 9)
    @JsonProperty("CUST_ID")
    private String CUST_ID; // 고객번호

    @IIrtFldMeta(size = 10)
    @JsonProperty("IRT_ID")
    private String IRT_ID; // IRT ID

    @IIrtFldMeta(size = 10)
    @JsonProperty("BUY_AMT")
    private String BUY_AMT; // 총구매액

    @IIrtFldMeta(size = 3)
    @JsonProperty("CUST_CNT")
    private String CUST_CNT; // 고객유형 수

    @IIrtFldMeta(size = 3)
    @JsonProperty("MD_CNT")
    private String MD_CNT; // MD코드 수

    @IIrtFldMeta(size = 3)
    @JsonProperty("PAY_CNT")
    private String PAY_CNT; // 결제수단개수

    @IIrtFldMeta(size = 32)
    @JsonProperty("FILLER")
    private String FILLER; // 예비

    @JsonProperty("CustList")
    private List<CustList> custList = new ArrayList<CustList>(); // 고객유형 반복

    @JsonProperty("MDList")
    private List<MDList> mdList = new ArrayList<MDList>(); // MD 반복

    @JsonProperty("PayList")
    private List<PayList> payList = new ArrayList<PayList>(); // 대상결제수단 반복

    // CustList
    @Getter
    @Setter
    public static class CustList {

        @IIrtFldMeta(size = 2)
        @JsonProperty("CUST_TYPE")
        private String CUST_TYPE; // 고객 유형

        @IIrtFldMeta(size = 18)
        @JsonProperty("CUST_FILLER")
        private String CUST_FILLER; // 예비
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

        @IIrtFldMeta(size = 29)
        @JsonProperty("MD_FILLER")
        private String MD_FILLER; // 예비
    }

    // PayList
    @Getter
    @Setter
    public static class PayList {

        @IIrtFldMeta(size = 2)
        @JsonProperty("ENURI_PAY_CD")
        private String ENURI_PAY_CD; // 결제수단코드

        @IIrtFldMeta(size = 10)
        @JsonProperty("ENURI_PAY_AMT")
        private String ENURI_PAY_AMT; // 결제수단별결제금액

        @IIrtFldMeta(size = 8)
        @JsonProperty("ENURI_BIN8")
        private String ENURI_BIN8; // 카드빈8

        @IIrtFldMeta(size = 34)
        @JsonProperty("ENURI_FILLER")
        private String ENURI_FILLER; // 예비
    }
}
