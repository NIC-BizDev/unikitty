package com.nicusa.resource;

import org.springframework.hateoas.ResourceSupport;

import java.util.Collection;

public class FeatureResource extends ResourceSupport {

  private String title;
  private String requestText;
  private Integer numberOfLikes;
  private Integer numberOfDislikes;
  
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getRequestText() {
    return requestText;
  }

  public void setRequestText(String requestText) {
    this.requestText = requestText;
  }

  public Integer getNumberOfLikes() {
    return numberOfLikes;
  }

  public void setNumberOfLikes(Integer numberOfLikes) {
    this.numberOfLikes = numberOfLikes;
  }

  public Integer getNumberOfDislikes() {
    return numberOfDislikes;
  }

  public void setNumberOfDislikes(Integer numberOfDislikes) {
    this.numberOfDislikes = numberOfDislikes;
  }

  /*
  public Integer getNumberOfLikes() {
    Integer numberOfLikes = 0;
    for (VoteResource current : voteResources) {
      if (current.getLike()) {
        numberOfLikes++;
      }
    }
    
    return numberOfLikes;
  }
  
  public Integer getNumberOfDislikes() {
    Integer numberOfDislikes = 0;
    for (VoteResource current : voteResources) {
      if (!current.getLike()) {
        numberOfDislikes++;
      }
    }
    
    return numberOfDislikes;
  }
*/
}
