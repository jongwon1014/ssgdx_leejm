/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.ldi.model.newirt
 * @fileName : DmsManualEnuriResN.java
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
public class RetrieveHwrtEnrAthResN {

    @IIrtFldMeta(size = 2)
    @JsonProperty("IrtType")
    private String IrtType; // 전문Type

    @IIrtFldMeta(size = 4)
    @JsonProperty("RES_CODE")
    private String RES_CODE; // 응답 코드

    @IIrtFldMeta(size = 80)
    @JsonProperty("RES_MSG")
    private String RES_MSG; // 응답 메시지

    @IIrtFldMeta(size = 1)
    @JsonProperty("AUTH_YN")
    private String AUTH_YN; // 권한여부

    @IIrtFldMeta(size = 4)
    @JsonProperty("MANUAL_STR_CD")
    private String MANUAL_STR_CD; // 점포코드

    @IIrtFldMeta(size = 6)
    @JsonProperty("MANUAL_YM")
    private String MANUAL_YM; // 행사년월

    @IIrtFldMeta(size = 9)
    @JsonProperty("MANUAL_EVT_NO")
    private String MANUAL_EVT_NO; // 행사번호

    @IIrtFldMeta(size = 2)
    @JsonProperty("MANUAL_EVT_CD")
    private String MANUAL_EVT_CD; // 행사종류코드

    @IIrtFldMeta(size = 3)
    @JsonProperty("NOR_ENU_RATE")
    private String NOR_ENU_RATE; // 정상에누리율

    @IIrtFldMeta(size = 3)
    @JsonProperty("EVT_ENU_RATE")
    private String EVT_ENU_RATE; // 행사에누리율

    @IIrtFldMeta(size = 15)
    @JsonProperty("ORG_ENURI_AMT")
    private String ORG_ENURI_AMT; // 원거래 수기에누리 금액

    @IIrtFldMeta(size = 1)
    @JsonProperty("ENURI_FLAG")
    private String ENURI_FLAG; // 권한적용범위FLAG

    @IIrtFldMeta(size = 10)
    @JsonProperty("AUTH_SHOP")
    private String AUTH_SHOP; // 권한적용범위

    @IIrtFldMeta(size = 58)
    @JsonProperty("FILLER")
    private String FILLER; // 예비
}
