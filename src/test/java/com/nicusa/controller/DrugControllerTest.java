package com.nicusa.controller;

import com.nicusa.converter.DrugResourceToDomainConverter;
import com.nicusa.resource.UserProfileResource;
import com.nicusa.util.ApiKey;
import com.nicusa.util.HttpRestClient;
import com.nicusa.util.HttpSlurper;
import com.nicusa.assembler.DrugAssembler;
import com.nicusa.domain.Drug;
import com.nicusa.resource.DrugResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.net.URI;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;

import javax.persistence.EntityManager;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DrugControllerTest {

  @Mock
  private EntityManager entityManager;

  @Mock
  private DrugAssembler drugAssembler;

  @InjectMocks
  private DrugController drugController;

  @Mock
  private SecurityController securityController;

  @Mock
  private DrugResourceToDomainConverter drugResourceToDomainConverter;

  @Before
  public void before() {
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
  }

  @After
  public void after() {
    RequestContextHolder.setRequestAttributes(null);
  }

  @Test
  public void testCreateDrugAsAnonymousUser() {
    DrugResource drugResource = new DrugResource();
    when(securityController.getAuthenticatedUserProfileId()).thenReturn(UserProfileResource.ANONYMOUS_USER_PROFILE_ID);
    ResponseEntity<?> responseEntity = drugController.create(drugResource);
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
  }

  @Test
  public void testCreateDrugAsALoggedInUser() {
    Drug drug = new Drug();
    drug.setId(1L);
    DrugResource drugResource = new DrugResource();
    when(securityController.getAuthenticatedUserProfileId()).thenReturn(1L);
    when(drugResourceToDomainConverter.convert(drugResource)).thenReturn(drug);
    ResponseEntity<?> responseEntity = drugController.create(drugResource);
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
  }



  @Test
  public void testGetDrugFound() {
    Drug persistedDrug = new Drug();
    persistedDrug.setId(1L);
    DrugResource drugResource = new DrugResource();
    when(entityManager.find(Drug.class, 1L)).thenReturn(persistedDrug);
    when(drugAssembler.toResource(persistedDrug)).thenReturn(drugResource);
    ResponseEntity<DrugResource> drugResourceResponseEntity = drugController.get(1L);
    assertThat(HttpStatus.OK, is(drugResourceResponseEntity.getStatusCode()));
    assertThat(drugResourceResponseEntity.getBody(), is(drugResource));
  }


  @Test
  public void testGetDrugNotFound() {
    when(entityManager.find(Drug.class, 1L)).thenReturn(null);
    ResponseEntity<DrugResource> drugResourceResponseEntity = drugController.get(1L);
    assertThat(HttpStatus.NOT_FOUND, is(drugResourceResponseEntity.getStatusCode()));
    assertThat(drugResourceResponseEntity.getBody(), is(nullValue()));
  }


  @Test
  public void testShortAutocomplete() throws IOException {
    DrugController drug = new DrugController();
    drug.rest = mock(HttpRestClient.class);
    String res = drug.autocomplete("a");
    verify(drug.rest, never()).getForObject(anyString(), (Class<?>) any(Class.class));
  }

  @Test
  public void testRegularAutocomplete() throws IOException {
    DrugController drug = new DrugController();
    final String response = "[{\"value\":\"the result\"}]";
    drug.rest = mock(HttpRestClient.class);
    when(drug.rest.getForObject(any(String.class), any(Class.class))).thenReturn(response);
    assertThat(drug.autocomplete("blah"), is("[\"the result\"]"));
  }

  @Test
  public void testGetUniisByName () throws IOException {
    DrugController drug = new DrugController();
    drug.fdaDrugLabelUrl = "http://localhost:8080";
    final String response = "[{\"term\":\"abcdefg\"}]";
    drug.rest = mock(HttpRestClient.class);
    drug.apiKey = new ApiKey();
    when( drug.rest.getForObject(any(URI.class),any(Class.class))).thenReturn(response);
    Set<String> uniis = drug.getUniisByName( "blah" );
    assertEquals( uniis.size(), 1 );
    assertTrue( uniis.contains( "ABCDEFG" ));
  }

  @Test
  public void testGetBrandNamesByNameAndUniis () throws IOException {
    DrugController drug = new DrugController();
    drug.fdaDrugLabelUrl = "http://localhost:8080";
    final String response = "{\"meta\":{\"disclaimer\":\"openFDA is a beta research project and not for clinical use. While we make every effort to ensure that data is accurate, you should assume all results are unvalidated.\",\"license\":\"http://open.fda.gov/license\",\"last_updated\":\"2015-05-31\"},\"results\":[{\"term\":\"ADVIL PM\",\"count\":2}]}";
    drug.rest = mock(HttpRestClient.class);
    drug.apiKey = new ApiKey();
    when( drug.rest.getForObject(any(URI.class),any(Class.class))).thenReturn(response);
    Set<String> uniis = new HashSet<String>();
    uniis.add("8GTS82S83M");
    uniis.add("WK2XYI10QM");
    Map<String,Set<String>> result = drug.getBrandNamesByNameAndUniis( "blah", uniis );
    assertNotNull( result );
    assertTrue( result.get( "8GTS82S83M" ).contains("ADVIL PM") );
  }

  @Test
  public void testGetBrandNamesByNameAndUnii () throws IOException {
    DrugController drug = new DrugController();
    drug.fdaDrugLabelUrl = "http://localhost:8080";
    final String response = "{\"meta\":{\"disclaimer\":\"openFDA is a beta research project and not for clinical use. While we make every effort to ensure that data is accurate, you should assume all results are unvalidated.\",\"license\":\"http://open.fda.gov/license\",\"last_updated\":\"2015-05-31\"},\"results\":[{\"term\":\"ADVIL PM\",\"count\":2}]}";
    drug.rest = mock(HttpRestClient.class);
    drug.apiKey = new ApiKey();
    when( drug.rest.getForObject(any(URI.class),any(Class.class))).thenReturn( response );
    Set<String> result = drug.getBrandNamesByNameAndUnii( "blah", "8GTS82S83M" );
    assertNotNull( result );
    assertTrue( result.contains("ADVIL PM") );
  }

  @Test
  public void testGetRxcuiByUnii () throws IOException {
    DrugController drug = new DrugController();
    final String response = "{\"idGroup\":{\"name\":\"advil\",\"rxnormId\":[\"153010\"]}}";
    drug.rest = mock(HttpRestClient.class);
    drug.nlmRxnavUrl = "http://localhost:8080";
    when( drug.rest.getForObject(any(URI.class),any(Class.class))).thenReturn( response );
    Long resultRxcuis = drug.getRxcuiByUnii( "blah" );
    assertNotNull( resultRxcuis );
    assertEquals( new Long( 153010L ), resultRxcuis );
  }

  @Test
  public void testGetGenericNameByRxcui () throws IOException {
    DrugController drug = new DrugController();
    final String response = "{\"propConceptGroup\":{\"propConcept\":[{\"propCategory\":\"NAMES\",\"propName\":\"RxNorm Name\",\"propValue\":\"Ibuprofen\"}]}}";
    drug.rest = mock(HttpRestClient.class);
    drug.nlmRxnavUrl = "http://localhost:8080";
    when( drug.rest.getForObject(any(URI.class),any(Class.class))).thenReturn( response );
    String name = drug.getGenericNameByRxcui( 5640L );
    assertEquals( "IBUPROFEN", name );
  }

  @Test
  public void testSimpleSearch() throws IOException {
    DrugController drug = new DrugController();
    drug.fdaDrugLabelUrl = "http://localhost:8080";
    final String restResponse1 = "[{\"term\":\"abcdefg\"}]";
    final String restResponse2 = "{\"meta\":{\"disclaimer\":\"openFDA is a beta research project and not for clinical use. While we make every effort to ensure that data is accurate, you should assume all results are unvalidated.\",\"license\":\"http://open.fda.gov/license\",\"last_updated\":\"2015-05-31\"},\"results\":[{\"term\":\"DIPHENHYDRAMINE HYDROCHLORIDE\",\"count\":245}]}";
    final String slurpResponse1 = "{\"meta\":{\"disclaimer\":\"openFDA is a beta research project and not for clinical use. While we make every effort to ensure that data is accurate, you should assume all results are unvalidated.\",\"license\":\"http://open.fda.gov/license\",\"last_updated\":\"2015-05-31\"},\"results\":[{\"term\":\"ADVIL PM\",\"count\":2}]}";
    final String slurpResponse2 = "{\"idGroup\":{\"name\":\"advil\",\"rxnormId\":[\"153010\"]}}";
    final String slurpResponse3 = "{\"propConceptGroup\":{\"propConcept\":[{\"propCategory\":\"NAMES\",\"propName\":\"RxNorm Name\",\"propValue\":\"Ibuprofen\"}]}}";
    drug.slurp = mock( HttpSlurper.class );
    drug.rest = mock(HttpRestClient.class);
    drug.apiKey = new ApiKey();

    when( drug.rest.getForObject(any(URI.class),any(Class.class))).thenReturn(slurpResponse1,slurpResponse2,slurpResponse3 );
    when( drug.rest.getForObject(any(String.class),any(Class.class))).thenReturn(restResponse1,restResponse2);
    String result = drug.search( "blah", 10, 0 );
    assertNotNull( result );
    assertTrue( result.length() > 1 );
  }
}
