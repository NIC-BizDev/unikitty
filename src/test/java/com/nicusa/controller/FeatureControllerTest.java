package com.nicusa.controller;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import javax.persistence.EntityManager;

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

import com.nicusa.assembler.FeatureAssembler;
import com.nicusa.domain.Feature;
import com.nicusa.resource.FeatureResource;

@RunWith(MockitoJUnitRunner.class)
public class FeatureControllerTest {

  @InjectMocks
  private FeatureController featureController;

  @Mock
  private FeatureAssembler featureAssembler;
  
  @Mock
  private EntityManager entityManager;
  
  @Test
  public void testGetFeature() {
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    
    Feature feature = new Feature();
    feature.setId(1L);
    feature.setTitle("TEST TITLE");
    feature.setRequestText("TEST REQUEST TEXT");

    FeatureResource featureResource = new FeatureResource();
    featureResource.setTitle("TEST TITLE");
    featureResource.setRequestText("TEST REQUEST TEXT");
    featureResource.setNumberOfLikes(2);
    featureResource.setNumberOfDislikes(1);
    featureResource.add(linkTo(methodOn(FeatureController.class).getFeature(1L)).withSelfRel());
    
    when(entityManager.find(Feature.class, 1L)).thenReturn(feature);
    when(featureAssembler.toResource(feature)).thenReturn(featureResource);

    ResponseEntity<FeatureResource> featureResponseEntity = featureController.getFeature(1L);
    assertThat(featureResponseEntity.getStatusCode(), is(HttpStatus.OK));
    FeatureResource controllerReturnedFeature = featureResponseEntity.getBody();
    assertThat(controllerReturnedFeature.getLink("self").getHref(), is("http://localhost/feature/1"));
    assertThat(controllerReturnedFeature.getTitle(), is("TEST TITLE"));
    assertThat(controllerReturnedFeature.getRequestText(), is("TEST REQUEST TEXT"));
    assertThat(controllerReturnedFeature.getNumberOfLikes(), is(2));
    assertThat(controllerReturnedFeature.getNumberOfDislikes(), is(1));
  }

  @Test
  public void testGetFeatureNotFound() {
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    
    when(entityManager.find(Feature.class, 1L)).thenReturn(null);
    ResponseEntity<FeatureResource> featureResourceResponseEntity = featureController.getFeature(1L);
    assertThat(HttpStatus.NOT_FOUND, is(featureResourceResponseEntity.getStatusCode()));
    assertThat(featureResourceResponseEntity.getBody(), is(nullValue()));    
  }
  
}
