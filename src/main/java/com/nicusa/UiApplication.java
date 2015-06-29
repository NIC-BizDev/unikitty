package com.nicusa;

import com.nicusa.util.HttpSlurper;

import java.io.FileNotFoundException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@PropertySources({
  @PropertySource(value = "file:${sys:user.home}/.nic/unikitty.properties", ignoreResourceNotFound = true),
  @PropertySource(value = "file:${user.home}/.nic/unikitty.properties", ignoreResourceNotFound = true) })
public class UiApplication {

  private static final Logger log = LoggerFactory.getLogger(UiApplication.class);

  @Value("${keystore.file:}")
  private String keystoreFile;

  @Value("${keystore.pass:}")
  private String keystorePassword;

  @Value("${keystore.type:}")
  private String keystoreType;

  @Value("${keystore.alias:}")
  private String keystoreAlias;


  public static void main(String[] args) {
    SpringApplication.run(UiApplication.class, args);
  }

  @Bean
  public DocumentBuilder documentBuilder() throws ParserConfigurationException
  {
      DocumentBuilder builder = null;
      builder = documentBuilderFactory().newDocumentBuilder();
      return builder;
  }
  
  @Bean 
  public HttpSlurper slurper()
  {
      return new HttpSlurper();
  }
  
  public DocumentBuilderFactory documentBuilderFactory()
  {
      return DocumentBuilderFactory.newInstance();
  }
  
  @Bean
  public EmbeddedServletContainerCustomizer containerCustomizer() throws FileNotFoundException
  {
      String absoluteKeystoreFile = null;
      try {
          if(keystoreFile != null && keystoreFile.length() > 0)
          {
              log.info("Absolute keystore file: "+keystoreFile);
              absoluteKeystoreFile = ResourceUtils.getFile(keystoreFile).getAbsolutePath();
          }
      } catch (Exception e)
      {
          log.warn("Keystore not defined ");
      }


      final TomcatConnectorCustomizer customizer = new SslTomcatConnectionCustomizer(
              absoluteKeystoreFile, keystorePassword, keystoreType, keystoreAlias);

      return new EmbeddedServletContainerCustomizer() {

        @Override
        public void customize(ConfigurableEmbeddedServletContainer container) {
          if(container instanceof TomcatEmbeddedServletContainerFactory) {
            TomcatEmbeddedServletContainerFactory containerFactory = (TomcatEmbeddedServletContainerFactory) container;
            containerFactory.addConnectorCustomizers(customizer);
          }
        };
      };
  }
}
