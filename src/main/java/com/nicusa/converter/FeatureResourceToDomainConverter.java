package com.nicusa.converter;

/* import com.nicusa.controller.FeatureContoller; */
import com.nicusa.domain.Vote;
import com.nicusa.domain.Feature;
import com.nicusa.resource.FeatureResource;
import com.nicusa.resource.VoteResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;

@Component
public class FeatureResourceToDomainConverter extends ResourceToDomainConverter<FeatureResource, Feature> {

  @PersistenceContext
  private EntityManager entityManager;

  /*
    @Autowired
    private VoteResourceToDomainConverter voteResourceToDomainConverter;
  */

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
    feature.setRequestText(featureResource.getRequestText());

    Collection<Vote> votes = new ArrayList<>();
    if (featureResource.getVoteResources() != null) {
      for (VoteResource voteResource : featureResource.getVoteResources()) {
        Vote vote = new Vote();
        vote.setLike(voteResource.getLike());
        votes.add(vote);
      }
    }
    feature.setVotes(votes);
    return feature;
  }
  
}
