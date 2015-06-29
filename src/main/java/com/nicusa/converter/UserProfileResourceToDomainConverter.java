package com.nicusa.converter;

import com.nicusa.controller.UserProfileController;
import com.nicusa.domain.NotificationSetting;
import com.nicusa.domain.NotificationSubject;
import com.nicusa.domain.NotificationType;
import com.nicusa.domain.UserProfile;
import com.nicusa.resource.NotificationSettingResource;
import com.nicusa.resource.PortfolioResource;
import com.nicusa.resource.UserProfileResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;

@Component
public class UserProfileResourceToDomainConverter extends ResourceToDomainConverter<UserProfileResource, UserProfile> {

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private PortfolioResourceToDomainConverter portfolioResourceToDomainConverter;

  @Override
  public UserProfile convert(UserProfileResource userProfileResource) {
    UserProfile userProfile = entityManager.find(UserProfile.class, extractIdFromLink(UserProfileController.class,
      userProfileResource, "getUserProfile", Long.class));
    if (userProfile == null) {
      userProfile = new UserProfile();
    }
    userProfile.setName(userProfileResource.getName());

    PortfolioResource portfolioResource = new PortfolioResource();
    portfolioResource.add(userProfileResource.getLink("portfolio").withRel("self"));
    userProfile.setPortfolio(portfolioResourceToDomainConverter.convert(portfolioResource));

    userProfile.setEmailAddress(userProfileResource.getEmailAddress());
    userProfile.setUserId(userProfileResource.getUserId());

    Collection<NotificationSetting> notificationSettings = new ArrayList<>();
    if (userProfileResource.getNotificationSettingResources() != null) {
      for (NotificationSettingResource notificationSettingResource :
        userProfileResource.getNotificationSettingResources()) {
        NotificationSetting notificationSetting = new NotificationSetting();
        notificationSetting.setNotificationSubject(NotificationSubject.valueOf(notificationSettingResource
          .getNotificationSubjectResource().name()));
        notificationSetting.setNotificationType(NotificationType.valueOf(notificationSettingResource
          .getNotificationTypeResource().name()));
        notificationSettings.add(notificationSetting);
      }
    }
    userProfile.setNotificationSettings(notificationSettings);
    return userProfile;

  }
}
