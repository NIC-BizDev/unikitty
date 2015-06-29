package com.nicusa.assembler;

import com.nicusa.controller.PortfolioController;
import com.nicusa.controller.UserProfileController;
import com.nicusa.domain.UserProfile;
import com.nicusa.resource.UserProfileResource;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class UserProfileAssembler extends ResourceAssemblerSupport<UserProfile, UserProfileResource> {

  public UserProfileAssembler() {
    super(UserProfileController.class, UserProfileResource.class);
  }

  @Override
  public UserProfileResource toResource(UserProfile userProfile) {
    UserProfileResource userProfileResource = createResourceWithId(userProfile.getId(), userProfile);
    userProfileResource.setName(userProfile.getName());
    userProfileResource.setEmailAddress(userProfile.getEmailAddress());
    userProfileResource.setUserId(userProfile.getUserId());
    userProfileResource.set_id(userProfile.getId().toString());
    if (userProfile.getPortfolio() != null) {
      userProfileResource.add(linkTo(methodOn(PortfolioController.class).getPortfolio(userProfile.getPortfolio()
        .getId())).withRel("portfolio"));
    }
    return userProfileResource;
  }
}
