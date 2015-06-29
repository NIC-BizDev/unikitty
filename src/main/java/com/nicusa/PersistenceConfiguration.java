package com.nicusa;

import javax.persistence.SharedCacheMode;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
@Profile("local")
@EnableTransactionManagement
public class PersistenceConfiguration {

  @Bean
  public DataSource dataSource()  {
    EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
    return builder.setType(EmbeddedDatabaseType.HSQL).build();
  }

  @Bean
  public JpaVendorAdapter vendorAdapter() {
    EclipseLinkJpaVendorAdapter vendorAdapter = new EclipseLinkJpaVendorAdapter();
    vendorAdapter.setDatabase(Database.HSQL);
    vendorAdapter.setGenerateDdl(true);
    vendorAdapter.setShowSql(true);
    return vendorAdapter;
  }

  @Bean
  public PlatformTransactionManager transactionManager() {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    return transactionManager;
  }


  @Bean
  public TransactionTemplate transactionTemplate() {
    return new TransactionTemplate(transactionManager());
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory()  {
    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    factory.setJpaVendorAdapter(vendorAdapter());
    factory.setSharedCacheMode(SharedCacheMode.NONE);
    factory.setPackagesToScan("com.nicusa.domain");
    factory.getJpaPropertyMap().put("eclipselink.weaving", "false");
    factory.getJpaPropertyMap().put("eclipselink.logging.level", "FINE");
    factory.getJpaPropertyMap().put("eclipselink.validate-existence", "true");
    factory.getJpaPropertyMap().put("eclipselink.id-validation", "NULL");
    factory.setDataSource(dataSource());
    return factory;
  }


}
