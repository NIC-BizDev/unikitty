package com.nicusa.converter;

import static org.hamcrest.Matchers.is;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.nicusa.controller.FeatureController;
import com.nicusa.domain.Feature;
import com.nicusa.resource.FeatureResource;

@RunWith(MockitoJUnitRunner.class)
public class FeatureResourceToDomainConverterTest {
  @Mock
  private EntityManager entityManager;

  @InjectMocks
  private FeatureResourceToDomainConverter featureResourceToDomainConverter;

  @Test
  public void testConvert() {
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    Feature persistedFeature = new Feature();
    persistedFeature.setTitle("TEST TITLE");
    persistedFeature.setRequestText("TEST REQUEST TEXT");

    when(entityManager.find(Feature.class, 1L)).thenReturn(persistedFeature);
    FeatureResource featureResource = new FeatureResource();
    featureResource.add(linkTo(methodOn(FeatureController.class).getFeature(1L)).withSelfRel());
    
    Feature feature = featureResourceToDomainConverter.convert(featureResource);
    assertThat(feature, is(persistedFeature));
  }

}
