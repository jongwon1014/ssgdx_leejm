/**
 * @project : 신세계백화점차세대프로젝트
 * @packageName : com.shinsegae.ssgdx.rst.config
 * @fileName : SlcDbConfiguration.java
 * @author : q93m9k
 * @date : 2024.02.29
 * @description :
 * 
 * COPYRIGHT ©2024 SHINSEGAE. ALL RIGHTS RESERVED.
 * 
 * <pre>
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024.02.29       q93m9k              최초 생성
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
 * @version : 2024.02.29
 * @see :
 */
@RequiredArgsConstructor
@Configuration
public class DmsDbConfiguration {

    private final DmsDataSourceProperties comDataSourceProperties;

    @Bean(name = "dmsDbHicariConfig")
    public HikariConfig slcDbHicariConfig(){

        DmsDataSourceProperties.Master mProps = comDataSourceProperties.getOpdms().get("master");
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

    @Bean(name = "dmsDbDataSource", destroyMethod = "close")
    public DataSource dmsDbDataSource(@Qualifier("dmsDbHicariConfig") HikariConfig dmsDbHicariConfig){
        return new HikariDataSource(dmsDbHicariConfig);
    }

    @Bean(name = "dmsDbSqlSessionFactory")
    public SqlSessionFactory dmsDbSqlSessionFactory(@Qualifier("dmsDbDataSource") DataSource dmsDbDataSource)
            throws Exception{
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dmsDbDataSource);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources("sql/*/*.xml")); // mapper
        //sessionFactory.setMapperLocations(resolver.getResources("sql/ldi/sql-ldi-ldi0001.xml")); // mapper
        // 파일
        // 로드
        return sessionFactory.getObject();
    }

    @Bean(name = "dmsDbSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(
            @Qualifier("dmsDbSqlSessionFactory") SqlSessionFactory dmsDbSqlSessionFactory) throws Exception{
        final SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(dmsDbSqlSessionFactory);
        return sqlSessionTemplate;
    }

    @Bean(name = "dmsDbTxManager")
    public PlatformTransactionManager dmsDbTxManager(@Qualifier("dmsDbDataSource") DataSource dmsDbDataSource){
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(dmsDbDataSource);
        dataSourceTransactionManager.setNestedTransactionAllowed(true); // nested
        return dataSourceTransactionManager;
    }
}
