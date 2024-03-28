/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.util
 * @fileName : SCSDaemon.java
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

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * 소켓 서버 데몬 클래스
 * </pre>
 * 
 * @author : q93m9k
 * @version : 2024.01.08
 * @see :
 */
@Slf4j
@Getter
@Setter
@Component
public abstract class SCSDaemon {

    private int srvPort = 0;

    private int maxThCnt = 0;

    private int cliCnt = 0;

    private double runTm = 0;

    public static boolean shutdownFlg = false;

    public static boolean conSkipFlg = false;

    public static ArrayList<String> skipIpLst = new ArrayList<String>();

    private ArrayList<SCSSocketConInfo> scsSocketConInfoLst = new ArrayList<SCSSocketConInfo>();

    private final long socketTimeOut = 1000 * 60 * 60 * 25;// 25 Hour

    private final Integer serverSocketKeepAliveTimeOut = 1000 * 60 * 30; // 30
    // minute

    private long socketTimeOutCheckStartTm = 0;

    /**
     * <pre>
     * 소켓 서버 실행
     * </pre>
     *
     * @param srvPort
     * @param maxThCnt
     */
    protected void serverRun(int srvPort, int maxThCnt){
        this.srvPort = srvPort;
        this.maxThCnt = maxThCnt;

        ServerSocket server = null;
        SCSSocketConTimeOutCheck thSocketConTimeOutCheck = null;

        try{
            log.info("SocketDaemon Start...");
            log.info("Server Port : {}, Max Thread Count : {}", srvPort, maxThCnt);

            server = new ServerSocket(srvPort);

            socketTimeOutCheckStartTm = System.currentTimeMillis();

            thSocketConTimeOutCheck = new SCSSocketConTimeOutCheck();
            thSocketConTimeOutCheck.init(this);
            thSocketConTimeOutCheck.start();

            while(!shutdownFlg){
                // 클라이언트 연결수가 최대 연결이면 서비스 연결 대기
                synchronized(this){
                    while(cliCnt >= maxThCnt){
                        log.info(String.format("SocketDaemon serrvice wait : cliCnt[%d]- thMaxCnt[%d]", cliCnt,
                                maxThCnt));
                        this.wait();
                    }
                }

                try{
                    Socket client = server.accept();
                    String clientIp = client.getInetAddress().getHostAddress();

                    // 특정 IP일(L4등) 경우 비지니스 연결하지 않음
                    // 클라이언트 연결 수 Set (증가)
                    // 클라이언트 처리 비즈니스 실행 (쓰레드풀)
                    if(conSkipFlg){
                        if(clientIp == null)
                            clientIp = "";
                        if(checkIp(clientIp)){
                            client.close();
                        }else{
                            client.setKeepAlive(true);
                            synchronized(this){
                                cliCnt++;

                                SCSSocketConInfo scsSocketConInfo = new SCSSocketConInfo();
                                scsSocketConInfo.setCliSocket(client);
                                scsSocketConInfo.setIp(clientIp);
                                scsSocketConInfo.setStartTm(System.currentTimeMillis());
                                scsSocketConInfoLst.add(scsSocketConInfo);
                            }
                            log.info("Connected Client Ip : " + clientIp);
                            business(this, client);
                        }
                    }else{
                        client.setKeepAlive(true);
                        synchronized(this){
                            cliCnt++;

                            SCSSocketConInfo scsSocketConInfo = new SCSSocketConInfo();
                            scsSocketConInfo.setCliSocket(client);
                            scsSocketConInfo.setIp(clientIp);
                            scsSocketConInfo.setStartTm(System.currentTimeMillis());
                            scsSocketConInfoLst.add(scsSocketConInfo);
                        }
                        log.info("Connected Client Ip : " + clientIp);
                        business(this, client);
                    }
                }catch(Exception e){
                    log.info("serverRun Exception1. {}", e.toString());
                    log.info("serverRun -- accept or client business Exception -- 연결수:{}", cliCnt);
                }
            }
        }catch(Exception e){
            log.error("serverRun Exception2. {}", e);
            log.error("Exception SocketDaemon Stoped -- 연결수:{}", cliCnt);
        }finally{
            try{
                if(thSocketConTimeOutCheck != null){
                    thSocketConTimeOutCheck.setShutdownFlg(true);
                }

                if(server != null && server.isBound()){
                    server.close();
                    server = null;
                }
                log.info("SocketDaemon Stoped -- 연결수:{}", cliCnt);
            }catch(Exception e){
                log.error("serverRun finally Exception. {}", e);
            }
        }
    }

