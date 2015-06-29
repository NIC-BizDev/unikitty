package com.nicusa.assembler;

import com.nicusa.controller.DrugController;
import com.nicusa.controller.PortfolioController;
import com.nicusa.domain.Drug;
import com.nicusa.domain.Portfolio;
import com.nicusa.resource.PortfolioResource;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class PortfolioAssembler extends ResourceAssemblerSupport<Portfolio, PortfolioResource> {

  public PortfolioAssembler() {
    super(PortfolioController.class, PortfolioResource.class);
  }


  @Override
  public PortfolioResource toResource(Portfolio portfolio) {
    PortfolioResource portfolioResource;
    if (portfolio.getId() != null) {
      portfolioResource = createResourceWithId(portfolio.getId(), portfolio);
    } else {
      portfolioResource = new PortfolioResource();
    }
    portfolioResource.setName(portfolio.getName());
    portfolioResource.set_id(portfolio.getId().toString());
    for (Drug drug : portfolio.getDrugs()) {
      portfolioResource.add(linkTo(methodOn(DrugController.class).get(drug.getId())).withRel("drugs"));
    }
    return portfolioResource;
  }
}
