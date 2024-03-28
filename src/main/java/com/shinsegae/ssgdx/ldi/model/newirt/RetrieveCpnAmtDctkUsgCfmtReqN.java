/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.ldi.model.newirt
 * @fileName : RetrieveCpnAmtDctkUsgCfmtReqN.java
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
 * DMS 쿠폰 및 금액할인권 사용 확정 조회
 * </pre>
 * 
 * @author : q93m9k
 * @version : 2024.01.24
 * @see :
 */
@Getter
@Setter
public class RetrieveCpnAmtDctkUsgCfmtReqN {

    @IIrtFldMeta(size = 2)
    @JsonProperty("IrtType")
    private String IrtType; // 전문Type

    @IIrtFldMeta(size = 9)
    @JsonProperty("CUST_ID")
    private String CUST_ID; // 고객번호

    @IIrtFldMeta(size = 10)
    @JsonProperty("IRT_ID")
    private String IRT_ID; // IRT ID

    @IIrtFldMeta(size = 3)
    @JsonProperty("CP_CNT")
    private String CP_CNT; // 사용 쿠폰/금액할인권 개수

    @IIrtFldMeta(size = 28)
    @JsonProperty("FILLER")
    private String FILLER; // 예비

    @JsonProperty("CouponList")
    private List<CouponList> couponList = new ArrayList<CouponList>(); // 사용쿠폰/금액할인권
                                                                       // 정보 반복

    // CouponList
    @Getter
    @Setter
    public static class CouponList {

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
        @JsonProperty("CP_KIND_NO")
        private String CP_KIND_NO; // 쿠폰구분코드

        @IIrtFldMeta(size = 16)
        @JsonProperty("CP_FILLER")
        private String CP_FILLER; // 예비
    }
}
