package com.nicusa.domain;

import javax.persistence.*;

@Entity
public class Drug {

  public static final String SEQUENCE_NAME = "DRUG_SEQUENCE";

  private Long id;
  private String name;
  private String unii;
  private Portfolio portfolio;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @SequenceGenerator(name = SEQUENCE_NAME, allocationSize = 1)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public String getUnii() {
    return unii;
  }

  public void setUnii(String unii) {
    this.unii = unii;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Portfolio getPortfolio() {
    return portfolio;
  }

  public void setPortfolio(Portfolio portfolio) {
    this.portfolio = portfolio;
  }

  @Override
  public String toString() {
    return "DRUG{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", unii='" + unii + '\'' +
            '}';
  }
}
