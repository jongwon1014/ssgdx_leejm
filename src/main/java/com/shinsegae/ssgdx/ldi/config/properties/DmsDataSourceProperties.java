/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.rst.config.properties
 * @fileName : RstDataSourceProperties.java
 * @author : q93m9k
 * @date : 2024.03.02
 * @description :
 * 
 * COPYRIGHT ©2024 SHINSEGAE. ALL RIGHTS RESERVED.
 * 
 * <pre>
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024.03.02       q93m9k              최초 생성
 * ===========================================================
 * </pre>
 */
package com.shinsegae.ssgdx.ldi.config.properties;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * 
 * </pre>
 * 
 * @author : q93m9k
 * @version : 2024.03.02
 * @see :
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.datasource")
public class DmsDataSourceProperties {

    // 통합운영 DB 설정
    private Map<String, Master> opmds;

    // 매출수집 DB 설정
    private Map<String, Master> opslc;

    // DMS DB 설정
    private Map<String, Master> opdms;

    @Getter
    @Setter
    public static class Master {

        private String url;

        private String username;

        private String driverClassName;

        private Hakari hikari;

        @Getter
        @Setter
        public static class Hakari {

            private boolean registerMbeans;

            private int minimumIdle;

            private int maximumPoolSize;
        }
    }
}
