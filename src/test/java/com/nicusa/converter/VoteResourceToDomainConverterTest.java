package com.nicusa.converter;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.nicusa.controller.FeatureController;
import com.nicusa.controller.VoteController;
import com.nicusa.domain.Feature;
import com.nicusa.domain.UserProfile;
import com.nicusa.domain.Vote;
import com.nicusa.resource.FeatureResource;
import com.nicusa.resource.VoteResource;

@RunWith(MockitoJUnitRunner.class)
public class VoteResourceToDomainConverterTest {
  @Mock
  private EntityManager entityManager;

  @InjectMocks
  private VoteResourceToDomainConverter voteResourceToDomainConverter;
  
  @Mock
  private FeatureResourceToDomainConverter featureResourceToDomainConverter;

  @Test
  public void testConvert() {
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    
    Feature feature = createFeature();
    
    Vote persistedVote = new Vote();
    persistedVote.setFeature(feature);
    persistedVote.setUserProfile(createUserProfile(1L, "TEST USER"));
    persistedVote.setLike(Boolean.TRUE);

    when(entityManager.find(Vote.class, 1L)).thenReturn(persistedVote);
    when(featureResourceToDomainConverter.convert(any(FeatureResource.class))).thenReturn(feature);
    
    VoteResource voteResource = new VoteResource();
    voteResource.add(linkTo(methodOn(VoteController.class).getVote(1L)).withSelfRel());
    voteResource.add(linkTo(methodOn(FeatureController.class).getFeature(1L)).withRel("feature"));

    Vote vote = voteResourceToDomainConverter.convert(voteResource);
    assertThat(vote, is(persistedVote));
  }

  private Feature createFeature() {
    Feature feature = new Feature();
    feature.setId(1L);
    feature.setTitle("TEST TITLE");
    feature.setRequestText("TEST REQUEST TEXT");
    feature.setVotes(new ArrayList<Vote>());
    return feature;
  }
  
  private UserProfile createUserProfile(Long id, String name) {
    UserProfile persistedProfile = new UserProfile();
    persistedProfile.setId(id);
    persistedProfile.setName(name);
    return persistedProfile;
  }
}
