/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.ldi.model.oldirt
 * @fileName : RetrieveEnrEvnInfoReqO.java
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
 * DMS 에누리 행사 정보조회
 * </pre>
 * 
 * @author : q93m9k
 * @version : 2024.01.23
 * @see :
 */
@Getter
@Setter
public class RetrieveEnrEvnInfoReqO {

    @IIrtFldMeta(size = 4)
    @JsonProperty("ORG_ENURI_STR_CD")
    private String ORG_ENURI_STR_CD; // 점포코드

    @IIrtFldMeta(size = 6)
    @JsonProperty("ORG_ENURI_YM")
    private String ORG_ENURI_YM; // 행사년월

    @IIrtFldMeta(size = 6)
    @JsonProperty("ORG_ENURI_EVT_NO")
    private String ORG_ENURI_EVT_NO; // 행사번호

    @IIrtFldMeta(size = 34)
    @JsonProperty("FILLER")
    private String FILLER; // 예비
}
