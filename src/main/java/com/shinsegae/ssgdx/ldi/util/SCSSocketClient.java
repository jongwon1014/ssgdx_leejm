/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.util
 * @fileName : SCSSocketClient.java
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
package com.shinsegae.ssgdx.ldi.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Component
public class SCSSocketClient {

    private final SocketUtil socketUtil;

    public int sendData(String tgIp, int tgPort, byte[] sendBuf, byte[] recvBuf, String socketRecvCharset,
            String socketSendCharset, int connectTimeout, int serviceTimeout){
        int ret = -1;

        int sendLen = 0, recvLen = 0;
        long startTime = 0;

        Socket socket = null;
        BufferedInputStream bis;
        BufferedOutputStream bos;

        try{
            sendLen = sendBuf.length;
            recvLen = recvBuf.length;

            // connect
            startTime = System.currentTimeMillis();
            SocketAddress socketAddress = new InetSocketAddress(tgIp, tgPort);
            socket = new Socket();
            socket.setSoTimeout(serviceTimeout);
            socket.connect(socketAddress, connectTimeout);

            bis = new BufferedInputStream(socket.getInputStream());
            bos = new BufferedOutputStream(socket.getOutputStream());

            // send
            ret = socketUtil.tcp_send_b(bos, socket, sendBuf, sendLen, serviceTimeout);

            if(ret == 0){
                // recv
                ret = socketUtil.tcp_recv_b(bis, socket, recvBuf, recvLen, serviceTimeout);

                if(ret == 0){
                    long endTime = System.currentTimeMillis();
                    long taskTime = endTime - startTime;
                    double elapsed_time = (double)taskTime / 1000;
                    int remain_time = serviceTimeout - (int)taskTime;
                    if(remain_time < 0)
                        remain_time = 0;

                    log.info(String.format("service timeout : %d, elapsed time : %.3f, remain time : %.3f",
                            serviceTimeout / 1000, elapsed_time, (double)remain_time / 1000));
                }
            }
        }catch(Exception e){
            log.error("sendData Exception. {}", e);
            ret = -1;
        }finally{
            // disconnect
            if(socket != null){
                try{
                    socket.close();
                }catch(IOException e){
                    log.error(e.toString());
                    ret = -1;
                }
            }
        }

        return ret;

    }
}
