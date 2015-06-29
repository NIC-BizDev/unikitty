package com.nicusa.resource;

import org.springframework.hateoas.ResourceSupport;

public class VoteResource extends ResourceSupport {

  private Boolean like;
  private UserProfileResource userProfileResource;

  public Boolean getLike() {
    return like;
  }

  public void setLike(Boolean like) {
    this.like = like;
  }

  public UserProfileResource getUserProfileResource() {
    return userProfileResource;
  }

  public void setUserProfileResource(UserProfileResource userProfileResource) {
    this.userProfileResource = userProfileResource;
  }

}
