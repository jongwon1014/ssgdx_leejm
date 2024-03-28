/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.ldi.model.oldirt
 * @fileName : DmsComHeaderO.java
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
package com.shinsegae.ssgdx.ldi.model.comirt;

import com.shinsegae.ssgdx.ldi.util.IIrtFldMeta;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * 
 * </pre>
 * 
 * @author : q93m9k
 * @version : 2024.01.23
 * @see :
 */
@Getter
@Setter
public class DmsComHeader {

    @IIrtFldMeta(size = 6)
    @JsonProperty("MSG_LEN")
    private String MSG_LEN; // 전문길이

    @IIrtFldMeta(size = 8)
    @JsonProperty("MSG_ID")
    private String MSG_ID; // 전문ID

    @IIrtFldMeta(size = 6)
    @JsonProperty("MSG_VER")
    private String MSG_VER; // 전문 버전

    @IIrtFldMeta(size = 8)
    @JsonProperty("SALE_DATE")
    private String SALE_DATE; // 영업일자

    @IIrtFldMeta(size = 4)
    @JsonProperty("STORE_CD")
    private String STORE_CD; // 점코드

    @IIrtFldMeta(size = 4)
    @JsonProperty("POS_NO")
    private String POS_NO; // POS번호

    @IIrtFldMeta(size = 4)
    @JsonProperty("TRAN_NO")
    private String TRAN_NO; // 거래번호

    @IIrtFldMeta(size = 3)
    @JsonProperty("COM_TYPE")
    private String COM_TYPE; // 통신구분

    @IIrtFldMeta(size = 10)
    @JsonProperty("COM_ID")
    private String COM_ID; // 통신ID

    @IIrtFldMeta(size = 8)
    @JsonProperty("COM_DATE")
    private String COM_DATE; // 통신날짜

    @IIrtFldMeta(size = 6)
    @JsonProperty("COM_TIME")
    private String COM_TIME; // 통신시간

    @IIrtFldMeta(size = 20)
    @JsonProperty("USER_INFO")
    private String USER_INFO; // 사용자정보

    @IIrtFldMeta(size = 4)
    @JsonProperty("RES_CODE")
    private String RES_CODE; // 응답 코드

    @IIrtFldMeta(size = 80)
    @JsonProperty("RES_MSG")
    private String RES_MSG; // 응답 메시지

    @IIrtFldMeta(size = 29)
    @JsonProperty("FILLER")
    private String FILLER; // 예비
}
