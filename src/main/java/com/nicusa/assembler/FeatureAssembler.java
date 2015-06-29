package com.nicusa.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import com.nicusa.controller.FeatureController;
import com.nicusa.controller.VoteController;
import com.nicusa.domain.Feature;
import com.nicusa.domain.Vote;
import com.nicusa.resource.FeatureResource;

@Component
public class FeatureAssembler extends ResourceAssemblerSupport<Feature, FeatureResource> {

  public FeatureAssembler() {
    super(FeatureController.class, FeatureResource.class);
  }
  
  @Override
  public FeatureResource toResource(Feature feature) {
    FeatureResource featureResource = createResourceWithId(feature.getId(), feature);
    featureResource.setTitle(feature.getTitle());
    featureResource.setRequestText(feature.getRequestText());
    
    Integer numberOfLikes = 0;
    Integer numberOfDislikes = 0;
    for (Vote current : feature.getVotes()) {
      if (current.getLike()) {
        numberOfLikes++;
      } else {
        numberOfDislikes++;
      }
    }

    featureResource.setNumberOfLikes(numberOfLikes);
    featureResource.setNumberOfDislikes(numberOfDislikes);

    featureResource.add(linkTo(methodOn(VoteController.class).voteLike(featureResource)).withRel("voteLike"));
    featureResource.add(linkTo(methodOn(VoteController.class).voteDislike(featureResource)).withRel("voteDislike"));
    return featureResource;
  }

}
