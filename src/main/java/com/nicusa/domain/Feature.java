package com.nicusa.domain;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class Feature {

  public static final String SEQUENCE_NAME = "FEATURE_SEQUENCE";
  
  private Long id;
  private String title;
  private String requestText;
  private Collection<Vote> votes;
  
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @SequenceGenerator(name = SEQUENCE_NAME, allocationSize = 1)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  public Collection<Vote> getVotes() {
    return votes;
  }

  public void setVotes(Collection<Vote> votes) {
    this.votes = votes;
  }

}