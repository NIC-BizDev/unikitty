package com.nicusa.security;

import com.nicusa.controller.SecurityController;
import com.nicusa.domain.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.web.context.request.NativeWebRequest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class UserProfileSignInAdapter implements SignInAdapter {

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private RequestCache requestCache;

  @Autowired
  private SecurityController securityController;

  @Override
  public String signIn(String userId, Connection<?> connection, NativeWebRequest request) {
    HttpServletRequest nativeReq = request.getNativeRequest(HttpServletRequest.class);
    HttpServletResponse nativeRes = request.getNativeResponse(HttpServletResponse.class);
    HttpSession session = nativeReq.getSession(false);

    TypedQuery<UserProfile> userProfileByUserIdQuery = entityManager
      .createQuery("select u from UserProfile u where u.userId = :userId", UserProfile.class);
    userProfileByUserIdQuery.setParameter("userId", userId);
    UserProfile userProfile = userProfileByUserIdQuery.getSingleResult();

    securityController.signin(userProfile);

    SavedRequest saved = requestCache.getRequest(nativeReq, nativeRes);
    if (saved == null) {
      return null;
    } else {
      requestCache.removeRequest(nativeReq, nativeRes);
      if (session != null) {
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
      }
      return saved.getRedirectUrl();
    }
  }
}