    /**
     * <pre>
     * 등록된 Skip IP 체크
     * </pre>
     *
     * @param clientIp
     * @return
     */
    private boolean checkIp(String clientIp){
        boolean ret = false;
        try{
            if(skipIpLst != null && skipIpLst.size() > 0){
                for(String ip : skipIpLst){
                    if(ip != null && ip.equals(clientIp)){
                        ret = true;
                        break;
                    }
                }
            }
        }catch(Exception e){
            log.error("checkIp Exception. {}", e);
        }
        return ret;
    }

    /**
     * <pre>
     * 클라이언트 서비스 종료시 호출되며 클라이언트 연결 수 Set (감소)
     * </pre>
     *
     */
    public void setCliCnt(){
        synchronized(this){
            cliCnt--;
            this.notifyAll();
        }
    }

    /**
     * <pre>
     * 서비스 실행 시간 Get
     * </pre>
     *
     * @return
     */
    public double getRunTm(){
        double d = runTm;
        synchronized(this){
            runTm = 0;
        }
        return d;
    }

    /**
     * <pre>
     * 서비스 실행 시간 Set
     * </pre>
     *
     * @param runTm
     */
    public void setRunTm(double runTm){
        synchronized(this){
            this.runTm += runTm;
        }
    }

    public void checkSocketTimeout(){
        long socketTimeOutCheckEndTime = System.currentTimeMillis();
        long socketTimeOutCheckTaskTime = (socketTimeOutCheckEndTime - socketTimeOutCheckStartTm);

        if(socketTimeOutCheckTaskTime >= socketTimeOut){
            synchronized(this){
                try{
                    int nLst = scsSocketConInfoLst.size();
                    log.info("checkSocketTimeout 연결 수 : {}", nLst);
                    for(int i = nLst - 1; i >= 0; i--){
                        SCSSocketConInfo scsSocketConInfo = scsSocketConInfoLst.get(i);

                        long endTime = System.currentTimeMillis();
                        long taskTime = (endTime - scsSocketConInfo.getStartTm());

                        if(taskTime >= socketTimeOut){
                            try{
                                if(scsSocketConInfo.getCliSocket() != null){
                                    log.info("checkSocketTimeout 시간 초과 된 클라이언트 종료 ip:{}", scsSocketConInfo.getIp());
                                    scsSocketConInfo.getCliSocket().close();
                                }
                            }catch(Exception e){
                                log.error("checkSocketTimeout socket close Exception. {}", e);
                            }finally{
                                scsSocketConInfoLst.remove(i);
                                log.info("checkSocketTimeout scsSocketConInfoLst removeed:{}",
                                        scsSocketConInfoLst.size());
                            }
                        }
                    }
                }catch(Exception e){
                    log.error("checkSocketTimeout unhandled Exception. {}", e);
                }
            }

            // 시작시간 초기화
            socketTimeOutCheckStartTm = System.currentTimeMillis();
            log.info("checkSocketTimeout 시작시간 초기화하고 다시 체크!!");
        }
    }

    public void removeClientSocketList(Socket cliSocket){
        synchronized(this){
            try{
                int nLst = scsSocketConInfoLst.size();
                for(int i = nLst - 1; i >= 0; i--){
                    SCSSocketConInfo scsSocketConInfo = scsSocketConInfoLst.get(i);
                    if(scsSocketConInfo.getCliSocket() != null && cliSocket != null){
                        if(scsSocketConInfo.getCliSocket().equals(cliSocket)){
                            log.info("removeClientSocketList -- cliIp:[{}]", scsSocketConInfo.getIp());
                            scsSocketConInfoLst.remove(i);
                            break;
                        }
                    }
                }
                log.info("removeClientSocketList 연결 수 : {}", scsSocketConInfoLst.size());
            }catch(Exception e){
                log.error("removeClientSocketList Exception. {}", e);
            }
        }
    }

    /**
     * <pre>
     * 클라이언트 서비스 실행
     * </pre>
     *
     * @param socketDaemon
     * @param client
     */
    public abstract void business(SCSDaemon socketDaemon, Socket client);
}
