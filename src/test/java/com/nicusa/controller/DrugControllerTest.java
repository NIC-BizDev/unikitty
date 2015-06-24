package com.nicusa.controller;

import com.nicusa.assembler.DrugAssembler;
import com.nicusa.domain.Drug;
import com.nicusa.resource.DrugResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import javax.persistence.EntityManager;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DrugControllerTest {

  @Mock
  private EntityManager entityManager;

  @Mock
  private DrugAssembler drugAssembler;

  @InjectMocks
  private DrugController drugController;

  @Test
  public void testGetDrugFound() {
    Drug persistedDrug = new Drug();
    persistedDrug.setId(1L);
    DrugResource drugResource = new DrugResource();
    when(entityManager.find(Drug.class, 1L)).thenReturn(persistedDrug);
    when(drugAssembler.toResource(persistedDrug)).thenReturn(drugResource);
    ResponseEntity<DrugResource> drugResourceResponseEntity = drugController.getDrug(1L);
    assertThat(HttpStatus.OK, is(drugResourceResponseEntity.getStatusCode()));
    assertThat(drugResourceResponseEntity.getBody(), is(drugResource));
  }
  

  @Test
  public void testGetDrugNotFound() {
    when(entityManager.find(Drug.class, 1L)).thenReturn(null);
    ResponseEntity<DrugResource> drugResourceResponseEntity = drugController.getDrug(1L);
    assertThat(HttpStatus.NOT_FOUND, is(drugResourceResponseEntity.getStatusCode()));
    assertThat(drugResourceResponseEntity.getBody(), is(nullValue()));
  }


  @Test
  public void testShortAutocomplete() throws IOException {
    DrugController drug = new DrugController();
    drug.rest = mock(RestTemplate.class);
    String res = drug.autocomplete("ab");
    verify(drug.rest, never()).getForObject(anyString(), (Class<?>) any(Class.class));
  }

  @Test
  public void testRegularAutocomplete() throws IOException {
    DrugController drug = new DrugController();
    final String res = "[{\"value\":\"the result\"}]";
    drug.rest = mock(RestTemplate.class);
    when(drug.rest.getForObject(anyString(), (Class<?>) any(Class.class))).thenAnswer(
      new Answer<String>() {
        @Override
        public String answer(InvocationOnMock inv) throws Throwable {
          return res;
        }
      });
    assertThat(drug.autocomplete("blah"), is("[\"the result\"]"));
  }

  @Test
  public void testSimpleSearch() throws IOException {
    DrugController drug = new DrugController();
    final String res = "[{\"value\":\"the result\"}]";
    drug.rest = mock( RestTemplate.class );
    when( drug.rest.getForObject(anyString(),(Class<?>)any(Class.class))).thenAnswer(
       new Answer<String>() {
         @Override
         public String answer(InvocationOnMock inv) throws Throwable {
           return res;
         }
       });
    assertThat( drug.search( "blah", 10, 0 ), is( res ));
  }
}
