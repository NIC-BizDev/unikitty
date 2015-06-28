package com.nicusa.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nicusa.util.ApiKey;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.web.client.RestTemplate;

public class EventControllerTest {
    

  @Test
  public void testEventTerms () throws IOException {
    EventController event = new EventController();
    event.rest = mock( RestTemplate.class );
    event.apiKey = new ApiKey();
    final String res = "{\"meta\":{\"disclaimer\":\"openFDA is a beta research project and not for clinical use. While we make every effort to ensure that data is accurate, you should assume all results are unvalidated.\",\"license\":\"http://open.fda.gov/license\",\"last_updated\": \"2015-01-21\"},\"results\":[{\"term\":\"PAIN\",\"count\": 584  },{\"term\": \"DIARRHOEA\",\"count\":543},{\"term\": \"NAUSEA\",\"count\": 481},{\"term\":\"ANXIETY\",\"count\": 417}]}";
    when( event.rest.getForObject(anyString(),(Class<?>)any(Class.class))).thenAnswer(
        new Answer<String>() {
          @Override
          public String answer(InvocationOnMock inv) throws Throwable {
            return res;
          }
        });

    Map<String,Long> terms = event.getEventTerms( "blah" );
    assertTrue( terms.containsKey( "PAIN" ));
    assertTrue( terms.containsKey( "DIARRHOEA" ));
    assertTrue( terms.containsKey( "ANXIETY" ));
    assertEquals( 481L, terms.get( "NAUSEA" ).longValue());
  }

  @Test
  public void testSearch () throws IOException {
    EventController event = new EventController();
    event.rest = mock( RestTemplate.class );
    event.apiKey = new ApiKey();
    final String res = "{\"meta\":{\"disclaimer\":\"openFDA is a beta research project and not for clinical use. While we make every effort to ensure that data is accurate, you should assume all results are unvalidated.\",\"license\":\"http://open.fda.gov/license\",\"last_updated\": \"2015-01-21\"},\"results\":[{\"term\":\"PAIN\",\"count\": 584  },{\"term\": \"DIARRHOEA\",\"count\":543},{\"term\": \"NAUSEA\",\"count\": 481},{\"term\":\"ANXIETY\",\"count\": 417}]}";
    when( event.rest.getForObject(anyString(),(Class<?>)any(Class.class))).thenAnswer(
        new Answer<String>() {
          @Override
          public String answer(InvocationOnMock inv) throws Throwable {
            return res;
          }
        });

    String results = event.search( "blah", 2, 1 );
    assertTrue( !results.contains( "PAIN" ));
    assertTrue( results.contains( "DIARRHOEA" ));
    assertTrue( results.contains( "481" ));
    assertTrue( !results.contains( "ANXIETY" ));
  }
  
  
}
