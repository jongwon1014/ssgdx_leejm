/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.ldi.model.comirt
 * @fileName : EncHeader.java
 * @author : q93m9k
 * @date : 2024.01.08
 * @description :
 * 
 * COPYRIGHT ©2024 SHINSEGAE. ALL RIGHTS RESERVED.
 * 
 * <pre>
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024.01.08       q93m9k              최초 생성
 * ===========================================================
 * </pre>
 */
package com.shinsegae.ssgdx.ldi.model.comirt;

import com.shinsegae.ssgdx.ldi.util.IIrtFldMeta;

import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * 
 * </pre>
 * 
 * @author : q93m9k
 * @version : 2024.01.08
 * @see :
 */
@Getter
@Setter
public class EncHeader {

    @IIrtFldMeta(size = 6)
    private String TOTLEN; // 전체전문길이

    @IIrtFldMeta(size = 1)
    private String ENCTYPE; // 암호화 타입

    @IIrtFldMeta(size = 4)
    private String POSNO; // POS 번호

    @IIrtFldMeta(size = 4)
    private String TRANNO; // 거래 번호

    @IIrtFldMeta(size = 6)
    private String MSGVLI; // 원문 길이
}
