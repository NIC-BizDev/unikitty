package com.nicusa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootApplication
@RestController
public class UiApplication {

  private static final Logger log = LoggerFactory.getLogger(UiApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(UiApplication.class, args);
  }

  @RequestMapping("/resource")
  public Map<String, Object> home() {
    Map<String, Object> model = new HashMap<>();
    model.put("id", UUID.randomUUID().toString());
    model.put("content", "Hello World");
    return model;
  }

  @Configuration
  @EnableWebSecurity
  @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
  @PropertySources({ 
    @PropertySource(value = "file:${sys:user.home}/.nic/unikitty.properties", ignoreResourceNotFound = true),
    @PropertySource(value = "file:${user.home}/.nic/unikitty.properties", ignoreResourceNotFound = true) })
  protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http.httpBasic().and().authorizeRequests()
        .antMatchers("/","/index.html"
                ,"/recalls"
                ,"/drug/recalls"
                ,"/drug/enforcements").permitAll()
        .anyRequest().fullyAuthenticated()
        .and().csrf().disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
      auth
        .inMemoryAuthentication()
        .withUser("user").password("password").roles("USER");
    }
  }

}
