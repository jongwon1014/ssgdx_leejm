/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.ldi.model.oldirt
 * @fileName : DmsManualEnuriReqO.java
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

import com.shinsegae.ssgdx.ldi.util.IIrtFldMeta;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * DMS 수기 에누리 권한 조회
 * </pre>
 * 
 * @author : q93m9k
 * @version : 2024.01.23
 * @see :
 */
@Getter
@Setter
public class RetrieveHwrtEnrAthReqO {

    @IIrtFldMeta(size = 2)
    @JsonProperty("CASHIER_ATTR")
    private String CASHIER_ATTR; // 캐셔속성

    @IIrtFldMeta(size = 6)
    @JsonProperty("CASHIER_NO")
    private String CASHIER_NO; // 캐셔번호

    @IIrtFldMeta(size = 1)
    @JsonProperty("AUTH_TYPE")
    private String AUTH_TYPE; // 권한종류구분

    @IIrtFldMeta(size = 8)
    @JsonProperty("ORT_TRAN_DATE")
    private String ORT_TRAN_DATE; // 원매출일

    @IIrtFldMeta(size = 4)
    @JsonProperty("ORG_POS_NO")
    private String ORG_POS_NO; // 원POS번호

    @IIrtFldMeta(size = 4)
    @JsonProperty("ORG_TRAN_NO")
    private String ORG_TRAN_NO; // 원거래번호

    @IIrtFldMeta(size = 6)
    @JsonProperty("ORG_CASHIER_NO")
    private String ORG_CASHIER_NO; // 원거래 캐셔번호

    @IIrtFldMeta(size = 19)
    @JsonProperty("FILLER")
    private String FILLER; // 예비
}
