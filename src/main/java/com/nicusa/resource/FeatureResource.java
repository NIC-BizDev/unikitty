package com.nicusa.resource;

import org.springframework.hateoas.ResourceSupport;

import java.util.Collection;

public class FeatureResource extends ResourceSupport {

  private String requestText;
  private Collection<VoteResource> voteResources;
  
  public String getRequestText() {
    return requestText;
  }

  public void setRequestText(String requestText) {
    this.requestText = requestText;
  }

  public Collection<VoteResource> getVoteResources() {
    return voteResources;
  }

  public void setVoteResources(Collection<VoteResource> voteResources) {
    this.voteResources = voteResources;
  }

}
