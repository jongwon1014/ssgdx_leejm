/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.ldi.config
 * @fileName : SCSHttpClientConfig.java
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
package com.shinsegae.ssgdx.ldi.config;

import java.util.concurrent.TimeUnit;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

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
@EnableScheduling
@Configuration
public class SCSHttpClientConfig {

    @Value("${ssgdx-opdms.server.maxThreadCount: 1}")
    private int maxConnectionsTotal; // 총 conneciton 수

    private static final int TTRLOG_HTTP_REQ_TIMEOUT = 10 * 1000; // 10s

    private static final int IDLE_TIMEOUT = 15 * 1000; // 15s

    @Bean("scsCloseableHttpClient")
    public CloseableHttpClient scsCloseableHttpClient(){
        RequestConfig config = RequestConfig.custom().setConnectTimeout(TTRLOG_HTTP_REQ_TIMEOUT) /*
                                                                                                  * 연결이
                                                                                                  * 설정
                                                                                                  * 될
                                                                                                  * 때까지
                                                                                                  * 시간
                                                                                                  * 초과를
                                                                                                  * 설정
                                                                                                  */
                .setConnectionRequestTimeout(TTRLOG_HTTP_REQ_TIMEOUT) /*
                                                                       * ConnectionManager
                                                                       * (
                                                                       * 커넥션풀
                                                                       * )
                                                                       * 로부터
                                                                       * 꺼내올
                                                                       * 때의
                                                                       * 타임아웃
                                                                       */
                .setSocketTimeout(TTRLOG_HTTP_REQ_TIMEOUT)
                .build(); /* 커넥션을 맺은 후 타임아웃 시간 동안 응답이 없으면 해제 */
        return HttpClients.custom().setConnectionManager(scsPoolingHttpClientConnectionManager())
                .setDefaultRequestConfig(config).build();
    }

    @Bean("scsPoolingHttpClientConnectionManager")
    public PoolingHttpClientConnectionManager scsPoolingHttpClientConnectionManager(){
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultMaxPerRoute(maxConnectionsTotal); // 어떤 경로든
                                                                      // 최대 연결수
        connectionManager.setMaxTotal(maxConnectionsTotal); // 전달되는 경로에 대해서 최대
                                                            // 연결 갯수를 지정
        return connectionManager;
    }

    /**
     * <pre>
     * ConnectionManager를 검사하여 이미 만료되었거나 Idle 상태인 Connection들을 종료해 주는 Bean을 만들어 5초(fixedDelay) 주기로 수행
     * IDLE_TIMEOUT은 15초로 설정하여 15초 동안 Connection을 재사용하지 않으면 Idle Connection으로 판단하여 종료
     * </pre>
     *
     * @param connectionManager
     * @return
     */
    @Bean("scsIdleConnectionMonitor")
    public Runnable scsIdleConnectionMonitor(
            @Qualifier("scsPoolingHttpClientConnectionManager") PoolingHttpClientConnectionManager connectionManager){
        return new Runnable() {

            @Override
            @Scheduled(fixedDelay = 5 * 1000)
            public void run(){
                try{
                    if(connectionManager != null){
                        // log.debug("{} : 만료 또는 Idle 커넥션 종료.",
                        // Thread.currentThread().getName());
                        connectionManager.closeExpiredConnections();
                        connectionManager.closeIdleConnections(IDLE_TIMEOUT, TimeUnit.MILLISECONDS);
                    }else{
                        // log.debug("{} : ConnectionManager가 없습니다.",
                        // Thread.currentThread().getName());
                    }
                }catch(Exception e){
                    log.error("{} : 만료 또는 Idle 커넥션 종료 중 예외 발생. {}", Thread.currentThread().getName(), e);
                }
            }
        };
    }
}
