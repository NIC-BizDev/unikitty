package com.nicusa.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
public class Portfolio {

  public static final String SEQUENCE_NAME = "PROTFOLIO_SEQUENCE";

  private Long id;
  private String name = "My Meds";
  private UserProfile userProfile;
  private Collection<Drug> drugs = new ArrayList<>();

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @SequenceGenerator(name = SEQUENCE_NAME, allocationSize = 1)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Collection<Drug> getDrugs() {
    return drugs;
  }

  public void setDrugs(Collection<Drug> drugs) {
    this.drugs = drugs;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public UserProfile getUserProfile() {
    return userProfile;
  }

  public void setUserProfile(UserProfile userProfile) {
    this.userProfile = userProfile;
  }

  @Override
  public String toString() {
    return "PORTFOLIO{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", drugs=" + drugs +
            '}';
  }
}
