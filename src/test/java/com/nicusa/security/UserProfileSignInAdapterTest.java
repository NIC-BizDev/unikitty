package com.nicusa.security;

import com.nicusa.controller.SecurityController;
import com.nicusa.domain.UserProfile;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.social.connect.Connection;
import org.springframework.web.context.request.NativeWebRequest;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserProfileSignInAdapterTest {

  @Mock
  private EntityManager entityManager;

  @Mock
  private RequestCache requestCache;

  @Mock
  private SecurityController securityController;

  @InjectMocks
  private UserProfileSignInAdapter userProfileSignInAdapter;

  @Test
  public void testSignIn() {
    String userId = "unikitty";
    Connection<?> mockConnection = mock(Connection.class);
    NativeWebRequest mockNativeWebRequest = mock(NativeWebRequest.class);
    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
    HttpSession httpSession = mock(HttpSession.class);
    TypedQuery<UserProfile> mockTypedUserProfileQuery = mock(TypedQuery.class);
    UserProfile mockUserProfile = mock(UserProfile.class);

    when(mockNativeWebRequest.getNativeRequest(HttpServletRequest.class)).thenReturn(httpServletRequest);
    when(mockNativeWebRequest.getNativeResponse(HttpServletResponse.class)).thenReturn(httpServletResponse);
    when(httpServletRequest.getSession(false)).thenReturn(httpSession);
    when(entityManager.createQuery(any(String.class), eq(UserProfile.class))).thenReturn(mockTypedUserProfileQuery);
    when(mockTypedUserProfileQuery.getSingleResult()).thenReturn(mockUserProfile);
    when(requestCache.getRequest(httpServletRequest, httpServletResponse)).thenReturn(null);


    String redirectUrl = userProfileSignInAdapter.signIn(userId, mockConnection, mockNativeWebRequest);

    assertThat(redirectUrl, is(nullValue()));
  }


  @Test
  public void testSignInNoSavedRequest() {
    String userId = "unikitty";
    Connection<?> mockConnection = mock(Connection.class);
    NativeWebRequest mockNativeWebRequest = mock(NativeWebRequest.class);
    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
    HttpSession httpSession = mock(HttpSession.class);
    TypedQuery<UserProfile> mockTypedUserProfileQuery = mock(TypedQuery.class);
    UserProfile mockUserProfile = mock(UserProfile.class);
    SavedRequest mockSavedRequest = mock(SavedRequest.class);

    when(mockNativeWebRequest.getNativeRequest(HttpServletRequest.class)).thenReturn(httpServletRequest);
    when(mockNativeWebRequest.getNativeResponse(HttpServletResponse.class)).thenReturn(httpServletResponse);
    when(httpServletRequest.getSession(false)).thenReturn(httpSession);
    when(entityManager.createQuery(any(String.class), eq(UserProfile.class))).thenReturn(mockTypedUserProfileQuery);
    when(mockTypedUserProfileQuery.getSingleResult()).thenReturn(mockUserProfile);
    when(requestCache.getRequest(httpServletRequest, httpServletResponse)).thenReturn(mockSavedRequest);
    when(mockSavedRequest.getRedirectUrl()).thenReturn("/");


    String redirectUrl = userProfileSignInAdapter.signIn(userId, mockConnection, mockNativeWebRequest);

    assertThat(redirectUrl, is("/"));
  }
}
