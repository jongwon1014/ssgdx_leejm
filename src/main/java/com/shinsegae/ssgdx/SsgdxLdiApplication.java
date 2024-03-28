/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx
 * @fileName : SsgdxLdiApplication.java
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
package com.shinsegae.ssgdx;

import java.net.Socket;
import java.util.ArrayList;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.core.task.TaskExecutor;

import com.shinsegae.ssgdx.ldi.config.SCSSchedulingConfig;
import com.shinsegae.ssgdx.ldi.controller.RecvDmsController;
import com.shinsegae.ssgdx.ldi.util.SCSDaemon;

/**
 * <pre>
 * 
 * </pre>
 * 
 * @author : q93m9k
 * @version : 2024.01.08
 * @see :
 */
@ConfigurationPropertiesScan("com.shinsegae.ssgdx.ldi.config.properties")
@SpringBootApplication
public class SsgdxLdiApplication extends SCSDaemon implements CommandLineRunner {

	@Value("${ssgdx-opdms.server.port: 9100}")
	int port;

	@Value("${ssgdx-opdms.server.maxThreadCount: 1}")
	int maxThreadCount;

	@Value("${ssgdx-opdms.ldi.skipIpLst:}")
	ArrayList<String> skipIpLstInfo;

	// 생성자 주입 빈 선언
	private final ObjectProvider<RecvDmsController> recvDmsControllerProvider;

	private final SCSSchedulingConfig scsSchedulingConfig;

	private final TaskExecutor scsThreadPoolTaskExecutor;

	public static void main(String[] args) {
		SpringApplication.run(SsgdxLdiApplication.class, args);
	}

	public SsgdxLdiApplication(ObjectProvider<RecvDmsController> recvDmsControllerProvider,
			@Qualifier("scsSchedulingConfig") SCSSchedulingConfig scsSchedulingConfig,
			@Qualifier("scsThreadPoolTaskExecutor") TaskExecutor scsThreadPoolTaskExecutor) {
		this.recvDmsControllerProvider = recvDmsControllerProvider;
		this.scsSchedulingConfig = scsSchedulingConfig;
		this.scsThreadPoolTaskExecutor = scsThreadPoolTaskExecutor;
	}

	/**
	 * 소켓 서버 실행
	 * 
	 * @param args
	 * @throws Exception
	 * @see org.springframework.boot.CommandLineRunner#run(java.lang.String[])
	 */
	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		for (String skipIp : skipIpLstInfo) {
			SCSDaemon.skipIpLst.add(skipIp);
		}
		if (SCSDaemon.skipIpLst != null && SCSDaemon.skipIpLst.size() > 0)
			SCSDaemon.conSkipFlg = true;

		serverRun(port, maxThreadCount);
		System.exit(0);
	}

	/**
	 * @param socketDaemon
	 * @param client
	 * @see com.shinsegae.ssgdx.ldi.util.SCSDaemon#business(com.shinsegae.ssgdx.ldi.util.SCSDaemon,
	 *      java.net.Socket)
	 */
	@Override
	public void business(SCSDaemon socketDaemon, Socket client) {
		// TODO Auto-generated method stub
		RecvDmsController recvDmsController = recvDmsControllerProvider.getObject();
		recvDmsController.init(socketDaemon, client);
		scsThreadPoolTaskExecutor.execute(recvDmsController);
	}

	/**
	 * <pre>
	 * 종료 전 scsIdleConnectionMonitor 스케쥴러 중단
	 * </pre>
	 *
	 */
	@PreDestroy
	public void destory() {
		scsSchedulingConfig.getThreadPoolTaskScheduler().destroy();
	}
}
