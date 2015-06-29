package com.nicusa.domain;

import java.sql.SQLException;

import javax.persistence.SharedCacheMode;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
@Profile("mysql")
public class MySqlPersistenceConfiguration {

  @Autowired
  @Value("${mysql.username}") String username;

  @Autowired
  @Value("${mysql.password}") String password;

  @Autowired
  @Value("${mysql.jdbc.url}") String jdbcUrl;

  @Bean
  public DataSource dataSource() throws SQLException {
    DriverManagerDataSource ds = new DriverManagerDataSource();
    ds.setDriverClassName("com.mysql.jdbc.Driver");
    ds.setUsername(username);
    ds.setPassword(password);
    ds.setUrl(jdbcUrl);
    return ds;
  }

  @Bean
  public JpaVendorAdapter vendorAdapter() {
    EclipseLinkJpaVendorAdapter vendorAdapter = new EclipseLinkJpaVendorAdapter();
    vendorAdapter.setDatabase(Database.MYSQL);
    vendorAdapter.setGenerateDdl(true);
    return vendorAdapter;
  }


  @Bean
  public PlatformTransactionManager transactionManager() {
    return new JpaTransactionManager();
  }


  @Bean
  public TransactionTemplate transactionTemplate() {
    return new TransactionTemplate(transactionManager());
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws SQLException {
    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    factory.setJpaVendorAdapter(vendorAdapter());
    factory.setSharedCacheMode(SharedCacheMode.NONE);
    factory.setPackagesToScan("com.nicusa.domain");
    factory.getJpaPropertyMap().put("eclipselink.weaving", "false");
    factory.getJpaPropertyMap().put("eclipselink.logging.level", "FINE");
    factory.getJpaPropertyMap().put("eclipselink.validate-existence", "true");
    factory.getJpaPropertyMap().put("eclipselink.id-validation", "NULL");
    factory.getJpaPropertyMap().put("eclipselink.ddl-generation", "create-or-extend-tables");
    factory.setDataSource(dataSource());
    return factory;
  }
}
