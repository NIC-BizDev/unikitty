package com.nicusa.domain;

import javax.persistence.*;

@Entity
public class Vote {

  public static final String SEQUENCE_NAME = "VOTE_SEQUENCE";
  
  private Long id;
  private Boolean like;
  private UserProfile userProfile;
  private Feature feature;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @SequenceGenerator(name = SEQUENCE_NAME, allocationSize = 1)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Boolean getLike() {
    return like;
  }

  public void setLike(Boolean like) {
    this.like = like;
  }

  public UserProfile getUserProfile() {
    return userProfile;
  }

  public void setUserProfile(UserProfile userProfile) {
    this.userProfile = userProfile;
  }

  public Feature getFeature() {
    return feature;
  }

  public void setFeature(Feature feature) {
    this.feature = feature;
  }
  
}