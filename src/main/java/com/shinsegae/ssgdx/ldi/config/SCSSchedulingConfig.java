/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.ldi.config
 * @fileName : SCSSchedulingConfig.java
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
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import lombok.Getter;

/**
 * <pre>
 * 기본적으로 모든 @Scheduled 작업은 Spring에 의해 생성 된 한개의 스레드 풀에서 실행된다. 그렇기 때문에 하나의
 * Scheduled이 돌고 있다면 그것이 다 끝나야 다음 Scheduled이 실행되는 문제가 있다. 스프링 부트에서 설정을 통해
 * Schedule에 대한 쓰래드 풀을 생성하고 그 쓰레드 풀을 사용하여 모든 스케줄 된 작업을 실행하도록 할 수 있다.
 * </pre>
 * 
 * @author : q93m9k
 * @version : 2024.01.08
 * @see :
 */
@Getter
@Configuration
@Qualifier("scsSchedulingConfig")
public class SCSSchedulingConfig implements SchedulingConfigurer {

    private final int SCH_TSK_POOL_SIZE = 1;

    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    /**
     * @param taskRegistrar
     * @see org.springframework.scheduling.annotation.SchedulingConfigurer#configureTasks(org.springframework.scheduling.config.ScheduledTaskRegistrar)
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar){
        // TODO Auto-generated method stub
        threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(SCH_TSK_POOL_SIZE);
        threadPoolTaskScheduler.setThreadNamePrefix("SocketComServer-scheduled-task-pool-");
        threadPoolTaskScheduler.initialize();

        taskRegistrar.setTaskScheduler(threadPoolTaskScheduler);
    }
}
