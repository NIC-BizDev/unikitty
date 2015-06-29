package com.nicusa.controller;

import com.nicusa.assembler.UserProfileAssembler;
import com.nicusa.converter.UserProfileResourceToDomainConverter;
import com.nicusa.domain.UserProfile;
import com.nicusa.resource.UserProfileResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.EntityManager;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RunWith(MockitoJUnitRunner.class)
public class UserProfileControllerTest {

  @InjectMocks
  private UserProfileController userProfileController;

  @Mock
  private EntityManager entityManager;

  @Mock
  private UserProfileAssembler userProfileAssembler;

  @Mock
  private SecurityController securityController;

  @Mock
  private UserProfileResourceToDomainConverter userProfileResourceToDomainConverter;

  @Test
  public void testGetUserNotAuthenticated() {
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    when(securityController.getAuthenticatedUserProfileId()).thenReturn(UserProfileResource.ANONYMOUS_USER_PROFILE_ID);
    assertThat(userProfileController.getUser().getStatusCode(), is(HttpStatus.FOUND));
    assertThat(userProfileController.getUser().getHeaders().getLocation().getPath(), is("/user/0"));
  }

  @Test
  public void testGetUserAuthorized() {
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(1L, null, null));
    when(securityController.getAuthenticatedUserProfileId()).thenReturn(1L);
    assertThat(userProfileController.getUser().getHeaders().getLocation().getPath(), is("/user/1"));
  }

  @Test
  public void testGetUserProfileAnonymous() throws Exception {
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    ResponseEntity<UserProfileResource> userProfileResponseEntity = userProfileController.getUserProfile(0L);
    assertThat(userProfileResponseEntity.getStatusCode(), is(HttpStatus.OK));
    assertThat(userProfileResponseEntity.getBody(), is(not(nullValue())));
    assertThat(userProfileResponseEntity.getBody(), instanceOf(UserProfileResource.class));
    assertThat(userProfileResponseEntity.getBody().getName(), is("anonymous"));
    assertThat(userProfileResponseEntity.getBody().getLink("self").getHref(), is("http://localhost/user/0"));
  }

  @Test
  public void testGetUserProfileFromTheCorrectUser() throws Exception {
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    UserProfile userProfile = new UserProfile();
    userProfile.setId(1L);
    UserProfileResource userProfileResource = new UserProfileResource();
    userProfileResource.add(linkTo(methodOn(UserProfileController.class).getUserProfile(1L)).withSelfRel());
    when(securityController.getAuthenticatedUserProfileId()).thenReturn(1L);
    when(entityManager.find(UserProfile.class, 1L)).thenReturn(userProfile);
    when(userProfileAssembler.toResource(userProfile)).thenReturn(userProfileResource);

    ResponseEntity<UserProfileResource> userProfileResponseEntity = userProfileController.getUserProfile(1L);
    assertThat(userProfileResponseEntity.getStatusCode(), is(HttpStatus.OK));
    assertThat(userProfileResponseEntity.getBody(), is(not(nullValue())));
    assertThat(userProfileResponseEntity.getBody(), instanceOf(UserProfileResource.class));
    assertThat(userProfileResponseEntity.getBody().getLink("self").getHref(), is("http://localhost/user/1"));
  }

  @Test
  public void testGetUserProfileNotFound() {
    when(securityController.getAuthenticatedUserProfileId()).thenReturn(3L);
    when(entityManager.find(UserProfile.class, 1L)).thenReturn(null);
    ResponseEntity<?> userProfileResponseEntity = userProfileController.getUserProfile(3L);
    assertThat(userProfileResponseEntity.getStatusCode(), is(HttpStatus.NOT_FOUND));
  }

  @Test
  public void testGetUserProfileFromTheInCorrectUser() throws Exception {
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    UserProfile userProfile = new UserProfile();
    userProfile.setId(1L);
    when(securityController.getAuthenticatedUserProfileId()).thenReturn(2L);

    ResponseEntity<UserProfileResource> userProfileResponseEntity = userProfileController.getUserProfile(1L);
    assertThat(userProfileResponseEntity.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
  }

  @Test
  public void testCreateUserProfileUnauthorized() throws Exception {
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    UserProfileResource userProfileResource = new UserProfileResource();
    userProfileResource.add(linkTo(methodOn(UserProfileController.class).getUserProfile(1L)).withSelfRel());
    UserProfile userProfile = new UserProfile();
    userProfile.setId(1L);
    when(securityController.getAuthenticatedUserProfileId()).thenReturn(2L);
    when(userProfileResourceToDomainConverter.convert(userProfileResource)).thenReturn(userProfile);

    ResponseEntity<?> userProfileResponseEntity = userProfileController.createUserProfile(userProfileResource);
    assertThat(userProfileResponseEntity.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
  }

  @Test
  public void testCreateUserProfileAnonymous() throws Exception {
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    UserProfileResource userProfileResource = new UserProfileResource();
    userProfileResource.add(linkTo(methodOn(UserProfileController.class).getUserProfile(1L)).withSelfRel());
    UserProfile userProfile = new UserProfile();
    userProfile.setId(1L);
    when(securityController.getAuthenticatedUserProfileId()).thenReturn(0L);
    when(userProfileResourceToDomainConverter.convert(userProfileResource)).thenReturn(userProfile);

    ResponseEntity<?> userProfileResponseEntity = userProfileController.createUserProfile(userProfileResource);
    assertThat(userProfileResponseEntity.getStatusCode(), is(HttpStatus.UNAUTHORIZED));

  }

  @Test
  public void testCreateUserProfileCreated() throws Exception {
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    UserProfileResource userProfileResource = new UserProfileResource();
    userProfileResource.add(linkTo(methodOn(UserProfileController.class).getUserProfile(1L)).withSelfRel());
    UserProfile userProfile = new UserProfile();
    userProfile.setId(1L);
    when(securityController.getAuthenticatedUserProfileId()).thenReturn(1L);
    when(userProfileResourceToDomainConverter.convert(userProfileResource)).thenReturn(userProfile);

    ResponseEntity<?> userProfileResponseEntity = userProfileController.createUserProfile(userProfileResource);
    assertThat(userProfileResponseEntity.getStatusCode(), is(HttpStatus.CREATED));

  }
}
