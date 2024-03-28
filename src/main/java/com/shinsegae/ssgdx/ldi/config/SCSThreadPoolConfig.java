/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.ldi.config
 * @fileName : SCSThreadPoolConfig.java
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

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * <pre>
 * 
 * </pre>
 * 
 * @author : q93m9k
 * @version : 2024.01.08
 * @see :
 */
@Configuration
@Qualifier("scsThreadPoolConfig")
public class SCSThreadPoolConfig {

    @Value("${ssgdx-opdms.server.maxThreadCount: 1}")
    int maxPoolSize;

    @Value("${ssgdx-opdms.server.awaitTerminationSeconds: 1}")
    int awaitTerminationSeconds;

    @Bean
    @Qualifier("scsThreadPoolTaskExecutor")
    public TaskExecutor scsThreadPoolTaskExecutor(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(maxPoolSize);
        taskExecutor.setMaxPoolSize(maxPoolSize);
        taskExecutor.setQueueCapacity(maxPoolSize);

        // ThreadPool 종료시 대기 여부 설정
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        // ThreadPool 종료시 대기를 허용했을 경우 이곳에서 설정된 시간이 지날 때까지 종료를 대기한다.
        taskExecutor.setAwaitTerminationSeconds(awaitTerminationSeconds);

        // core thread에대한 타임아웃 여부
        taskExecutor.setAllowCoreThreadTimeOut(false);
        // 코어 쓰레드 타임아웃을 허용했을 경우 이곳에서 설정된 시간이 지날 때까지 코어 쓰레드 풀의 쓰레드가 사용되지 않을 경우 해당
        // 쓰레드는 종료
        // 된다
        taskExecutor.setKeepAliveSeconds(60);

        taskExecutor.initialize();

        return taskExecutor;
    }
}
