package com.nicusa.converter;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.nicusa.controller.FeatureController;
import com.nicusa.domain.Feature;
import com.nicusa.domain.UserProfile;
import com.nicusa.domain.Vote;
import com.nicusa.resource.FeatureResource;

@RunWith(MockitoJUnitRunner.class)
public class FeatureResourceToDomainConverterTest {
  @Mock
  private EntityManager entityManager;

  @InjectMocks
  private FeatureResourceToDomainConverter featureResourceToDomainConverter;

  @Test
  public void testConvert() {
    Feature persistedFeature = new Feature();
    persistedFeature.setTitle("TEST TITLE");
    persistedFeature.setRequestText("TEST REQUEST TEXT");
    //persistedFeature.setVotes(createFeatureVotes(persistedFeature));

    when(entityManager.find(Feature.class, 1L)).thenReturn(persistedFeature);
    FeatureResource featureResource = new FeatureResource();
    featureResource.add(linkTo(methodOn(FeatureController.class).getFeature(1L)).withSelfRel());
    
    Feature feature = featureResourceToDomainConverter.convert(featureResource);
    assertThat(feature, is(persistedFeature));
  }

  /*
  private List<Vote> createFeatureVotes(Feature feature) {
    List<Vote> votes = new ArrayList<Vote>();
    
    Vote vote1 = new Vote();
    vote1.setFeature(feature);
    vote1.setUserProfile(createUserProfile(1L, "TEST USER1"));
    vote1.setLike(Boolean.TRUE);
    votes.add(vote1);
    
    Vote vote2 = new Vote();
    vote2.setFeature(feature);
    vote2.setUserProfile(createUserProfile(2L, "TEST USER2"));
    vote2.setLike(Boolean.FALSE);
    votes.add(vote2);
    
    Vote vote3 = new Vote();
    vote3.setFeature(feature);
    vote3.setUserProfile(createUserProfile(3L, "TEST USER3"));
    vote3.setLike(Boolean.TRUE);
    votes.add(vote3);
    
    return votes;
  }
  */
  
  private UserProfile createUserProfile(Long id, String name) {
    UserProfile persistedProfile = new UserProfile();
    persistedProfile.setId(id);
    persistedProfile.setName(name);
    return persistedProfile;
  }
}
