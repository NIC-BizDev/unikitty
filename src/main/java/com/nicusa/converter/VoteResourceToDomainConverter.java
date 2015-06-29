package com.nicusa.converter;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nicusa.controller.VoteController;
import com.nicusa.domain.Vote;
import com.nicusa.resource.FeatureResource;
import com.nicusa.resource.VoteResource;

@Component
public class VoteResourceToDomainConverter extends ResourceToDomainConverter<VoteResource, Vote> {
  @PersistenceContext
  private EntityManager entityManager;
  
  @Autowired
  private FeatureResourceToDomainConverter featureConverter;

  @Override
  public Vote convert(VoteResource voteResource) {
    Vote vote = entityManager.find(Vote.class, extractIdFromLink(VoteController.class,
                                                                 voteResource, "getVote", Long.class));

    if (vote == null) {
      vote = new Vote();
    }
    
    vote.setLike(voteResource.getLike());
    
    FeatureResource featureResource = new FeatureResource();
    featureResource.add(voteResource.getLink("feature").withRel("self"));
    vote.setFeature(featureConverter.convert(featureResource));
    
    return vote;
  }

}
