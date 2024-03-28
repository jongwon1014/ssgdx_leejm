/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.ldi.model.oldirt
 * @fileName : RetrieveCpnAmtDctkUsgCfmtResO.java
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
package com.shinsegae.ssgdx.ldi.model.oldirt;

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
public class RetrieveCpnAmtDctkUsgCfmtResO {

    @IIrtFldMeta(size = 2)
    @JsonProperty("IrtType")
    private String IrtType; // 전문Type

    @IIrtFldMeta(size = 4)
    @JsonProperty("RES_CODE")
    private String RES_CODE; // 응답 코드

    @IIrtFldMeta(size = 80)
    @JsonProperty("RES_MSG")
    private String RES_MSG; // 응답 메시지
}
