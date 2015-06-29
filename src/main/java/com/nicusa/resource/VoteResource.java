package com.nicusa.resource;

import org.springframework.hateoas.ResourceSupport;

public class VoteResource extends ResourceSupport {

  private Boolean like;

  public Boolean getLike() {
    return like;
  }

  public void setLike(Boolean like) {
    this.like = like;
  }

}
