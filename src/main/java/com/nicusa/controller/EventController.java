package com.nicusa.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nicusa.service.AdverseEffectService;
import com.nicusa.util.AdverseEffect;
import com.nicusa.util.ApiKey;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class EventController {
    private static final Logger log = LoggerFactory.getLogger(EventController.class);

  @Autowired
  ApiKey apiKey;
  
  @Autowired
  @Value("${fda.drug.event.url:https://api.fda.gov/drug/event.json}")
  private String fdaDrugEventUrl;

  @Autowired
  AdverseEffectService adverseEffectService;

  RestTemplate rest = new RestTemplate();

  public Map<String,Long> getEventTerms ( String unii ) throws IOException {
      apiKey = new ApiKey();
    String query = String.format(
        this.fdaDrugEventUrl + "?search=patient.drug.openfda.unii:%s&count=patient.reaction.reactionmeddrapt.exact",
        URLEncoder.encode( unii, StandardCharsets.UTF_8.name() )) +
      this.apiKey.getFdaApiKeyQuery();

    ObjectMapper mapper = new ObjectMapper();
    JsonNode node = mapper.readTree(
        this.rest.getForObject( query, String.class ));
    JsonNode results = node.get( "results" );
    Map<String,Long> rv = new TreeMap<String,Long>();
    Iterator<JsonNode> iter = results.iterator();
    while ( iter.hasNext() ) {
      JsonNode n = iter.next();
      Iterator<JsonNode> internal = n.iterator();
      rv.put( internal.next().textValue(), internal.next().longValue() );
    }
    return rv;
  }


  
  @RequestMapping("/event")
  public String search(
      @RequestParam(value="unii", defaultValue="" ) String unii,
      @RequestParam(value="limit", defaultValue="10" ) int limit,
      @RequestParam(value="skip", defaultValue="0" ) int skip ) throws IOException {
    if ( unii == null ) {
      unii = "";
    }

    Map<String,Long> terms = this.getEventTerms( unii );

    ObjectMapper mapper = new ObjectMapper();

    long max = 0L;
    for ( String k : terms.keySet() ) {
      if ( terms.get( k ) > max ) {
        max = terms.get( k );
      }
    }

    Set<AdverseEffect> effects = new TreeSet<AdverseEffect>();
    for ( String k : terms.keySet() ) {
      AdverseEffect ef = new AdverseEffect();
      ef.setEffect( k );
      ef.setCount( terms.get( k ));
      ef.setTotal( max );
      ef.setDescription( adverseEffectService.findEffectDescription(k));
      // TODO set description
      effects.add( ef );
    }
    ObjectNode top = mapper.createObjectNode();
    top.put( "UNII", unii );
    ArrayNode array = mapper.createArrayNode();
    int count = 0;
    for ( AdverseEffect ef : effects ) {
      if ( count >= skip ) {
        if ( count == limit + skip ) {
          break;
        }
        array.add( mapper.valueToTree( ef ));
      }
      count++;
    }
    top.put( "effect", array );

    return mapper.writeValueAsString( top );
  }

}
