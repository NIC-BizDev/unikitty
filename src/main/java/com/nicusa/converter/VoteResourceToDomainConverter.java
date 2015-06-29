package com.nicusa.converter;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nicusa.controller.SecurityController;
import com.nicusa.domain.UserProfile;
import com.nicusa.domain.Vote;
import com.nicusa.resource.FeatureResource;
import com.nicusa.resource.VoteResource;

@Component
public class VoteResourceToDomainConverter extends ResourceToDomainConverter<VoteResource, Vote> {
  @PersistenceContext
  private EntityManager entityManager;
  
  @Autowired
  private FeatureResourceToDomainConverter featureConverter;

  @Autowired
  private SecurityController securityController;
  
  @Override
  public Vote convert(VoteResource voteResource) {
    Vote vote = new Vote();
    vote.setLike(voteResource.getLike());
    
    FeatureResource featureResource = new FeatureResource();
    featureResource.add(featureResource.getLink("feature").withRel("self"));
    vote.setFeature(featureConverter.convert(featureResource));
    
    UserProfile userProfile = entityManager.find(UserProfile.class, securityController.getAuthenticatedUserProfileId());
    vote.setUserProfile(userProfile);

    return vote;
  }

}
