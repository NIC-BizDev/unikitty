package com.nicusa.controller;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.nicusa.assembler.VoteAssembler;
import com.nicusa.converter.VoteResourceToDomainConverter;
import com.nicusa.domain.Vote;
import com.nicusa.resource.VoteResource;

@RunWith(MockitoJUnitRunner.class)
public class VoteControllerTest {
  @Mock
  private EntityManager entityManager;

  @Mock
  private VoteAssembler voteAssembler;

  @Mock
  public VoteResourceToDomainConverter voteResourceToDomainConverter;

  @InjectMocks
  private VoteController voteController;

  @Test
  public void testGetVote() throws Exception {
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    Vote vote = new Vote();
    when(entityManager.find(Vote.class, 1L)).thenReturn(vote);

    VoteResource voteResource = new VoteResource();
    when(voteAssembler.toResource(vote)).thenReturn(voteResource);
    ResponseEntity<VoteResource> voteResourceResponseEntity = voteController.getVote(1L);
    assertThat(HttpStatus.OK, is(voteResourceResponseEntity.getStatusCode()));
    assertThat(voteResource, is(voteResourceResponseEntity.getBody()));
  }

  @Test
  public void testGetVoteNotFound() {
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    when(entityManager.find(Vote.class, 1L)).thenReturn(null);
    ResponseEntity<VoteResource> voteResourceResponseEntity = voteController.getVote(1L);
    assertThat(HttpStatus.NOT_FOUND, is(voteResourceResponseEntity.getStatusCode()));
    assertThat(voteResourceResponseEntity.getBody(), is(nullValue()));
  }
}
