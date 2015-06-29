package com.nicusa;

import com.nicusa.controller.SecurityController;
import com.nicusa.domain.UserProfile;
import com.nicusa.security.UserProfileSignInAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.web.context.request.NativeWebRequest;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {


  @Inject
  private DataSource dataSource;

  @Inject
  private SecurityController securityController;

  @Autowired
  public void registerAuthentication(AuthenticationManagerBuilder auth) throws Exception {
    auth.jdbcAuthentication()
      .dataSource(dataSource)
      .usersByUsernameQuery("select userid, userid, true from userprofile where userid = ?")
      .authoritiesByUsernameQuery("select 'ROLE_USER' from DUAL")
      .passwordEncoder(passwordEncoder());
  }


  @Override
  public void configure(WebSecurity web) {
    web
      .ignoring()
      .antMatchers("/**/*.css", "/**/*.png", "/**/*.gif", "/**/*.jpg", "/**/*.js");

  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http.csrf().disable()
      .formLogin()
      .loginPage("/signin")
      .loginProcessingUrl("/signin/authenticate")
      .failureUrl("/signin?param.error=bad_credentials")
      .and()
      .logout()
      .logoutUrl("/signout")
      .deleteCookies("JSESSIONID")
      .and()
      .authorizeRequests()
      .antMatchers("/search/**", "/signin/**", "/error", "/signup/**", "/autocomplete", "/drug/**", "/enforcement/**", "/event/**",
        "/recalls/**", "/user/**", "/", "/index.html" ,"/recalls" ,"/drug/recalls", "/drug/enforcements").permitAll()
      .antMatchers("/**").authenticated()
      .and()
      .rememberMe();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();
  }

  @Bean
  public RequestCache requestCache() {
    return new HttpSessionRequestCache();
  }

  @Bean
  public SignInAdapter signInAdapter() {
    return new UserProfileSignInAdapter();
  }

  @Bean
  public ProviderSignInUtils providerSignInUtils() {
    return new ProviderSignInUtils();
  }

}
