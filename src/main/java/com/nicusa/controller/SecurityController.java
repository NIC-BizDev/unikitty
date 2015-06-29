package com.nicusa.controller;

import com.nicusa.domain.Portfolio;
import com.nicusa.domain.UserProfile;
import com.nicusa.resource.UserProfileResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.support.RequestContext;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpSession;
import java.util.UUID;

@Controller
public class SecurityController {


  public static final String LOGGED_IN_USER_PROFILE_ID = "LOGGED_IN_USER_PROFILE_ID";

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private ProviderSignInUtils providerSignInUtils;

  @RequestMapping(value = "/signin", method = RequestMethod.GET)
  public String signin() {
    return "signin";
  }

  @RequestMapping(value="/signup", method= RequestMethod.GET)
  @Transactional
  public String signup(WebRequest request) {
    Connection<?> connection = providerSignInUtils.getConnectionFromSession(request);
    UserProfile userProfile = new UserProfile();
    userProfile.setUserId(UUID.randomUUID().toString());
    org.springframework.social.connect.UserProfile socialUserProfile = connection.fetchUserProfile();
    userProfile.setName(socialUserProfile.getName());
    userProfile.setEmailAddress(socialUserProfile.getEmail());
    entityManager.persist(userProfile);
    Portfolio portfolio = new Portfolio();
    entityManager.persist(new Portfolio());
    userProfile.setPortfolio(portfolio);
    entityManager.merge(userProfile);


    signin(userProfile);

    providerSignInUtils.doPostSignUp(userProfile.getUserId(), request);
    return "redirect:/";
  }

  public void signin(UserProfile userProfile) {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userProfile.getId(), null, null));
  }

  public Long getAuthenticatedUserProfileId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if(authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      return UserProfileResource.ANONYMOUS_USER_PROFILE_ID;
    } else {
      return (Long)authentication.getPrincipal();
    }
  }

}
