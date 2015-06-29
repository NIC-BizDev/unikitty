package com.nicusa.controller;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nicusa.assembler.VoteAssembler;
import com.nicusa.converter.VoteResourceToDomainConverter;
import com.nicusa.domain.Vote;
import com.nicusa.resource.FeatureResource;
import com.nicusa.resource.VoteResource;

public class VoteController {
  @PersistenceContext
  private EntityManager entityManager;
  
  @Autowired 
  private VoteAssembler voteAssembler;
  
  @Autowired
  private VoteResourceToDomainConverter voteResourceToDomainConverter;  
  
  @ResponseBody
  @RequestMapping(value = "/vote/{id}", method = RequestMethod.GET, produces = "application/hal+json")
  public ResponseEntity<VoteResource> getVote(@PathVariable("id") Long id) {
    Vote vote = entityManager.find(Vote.class, id);
    if (vote == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(voteAssembler.toResource(vote), HttpStatus.OK);
  }
  
  @ResponseBody
  @RequestMapping(value = "/vote-like", method = RequestMethod.POST, produces = "application/hal+json")  
  public ResponseEntity<VoteResource> voteLike(FeatureResource featureResource) {
    VoteResource voteResource = new VoteResource();
    voteResource.setLike(Boolean.TRUE);
    entityManager.persist(voteResourceToDomainConverter.convert(voteResource));
    return new ResponseEntity<>(voteResource, HttpStatus.OK);
  }
  
  @ResponseBody
  @RequestMapping(value = "/vote-dislike", method = RequestMethod.POST, produces = "application/hal+json")  
  public ResponseEntity<VoteResource> voteDislike(FeatureResource featureResource) {
    VoteResource voteResource = new VoteResource();
    voteResource.setLike(Boolean.FALSE);
    entityManager.persist(voteResourceToDomainConverter.convert(voteResource));
    return new ResponseEntity<>(voteResource, HttpStatus.OK);  
  }  
  
}
