package com.nicusa.domain;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class UserProfile {

  public static final String SEQUENCE_NAME = "USER_PROFILE_SEQUENCE";

  private Long id;
  private String userId;
  private String name;
  private String emailAddress;
  private Portfolio portfolio;
  private Collection<NotificationSetting> notificationSettings;
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

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public Portfolio getPortfolio() {
    return portfolio;
  }

  public void setPortfolio(Portfolio portfolio) {
    this.portfolio = portfolio;
  }

  public Collection<NotificationSetting> getNotificationSettings() {
    return notificationSettings;
  }

  public void setNotificationSettings(Collection<NotificationSetting> notificationSettings) {
    this.notificationSettings = notificationSettings;
  }

  public Collection<Vote> getVotes() {
    return votes;
  }

  public void setVotes(Collection<Vote> votes) {
    this.votes = votes;
  }
}
