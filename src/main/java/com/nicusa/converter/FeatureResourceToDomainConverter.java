package com.nicusa.converter;

/* import com.nicusa.controller.FeatureContoller; */
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;

import com.nicusa.domain.Feature;
import com.nicusa.resource.FeatureResource;

@Component
public class FeatureResourceToDomainConverter extends ResourceToDomainConverter<FeatureResource, Feature> {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Feature convert(FeatureResource featureResource) {
    Feature feature = null;
    /*
      feature = entityManager.find(Feature.class, extractIdFromLink(FeatureController.class,
        featureResource, "getFeature", Principal.class, Long.class));
    */
    if (feature == null) {
      feature = new Feature();
    }
    
    feature.setTitle(featureResource.getTitle());
    feature.setRequestText(featureResource.getRequestText());
    return feature;
  }
  
}
