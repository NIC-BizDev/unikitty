package com.nicusa.controller;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nicusa.assembler.FeatureAssembler;
import com.nicusa.domain.Feature;
import com.nicusa.resource.FeatureResource;

@RestController
public class FeatureController {

  @PersistenceContext
  private EntityManager entityManager;
  
  @Autowired 
  private FeatureAssembler featureAssembler;
  
  @ResponseBody
  @RequestMapping(value = "/feature/{id}", method = RequestMethod.GET, produces = "application/hal+json")
  public ResponseEntity<FeatureResource> getFeature(@PathVariable("id") Long id) {
    Feature feature = entityManager.find(Feature.class, id);
    if (feature == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(featureAssembler.toResource(feature), HttpStatus.OK);
  }  
  
  @ResponseBody
  @RequestMapping(value = "/features", method = RequestMethod.GET, produces = "application/hal+json")
  public ResponseEntity<List<FeatureResource>> getFeatures() {
    Query query = entityManager.createQuery("SELECT f FROM Feature f");
    List<Feature> features = query.getResultList();

    if (features == null || features.size() == 0) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } 

    return new ResponseEntity<>(featureAssembler.toResources(features), HttpStatus.OK);
  }  
}
