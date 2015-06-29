package com.nicusa.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicusa.assembler.DrugAssembler;
import com.nicusa.converter.DrugResourceToDomainConverter;
import com.nicusa.domain.Drug;
import com.nicusa.resource.DrugResource;
import com.nicusa.resource.UserProfileResource;
import com.nicusa.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class DrugController {
  private static final Logger log = LoggerFactory.getLogger(DrugController.class);

  RestTemplate rest = new RestTemplate();
  HttpSlurper slurp = new HttpSlurper();

  @Autowired
  ApiKey apiKey;

  @Autowired
  @Value("${fda.drug.label.url:https://api.fda.gov/drug/label.json}")
  private String fdaDrugLabelUrl;
  @Autowired
  @Value("${nlm.dailymed.autocomplete.url:https://dailymed.nlm.nih.gov/dailymed/autocomplete.cfm}")
  private String nlmDailymedAutocompleteUrl;
  @Autowired
  @Value("${nlm.rxnav.url:http://rxnav.nlm.nih.gov/REST/rxcui}")
  private String nlmRxnavUrl;
  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private DrugAssembler drugAssembler;

  @Autowired
  private DrugResourceToDomainConverter drugResourceToDomainConverter;

  @Autowired
  private SecurityController securityController;

  @Transactional
  @ResponseBody
  @RequestMapping(value = "/drug", method = RequestMethod.POST, consumes = "application/json")
  public ResponseEntity<?> create(@RequestBody DrugResource drugResource) {
    Long loggedInUserProfileId = securityController.getAuthenticatedUserProfileId();
  if (loggedInUserProfileId != null && loggedInUserProfileId != UserProfileResource.ANONYMOUS_USER_PROFILE_ID) {
      Drug drug = drugResourceToDomainConverter.convert(drugResource);
      entityManager.persist(drug);
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setLocation(linkTo(methodOn(DrugController.class).get(drug.getId())).toUri());
      return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
    } else {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
  }

  @Transactional
  @ResponseBody
  @RequestMapping(value = "/drug/{id}", method = RequestMethod.DELETE, consumes = "application/json")
  public ResponseEntity<?> delete(@PathVariable("id") Long id) {
    Long loggedInUserProfileId = securityController.getAuthenticatedUserProfileId();
    if(loggedInUserProfileId != null && loggedInUserProfileId != UserProfileResource.ANONYMOUS_USER_PROFILE_ID) {
      Drug drug = entityManager.find(Drug.class, id);
      entityManager.remove(drug);
      return new ResponseEntity<>(HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
  }

  @ResponseBody
  @RequestMapping(value = "/drug/{id}", method = RequestMethod.GET, produces = "application/json")
  public ResponseEntity<DrugResource> get(@PathVariable("id") Long id) {
    Drug drug = entityManager.find(Drug.class, id);
    if (drug == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(drugAssembler.toResource(drug), HttpStatus.OK);
  }

  public Set<String> getUniisByName ( String name ) throws IOException {
    String query = this.fdaDrugLabelUrl +
      "?search=openfda.brand_name:" +
      URLEncoder.encode( name, StandardCharsets.UTF_8.name() ) +
      "&count=openfda.unii" + this.apiKey.getFdaApiKeyQuery();
    try {
      String result = rest.getForObject( query, String.class );
      FieldFinder finder = new FieldFinder( "term" );
      return finder.find( result );
    } catch ( HttpClientErrorException notFound ) {
      if( notFound.getStatusCode() == HttpStatus.NOT_FOUND ){
        // server reported 404, handle it by returning no results
        log.warn( "[getUniisByName] No uniis with name " + name);
        return Collections.emptySet();
      }
      throw notFound;
    }
  }

  public Map<String,Set<String>> getBrandNamesByNameAndUniis (
      String name,
      Set<String> uniis ) throws IOException {
    Map<String,Set<String>> rv = new TreeMap<String,Set<String>>();
    for ( String unii : uniis ) {
      rv.put( unii, this.getBrandNamesByNameAndUnii( name, unii ));
    }
    return rv;
  }

  public Set<String> getBrandNamesByNameAndUnii (
      String name,
      String unii ) throws IOException {
    String query = this.fdaDrugLabelUrl +
      "?search=(openfda.unii:" +
      URLEncoder.encode( unii, StandardCharsets.UTF_8.name() ) +
      ")+AND+(openfda.brand_name:" +
      URLEncoder.encode( name, StandardCharsets.UTF_8.name() ) +
      ")&count=openfda.brand_name.exact" +
      this.apiKey.getFdaApiKeyQuery();

    try {
      String result = slurp.getData( query );
      FieldFinder finder = new FieldFinder( "term" );
      return finder.find( result );
    } catch ( FileNotFoundException notFound ) {
      // server reported 404, handle it by returning no results
      log.warn( "No brand name data found for search by name: " +
          name + " and unii " + unii );
      return Collections.emptySet();
    }
  }

  public String getGenericNameByUnii ( String unii ) throws IOException {
    String query = this.fdaDrugLabelUrl +
      "?search=openfda.unii:" +
      URLEncoder.encode( unii, StandardCharsets.UTF_8.name() ) +
      "&count=openfda.generic_name.exact&limit=1" +
      this.apiKey.getFdaApiKeyQuery();
    String result = rest.getForObject( query, String.class );
    FieldFinder finder = new FieldFinder( "term" );
    Set<String> generics = finder.find( result );
    if ( generics.isEmpty() ) {
      return "";
    } else {
      return generics.iterator().next();
    }
  }

  public Long getRxcuiByBrandName ( String brandName ) throws IOException {
    String query = this.nlmRxnavUrl +
      ".json?name=" +
      URLEncoder.encode( brandName, StandardCharsets.UTF_8.name() );
    String result = slurp.getData( query );
    FieldFinder finder = new FieldFinder( "rxnormId" );
    Set<String> rxcuis = finder.find( result );
    for ( String rxcui : rxcuis ) {
      try {
        return Long.parseLong( rxcui );
      } catch ( NumberFormatException e ) {
        // ignore invalid rxcui, fallthrough to returning null if none work
        log.warn( "Got invalid RXCUI from " + this.nlmRxnavUrl +
            " with brandName " + brandName, e );
      }
    }
    return null;
  }

  public Set<String> getActiveIngredientsByRxcui ( Long rxcui ) throws IOException {
    if ( rxcui == null ) {
      return Collections.emptySet();
    }
    String query = this.nlmRxnavUrl +
      "/" +
      rxcui +
      "/related.json?rela=tradename_of+has_precise_ingredient";
    String result = slurp.getData( query );
    FieldFinder finder = new FieldFinder( "name" );
    return finder.find( result );
  }

  @RequestMapping("/drug")
  public String search(
    @RequestParam(value = "name", defaultValue = "") String name,
    @RequestParam(value = "limit", defaultValue = "10") int limit,
    @RequestParam(value = "skip", defaultValue = "0") int skip) throws IOException {
    if (name == null) {
      name = "";
    }

    name = name.replaceAll(",", "");

    List<DrugSearchResult> rv = new LinkedList<DrugSearchResult>();

    Set<String> uniis = this.getUniisByName( name );
    Map<String,Set<String>> brandNames = this.getBrandNamesByNameAndUniis(
        name,
        uniis );

    int count = 0;
    for ( String unii : uniis ) {
      if ( rv.size() >= limit ) {
        break;
      }
      for ( String brandName : brandNames.get( unii )) {
        if ( rv.size() >= limit ) {
          break;
        }
        if ( count > skip ) {
          DrugSearchResult res = new DrugSearchResult();
          res.setUnii( unii );
          res.setBrandName( brandName );
          res.setGenericName( this.getGenericNameByUnii( unii ));
          res.setRxcui( this.getRxcuiByBrandName( brandName ));
          res.setActiveIngredients( this.getActiveIngredientsByRxcui(
                res.getRxcui() ));
          // workaround for beta rxcui source
          if ( res.getActiveIngredients().isEmpty() &&
              res.getGenericName() != null ) {
            res.setActiveIngredients(
              new TreeSet<String>( Arrays.asList(
                res.getGenericName().split( ", " ))));
          }

          // if the brand name matches the search, display as first result
          if ( name.equalsIgnoreCase( brandName )) {
            rv.add( 0, res );
          } else {
            rv.add( res );
          }
        }
        count++;
      }
    }

    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString( rv );
  }

  @RequestMapping("/autocomplete")
  //TODO - Move this to the client side.
  public String autocomplete(
    @RequestParam(value = "name", defaultValue = "") String name) throws IOException {
    if (name == null) {
      name = "";
    }

    String result = "[{\"value\":\"" + name + "\"}]";
    if (name.length() >= 2) {
      ObjectMapper mapper = new ObjectMapper();
      String query = String.format(
          this.nlmDailymedAutocompleteUrl + "?key=search&returntype=json&term=%s",
          URLEncoder.encode(name, StandardCharsets.UTF_8.name()));
      JsonNode node = mapper.readTree(
          rest.getForObject(query, String.class));
      AutocompleteFilter myFilter = new AutocompleteFilter();
      List<String> values = myFilter.filter(node);
      result = mapper.writeValueAsString(values);

    }
    return result;
  }

}
