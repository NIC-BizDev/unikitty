package com.nicusa.assembler;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import com.nicusa.controller.FeatureController;
import com.nicusa.controller.VoteController;
import com.nicusa.domain.Vote;
import com.nicusa.resource.VoteResource;

@Component
public class VoteAssembler extends ResourceAssemblerSupport<Vote, VoteResource> {
  
  public VoteAssembler() {
    super(VoteController.class, VoteResource.class);
  }
  
  public VoteResource toResource(Vote vote) {
    VoteResource voteResource = createResourceWithId(vote.getId(), vote);
    voteResource.setLike(vote.getLike());
    voteResource.add(linkTo(methodOn(FeatureController.class).getFeature(vote.getFeature().getId())).withRel("feature"));
    return voteResource;
  }

}
