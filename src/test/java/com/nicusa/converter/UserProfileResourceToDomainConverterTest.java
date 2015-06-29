package com.nicusa.converter;

import com.nicusa.controller.PortfolioController;
import com.nicusa.controller.UserProfileController;
import com.nicusa.domain.Portfolio;
import com.nicusa.domain.UserProfile;
import com.nicusa.resource.PortfolioResource;
import com.nicusa.resource.UserProfileResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.EntityManager;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RunWith(MockitoJUnitRunner.class)
public class UserProfileResourceToDomainConverterTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private PortfolioResourceToDomainConverter portfolioResourceToDomainConverter;

    @Mock
    private DrugResourceToDomainConverter drugResourceToDomainConverter;

    @InjectMocks
    private UserProfileResourceToDomainConverter userProfileResourceToDomainConverter;

    @Test
    public void testConvert() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
        UserProfile persistedProfile = new UserProfile();
        Portfolio portfolio = new Portfolio();
        when(entityManager.find(UserProfile.class, 1L)).thenReturn(persistedProfile);
        when(portfolioResourceToDomainConverter.convert(any(PortfolioResource.class))).thenReturn(portfolio);
        UserProfileResource userProfileResource = new UserProfileResource();
        userProfileResource.add(linkTo(methodOn(UserProfileController.class).getUserProfile(1L)).withSelfRel());
        userProfileResource.add(linkTo(methodOn(PortfolioController.class).getPortfolio(1L)).withRel("portfolio"));
        UserProfile userProfile = userProfileResourceToDomainConverter.convert(userProfileResource);
        assertThat(userProfile, is(persistedProfile));
    }



}
