package com.nicusa.controller;

import com.nicusa.assembler.PortfolioAssembler;
import com.nicusa.converter.PortfolioResourceToDomainConverter;
import com.nicusa.domain.Portfolio;
import com.nicusa.domain.UserProfile;
import com.nicusa.resource.DrugResource;
import com.nicusa.resource.PortfolioResource;
import com.nicusa.resource.UserProfileResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


@RestController
public class PortfolioController {

  @PersistenceContext
  public EntityManager entityManager;

  @Autowired
  public PortfolioAssembler portfolioAssembler;

  @Autowired
  public PortfolioResourceToDomainConverter portfolioResourceToDomainConverter;

  @Autowired
  public SecurityController securityController;


  @Transactional
  @ResponseBody
  @RequestMapping(value = "/portfolio", method = RequestMethod.POST, consumes = "application/json")
  public ResponseEntity<?> create(@RequestBody PortfolioResource portfolioResource) {
    Long loggedInUserProfileId = securityController.getAuthenticatedUserProfileId();
    if(loggedInUserProfileId != null && loggedInUserProfileId != UserProfileResource.ANONYMOUS_USER_PROFILE_ID) {
      Portfolio portfolio = portfolioResourceToDomainConverter.convert(portfolioResource);
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setLocation(linkTo(methodOn(PortfolioController.class).getPortfolio(portfolio.getId())).toUri());
      return new ResponseEntity<PortfolioResource>(httpHeaders, HttpStatus.CREATED);
    } else {
      return new ResponseEntity<PortfolioResource>(HttpStatus.UNAUTHORIZED);
    }
  }

  @ResponseBody
  @RequestMapping(value = "/portfolio/{id}", method = RequestMethod.GET, produces = "application/json")
  public ResponseEntity<PortfolioResource> getPortfolio(@PathVariable("id") Long id) {
    Portfolio portfolio = entityManager.find(Portfolio.class, id);
    if (portfolio == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(portfolioAssembler.toResource(portfolio), HttpStatus.OK);
  }

  @Transactional
  @ResponseBody
  @RequestMapping(value = "/portfolio/{id}", method = RequestMethod.PUT, consumes = "application/json")
  public ResponseEntity<?> updatePortfolio(@PathVariable("id") Long id,
                                                           @RequestBody PortfolioResource portfolioResource) {
    Long loggedInUserProfileId = securityController.getAuthenticatedUserProfileId();
    if(loggedInUserProfileId != null && loggedInUserProfileId != UserProfileResource.ANONYMOUS_USER_PROFILE_ID) {
      Portfolio portfolio = entityManager.find(Portfolio.class, id);
      if (portfolio == null) {
        return new ResponseEntity<PortfolioResource>(HttpStatus.NO_CONTENT);
      } else {
        if(portfolio.getUserProfile() != null && portfolio.getUserProfile().getId() == loggedInUserProfileId) {
          Portfolio updatedPortfolio = portfolioResourceToDomainConverter.convert(portfolioResource);
          portfolio.setName(updatedPortfolio.getName());
          portfolio.setUserProfile(updatedPortfolio.getUserProfile());
          portfolio.setDrugs(updatedPortfolio.getDrugs());
          portfolio = entityManager.merge(portfolio);
          PortfolioResource updatedPortfolioResource = portfolioAssembler.toResource(portfolio);
          return new ResponseEntity<>(HttpStatus.OK);
        } else {
          return new ResponseEntity<PortfolioResource>(HttpStatus.UNAUTHORIZED);
        }
      }
    } else {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
  }

  @Transactional
  @ResponseBody
  @RequestMapping(value = "/portfolio/{id}", method = RequestMethod.DELETE, consumes = "application/json")
  public ResponseEntity<?> delete(@PathVariable Long id) {
    Long loggedInUserProfileId = securityController.getAuthenticatedUserProfileId();
    if(loggedInUserProfileId != null && loggedInUserProfileId != UserProfileResource.ANONYMOUS_USER_PROFILE_ID) {
      Portfolio portfolio = entityManager.find(Portfolio.class, id);
      if (portfolio == null) {
        return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
      } else {
        if (portfolio.getUserProfile() != null && portfolio.getUserProfile().getId() == loggedInUserProfileId) {
          entityManager.remove(portfolio);
          return new ResponseEntity<Object>(HttpStatus.OK);
        } else {
          return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
        }
      }
    } else {
      return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
    }
  }

}
