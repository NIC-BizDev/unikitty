package com.nicusa.converter;

import com.nicusa.controller.DrugController;
import com.nicusa.domain.Drug;
import com.nicusa.domain.Portfolio;
import com.nicusa.resource.DrugResource;
import com.nicusa.resource.PortfolioResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.EntityManager;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RunWith(MockitoJUnitRunner.class)
public class PortfolioResourceToDomainConverterTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private DrugResourceToDomainConverter drugResourceToDomainConverter;

    @InjectMocks
    private PortfolioResourceToDomainConverter portfolioResourceToDomainConverter;

    @Test
    public void testConvert()  {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
        Portfolio persistedPortfolio = new Portfolio();
        when(entityManager.find(Portfolio.class, 1L)).thenReturn(persistedPortfolio);
        PortfolioResource portfolioResource = new PortfolioResource();
        Portfolio portfolio = portfolioResourceToDomainConverter.convert(portfolioResource);
        assertThat(portfolio, is(not(nullValue())));
    }

    @Test
    public void testConvertWithDrugs() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
        Portfolio persistedPortfolio = new Portfolio();
        Drug drug = new Drug();
        when(entityManager.find(Portfolio.class, 1L)).thenReturn(persistedPortfolio);
        when(drugResourceToDomainConverter.convert(any(DrugResource.class))).thenReturn(drug);
        PortfolioResource portfolioResource = new PortfolioResource();
        portfolioResource.add(linkTo(methodOn(DrugController.class).get(1L)).withRel("drugs"));
        Portfolio portfolio = portfolioResourceToDomainConverter.convert(portfolioResource);
        assertThat(portfolio, is(not(nullValue())));
    }
}
