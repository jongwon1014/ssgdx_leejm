/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.ldi.util
 * @fileName : SCSSocketConTimeOutCheck.java
 * @author : q93m9k
 * @date : 2024.03.05
 * @description :
 * 
 * COPYRIGHT ©2024 SHINSEGAE. ALL RIGHTS RESERVED.
 * 
 * <pre>
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024.03.05       q93m9k              최초 생성
 * ===========================================================
 * </pre>
 */
package com.shinsegae.ssgdx.ldi.util;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * 
 * </pre>
 * 
 * @author : q93m9k
 * @version : 2024.03.05
 * @see :
 */
@Slf4j
@Getter
@Setter
public class SCSSocketConTimeOutCheck extends Thread {

    private SCSDaemon scsDaemon;

    private boolean shutdownFlg = false;

    public void init(SCSDaemon scsDaemon){
        this.scsDaemon = scsDaemon;
    }

    /**
     * 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run(){
        // TODO Auto-generated method stub
        while(!shutdownFlg){
            try{
                Thread.sleep(10 * 1000);
                scsDaemon.checkSocketTimeout();
            }catch(Exception e){
                log.error("SCSSocketConTimeOutCheck Exception. {}", e);
            }
        }
    }
}
