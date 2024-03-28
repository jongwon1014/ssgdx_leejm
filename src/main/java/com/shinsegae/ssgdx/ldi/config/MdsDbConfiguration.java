/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.rst.config
 * @fileName : MdsDbConfiguration.java
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
package com.shinsegae.ssgdx.ldi.config;

import javax.sql.DataSource;

import com.shinsegae.ssgdx.ldi.config.properties.DmsDataSourceProperties;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.RequiredArgsConstructor;

/**
 * <pre>
 * 
 * </pre>
 * 
 * @author : q93m9k
 * @version : 2024.03.02
 * @see :
 */
@RequiredArgsConstructor
@Configuration
public class MdsDbConfiguration {

    private final DmsDataSourceProperties comDataSourceProperties;

    @Bean(name = "mdsDbHicariConfig")
    public HikariConfig mdsDbHicariConfig(){

        DmsDataSourceProperties.Master mProps = comDataSourceProperties.getOpmds().get("master");
        DmsDataSourceProperties.Master.Hakari hProps = mProps.getHikari();

        HikariConfig hCf = new HikariConfig();
        hCf.setDriverClassName(mProps.getDriverClassName());
        hCf.setJdbcUrl(mProps.getUrl());
        hCf.setUsername(mProps.getUsername());
        hCf.setRegisterMbeans(hProps.isRegisterMbeans());
        hCf.setMinimumIdle(hProps.getMinimumIdle());
        hCf.setMaximumPoolSize(hProps.getMaximumPoolSize());
        hCf.setAutoCommit(false);
        return hCf;
    }

    @Bean(name = "mdsDbDataSource", destroyMethod = "close")
    public DataSource mdsDbDataSource(@Qualifier("mdsDbHicariConfig") HikariConfig mdsDbHicariConfig){
        return new HikariDataSource(mdsDbHicariConfig);
    }

    @Bean(name = "mdsDbSqlSessionFactory")
    public SqlSessionFactory mdsDbSqlSessionFactory(@Qualifier("mdsDbDataSource") DataSource mdsDbDataSource)
            throws Exception{
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(mdsDbDataSource);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources("sql/*/*.xml")); // mapper
        //sessionFactory.setMapperLocations(resolver.getResources("sql/ldi/sql-ldi-ldi0001.xml")); // mapper
        // 파일
        // 로드
        return sessionFactory.getObject();
    }

    @Bean(name = "mdsDbSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(
            @Qualifier("mdsDbSqlSessionFactory") SqlSessionFactory mdsDbSqlSessionFactory) throws Exception{
        final SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(mdsDbSqlSessionFactory);
        return sqlSessionTemplate;
    }

    @Bean(name = "mdsDbTxManager")
    public PlatformTransactionManager mdsDbTxManager(@Qualifier("mdsDbDataSource") DataSource mdsDbDataSource){
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(mdsDbDataSource);
        dataSourceTransactionManager.setNestedTransactionAllowed(true); // nested
        return dataSourceTransactionManager;
    }
}
