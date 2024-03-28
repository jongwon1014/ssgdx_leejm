/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.ldi.controller
 * @fileName : RecvDmsController.java
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
package com.shinsegae.ssgdx.ldi.controller;

import java.net.Socket;

import com.shinsegae.ssgdx.ldi.service.RecvDmsService;
import com.shinsegae.ssgdx.ldi.util.SCSDaemon;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * 
 * </pre>
 * 
 * @author : q93m9k
 * @version : 2024.01.08
 * @see :
 */
@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
@Scope("prototype")
@Controller
public class RecvDmsController implements ISCSController , Runnable {

    private SCSDaemon scsDaemon;

    private Socket client;

    // 생성자 주입 빈 선언
    private final ObjectProvider<RecvDmsService> recvDmsServiceProvider;

    /**
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run(){
        // TODO Auto-generated method stub
        RecvDmsService recvDmsService = recvDmsServiceProvider.getObject();
        recvDmsService.business(scsDaemon, client);
    }

    /**
     * @param scsDaemon
     * @param client
     * @see com.shinsegae.ssgdx.ldi.controller.ISCSController#init(com.shinsegae.ssgdx.ldi.util.SCSDaemon,
     * java.net.Socket)
     */
    @Override
    public void init(SCSDaemon scsDaemon, Socket client){
        // TODO Auto-generated method stub
        this.scsDaemon = scsDaemon;
        this.client = client;
    }

}
