/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.util
 * @fileName : SocketUtil.java
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
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.springframework.stereotype.Component;

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
@Component
public class SocketUtil {

    /**
     * <pre>
     * socket recv
     * </pre>
     *
     * @param bis
     * @param sock
     * @param bRecvBuf
     * @param nRecvLen
     * @return
     */
    public int tcp_recv_b(BufferedInputStream bis, Socket sock, byte[] bRecvBuf, int nRecvLen){
        int nRet = -1;
        nRet = tcp_recv_b(bis, sock, bRecvBuf, nRecvLen, 0);
        return nRet;
    }

    /**
     * <pre>
     * socket recv with timeout
     * </pre>
     *
     * @param bis
     * @param sock
     * @param bRecvBuf
     * @param nRecvLen
     * @param nTimeout
     * @return
     */
    public int tcp_recv_b(BufferedInputStream bis, Socket sock, byte[] bRecvBuf, int nRecvLen, int nTimeout){
        int nRet = -1;
        int nlTotLen = 0;

        try{
            if(nTimeout > 0)
                sock.setSoTimeout(nTimeout);

            log.debug(String.format("SocketUtil -- Recv Wait -- nRecvLen:[%d], nTimeout:[%d(ms)]", nRecvLen, nTimeout));

            while(nlTotLen != nRecvLen){
                int bytesRead = bis.read(bRecvBuf, nlTotLen, nRecvLen - nlTotLen);

                log.debug(String.format("SocketUtil -- Recv -- bytesRead:[%d]", bytesRead));
                log.debug(String.format("SocketUtil -- Recv -- bRecvBuf:[%s]", new String(bRecvBuf)));

                if(bytesRead == -1)
                    break;
                nlTotLen += bytesRead;
            }
            log.debug(String.format("SocketUtil -- Recv -- nlTotLen:[%d]", nlTotLen));

            if(nlTotLen == 0 || nlTotLen != nRecvLen)
                nRet = -1;
            else
                nRet = 0;
        }catch(SocketTimeoutException e){
            nRet = -2;
            log.error("SocketUtil -- Recv SocketTimeoutException. {}", e);
        }catch(Exception e){
            nRet = -3;
            log.info("SocketUtil -- Recv Exception. {}", e.toString());
        }

        return nRet;
    }

    /**
     * <pre>
     * socket send
     * </pre>
     *
     * @param bos
     * @param sock
     * @param bSendBuf
     * @param nSendLen
     * @return
     */
    public int tcp_send_b(BufferedOutputStream bos, Socket sock, byte[] bSendBuf, int nSendLen){
        int nRet = -1;
        nRet = tcp_send_b(bos, sock, bSendBuf, nSendLen, 0);
        return nRet;
    }

    /**
     * <pre>
     * socket send with timeout
     * </pre>
     *
     * @param bos
     * @param sock
     * @param bSendBuf
     * @param nSendLen
     * @param nTimeout
     * @return
     */
    public int tcp_send_b(BufferedOutputStream bos, Socket sock, byte[] bSendBuf, int nSendLen, int nTimeout){
        int nRet = -1;

        if(nSendLen <= 0)
            return nRet;

        try{
            if(nTimeout > 0)
                sock.setSoTimeout(nTimeout);

            log.debug(String.format("SocketUtil -- Send -- nSendLen:[%d], nTimeout:[%d(ms)]", nSendLen, nTimeout));

            bos.write(bSendBuf, 0, nSendLen);
            bos.flush();

            log.debug(String.format("SocketUtil -- Send -- bSendBuf:[%s]", new String(bSendBuf)));

            nRet = 0;
        }catch(SocketTimeoutException e){
            nRet = -2;
            log.error("SocketUtil -- Send SocketTimeoutException. {}", e);
        }catch(Exception e){
            nRet = -3;
            log.info("SocketUtil -- Send Exception. {}" + e.toString());
        }

        return nRet;
    }

    /**
     * <pre>
     * 전체 서비스 타임아웃에서 서비스 시작 이후 남은 시간 계산
     * </pre>
     *
     * @param startTime
     * @param serviceTimeout
     * @return
     */
    public int getRemainTime(long startTime, int serviceTimeout){
        long endTime = System.currentTimeMillis();
        long taskTime = endTime - startTime;
        double elapsed_time = (double)taskTime / 1000;
        int remain_time = serviceTimeout - (int)taskTime;
        if(remain_time < 0)
            remain_time = 0;

        log.debug(String.format("service timeout : %d, elapsed time : %.3f, remain time : %.3f", serviceTimeout / 1000,
                elapsed_time, (double)remain_time / 1000));

        return remain_time;
    }
}
