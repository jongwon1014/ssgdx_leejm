/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.ldi.model.newirt
 * @fileName : RetrieveCpnAmtDctkIssuPlndResN.java
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
public class RetrieveCpnAmtDctkIssuPlndResN {

    @IIrtFldMeta(size = 2)
    @JsonProperty("IrtType")
    private String IrtType; // 전문Type

    @IIrtFldMeta(size = 4)
    @JsonProperty("RES_CODE")
    private String RES_CODE; // 응답 코드

    @IIrtFldMeta(size = 80)
    @JsonProperty("RES_MSG")
    private String RES_MSG; // 응답 메시지

    @IIrtFldMeta(size = 3)
    @JsonProperty("PRT_TOT_CNT")
    private String PRT_TOT_CNT; // 출력라인총개수

    @IIrtFldMeta(size = 27)
    @JsonProperty("FILLER")
    private String FILLER; // 예비

    @JsonProperty("PrintList")
    private List<PrintList> printList = new ArrayList<PrintList>(); // 출력라인 반복

    // PrintList
    @Getter
    @Setter
    public static class PrintList {

        @IIrtFldMeta(size = 3)
        @JsonProperty("PRT_SEQ")
        private String PRT_SEQ; // 순번

        @IIrtFldMeta(size = 1)
        @JsonProperty("PRT_CTL")
        private String PRT_CTL; // 제어문자

        @IIrtFldMeta(size = 40)
        @JsonProperty("PRT_MSG")
        private String PRT_MSG; // 내용

        @IIrtFldMeta(size = 26)
        @JsonProperty("PRT_FILLER")
        private String PRT_FILLER; // 예비
    }
}
