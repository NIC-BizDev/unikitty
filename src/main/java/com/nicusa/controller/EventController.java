package com.nicusa.controller;

import com.nicusa.util.FieldFinder;
import com.nicusa.util.HttpSlurper;
import com.nicusa.util.SpaceConverter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@RestController
public class EventController {
    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    @Autowired
    @Value("${fda.drug.event.url:https://api.fda.gov/drug/event.json}")
    private String fdaDrugEventUrl;

    RestTemplate rest = new RestTemplate();
    HttpSlurper slurp = new HttpSlurper();

    public Set<String> getEventTerms ( String unii ) throws IOException {
      String query = String.format(
          this.fdaDrugEventUrl + "?search=patient.drug.openfda.unii:%s&count=patient.reaction.reactionmeddrapt.exact",
          URLEncoder.encode( unii, StandardCharsets.UTF_8.name() ));
      String termJson = this.rest.getForObject( query, String.class );
      FieldFinder ff = new FieldFinder( "term" );
      return ff.find( termJson );
    }

    @RequestMapping("/event")
    public String search(
        @RequestParam(value="unii", defaultValue="" ) String unii,
        @RequestParam(value="limit", defaultValue="10" ) int limit,
        @RequestParam(value="skip", defaultValue="0" ) int skip ) throws IOException {
      if ( unii == null ) {
        unii = "";
      }

      Set<String> tSet = this.getEventTerms( unii );

      ObjectMapper mapper = new ObjectMapper();
      ArrayNode top = mapper.createArrayNode();
      SpaceConverter conv = new SpaceConverter();

      for ( String t : tSet ) {
        ArrayNode event = mapper.createArrayNode();
        event.add( t );
        String query =
          this.fdaDrugEventUrl +
          "?search=patient.drug.openfda.unii:" +
          URLEncoder.encode( unii, StandardCharsets.UTF_8.name() ) +
          "+AND+patient.reaction.reactionmeddrapt:" +
          conv.convert( t ) +
          "&count=serious";
        JsonNode tree = mapper.readTree( slurp.getData( query ));
        event.add( tree.get( "results" ));
        top.add( event );
      }

      return mapper.writeValueAsString( top );
    }

}
