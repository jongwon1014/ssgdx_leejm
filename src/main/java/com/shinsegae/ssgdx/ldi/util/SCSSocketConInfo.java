/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.ldi.util
 * @fileName : SCSSocketConInfo.java
 * @author : q93m9k
 * @date : 2024.03.04
 * @description :
 * 
 * COPYRIGHT ©2024 SHINSEGAE. ALL RIGHTS RESERVED.
 * 
 * <pre>
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024.03.04       q93m9k              최초 생성
 * ===========================================================
 * </pre>
 */
package com.shinsegae.ssgdx.ldi.util;

import java.net.Socket;

import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * 
 * </pre>
 * 
 * @author : q93m9k
 * @version : 2024.03.04
 * @see :
 */
@Getter
@Setter
public class SCSSocketConInfo {

    private Socket cliSocket;

    private long startTm;

    private String ip;
}
