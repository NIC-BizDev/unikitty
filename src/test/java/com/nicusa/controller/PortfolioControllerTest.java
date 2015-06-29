package com.nicusa.controller;

import com.nicusa.assembler.PortfolioAssembler;
import com.nicusa.converter.PortfolioResourceToDomainConverter;
import com.nicusa.domain.Drug;
import com.nicusa.domain.Portfolio;
import com.nicusa.domain.UserProfile;
import com.nicusa.resource.PortfolioResource;
import com.nicusa.resource.UserProfileResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PortfolioControllerTest {

  @Mock
  private EntityManager entityManager;

  @Mock
  private PortfolioAssembler portfolioAssembler;

  @Mock
  private SecurityController securityController;

  @Mock
  public PortfolioResourceToDomainConverter portfolioResourceToDomainConverter;

  @InjectMocks
  private PortfolioController portfolioController;

  @Before
  public void before() {
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
  }

  @After
  public void after() {
    RequestContextHolder.setRequestAttributes(null);
  }

  @Test
  public void testGetPortfolio() throws Exception {
    Portfolio portfolio = new Portfolio();
    when(entityManager.find(Portfolio.class, 1L)).thenReturn(portfolio);
    PortfolioResource portfolioResource = new PortfolioResource();
    when(portfolioAssembler.toResource(portfolio)).thenReturn(portfolioResource);
    ResponseEntity<PortfolioResource> portfolioResourceResponseEntity = portfolioController.getPortfolio(1L);
    assertThat(HttpStatus.OK, is(portfolioResourceResponseEntity.getStatusCode()));
    assertThat(portfolioResource, is(portfolioResourceResponseEntity.getBody()));
  }

  @Test
  public void testGetPortfolioNotFound() {
    when(entityManager.find(Portfolio.class, 1L)).thenReturn(null);
    ResponseEntity<PortfolioResource> portfolioResourceResponseEntity = portfolioController.getPortfolio(1L);
    assertThat(HttpStatus.NOT_FOUND, is(portfolioResourceResponseEntity.getStatusCode()));
    assertThat(portfolioResourceResponseEntity.getBody(), is(nullValue()));
  }

  @Test
  public void testCreatePortfolio() {
    PortfolioResource portfolioResource = new PortfolioResource();
    Portfolio portfolio = new Portfolio();
    portfolio.setId(1L);
    when(securityController.getAuthenticatedUserProfileId()).thenReturn(1L);
    when(portfolioResourceToDomainConverter.convert(portfolioResource)).thenReturn(portfolio);
    ResponseEntity<?> responseEntity = portfolioController.create(portfolioResource);
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
  }

  @Test
  public void testCreateAnonymousShouldReturnUnAuthorized() {
    PortfolioResource portfolioResource = new PortfolioResource();
    when(securityController.getAuthenticatedUserProfileId()).thenReturn(UserProfileResource.ANONYMOUS_USER_PROFILE_ID);
    ResponseEntity<?> responseEntity = portfolioController.create(portfolioResource);
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
  }

  @Test
  public void testUpdatePortfolioResourceAnonymousUnAuthorized() {
    PortfolioResource portfolioResource = new PortfolioResource();
    when(securityController.getAuthenticatedUserProfileId()).thenReturn(UserProfileResource.ANONYMOUS_USER_PROFILE_ID);
    ResponseEntity<?> responseEntity = portfolioController.updatePortfolio(1L, portfolioResource);
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
  }

  @Test
  public void testUpdatePortfolioNoContent() {
    PortfolioResource portfolioResource = new PortfolioResource();
    when(securityController.getAuthenticatedUserProfileId()).thenReturn(1L);
    when(entityManager.find(Portfolio.class, 1L)).thenReturn(null);
    ResponseEntity<?> responseEntity = portfolioController.updatePortfolio(1L, portfolioResource);
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.NO_CONTENT));
  }

  @Test
  public void testUpdatePortfolioForADifferentUserProfile() {
    PortfolioResource portfolioResource = new PortfolioResource();
    UserProfile userProfile = new UserProfile();
    userProfile.setId(2L);
    Portfolio portfolio = new Portfolio();
    portfolio.setId(1L);
    when(securityController.getAuthenticatedUserProfileId()).thenReturn(1L);
    when(entityManager.find(Portfolio.class, 1L)).thenReturn(portfolio);
    ResponseEntity<?> responseEntity = portfolioController.updatePortfolio(1L, portfolioResource);
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
  }

  @Test
  public void testUpdatePortfolio() {
    Drug drug = new Drug();
    drug.setName("cotton candy");
    drug.setUnii("cotton candy");
    Collection<Drug> drugCollection = new ArrayList<Drug>();
    drugCollection.add(drug);
    PortfolioResource portfolioResource = new PortfolioResource();
    UserProfile userProfile = new UserProfile();
    userProfile.setId(1L);
    Portfolio portfolio = new Portfolio();
    portfolio.setId(1L);
    portfolio.setName("unikitty");
    portfolio.setUserProfile(userProfile);
    portfolio.setDrugs(drugCollection);
    when(securityController.getAuthenticatedUserProfileId()).thenReturn(1L);
    when(entityManager.find(Portfolio.class, 1L)).thenReturn(portfolio);
    when(portfolioResourceToDomainConverter.convert(portfolioResource)).thenReturn(portfolio);
    ResponseEntity<?> responseEntity = portfolioController.updatePortfolio(1L, portfolioResource);
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
  }

  @Test
  public void testDeletePortfolioAsAnonymousUser() {
    when(securityController.getAuthenticatedUserProfileId()).thenReturn(UserProfileResource.ANONYMOUS_USER_PROFILE_ID);
    ResponseEntity<?> responseEntity = portfolioController.delete(1L);
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
  }

  @Test
  public void testDeletePortfolioNoContent() {
    when(securityController.getAuthenticatedUserProfileId()).thenReturn(1L);
    when(entityManager.find(Portfolio.class, 1L)).thenReturn(null);
    ResponseEntity<?> responseEntity = portfolioController.delete(1L);
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.NO_CONTENT));
  }

  @Test
  public void testDeletePortfolioForAnotherUserProfile() {
    UserProfile userProfile = new UserProfile();
    userProfile.setId(2L);
    Portfolio portfolio = new Portfolio();
    portfolio.setId(1L);
    portfolio.setUserProfile(userProfile);
    when(securityController.getAuthenticatedUserProfileId()).thenReturn(1L);
    when(entityManager.find(Portfolio.class, 1L)).thenReturn(portfolio);
    ResponseEntity<?> responseEntity = portfolioController.delete(1L);
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
  }

  @Test
  public void testDeletePortfolio() {
    UserProfile userProfile = new UserProfile();
    userProfile.setId(1L);
    Portfolio portfolio = new Portfolio();
    portfolio.setId(1L);
    portfolio.setUserProfile(userProfile);
    when(securityController.getAuthenticatedUserProfileId()).thenReturn(1L);
    when(entityManager.find(Portfolio.class, 1L)).thenReturn(portfolio);
    ResponseEntity<?> responseEntity = portfolioController.delete(1L);
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
  }

}
