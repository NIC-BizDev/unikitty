package com.nicusa;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = UiApplication.class)
@WebAppConfiguration
@ActiveProfiles("local")
@IntegrationTest("server.port:0")
public class UiApplicationTest {

  @Test
  public void contextLoads() {
  }

  @Value("${local.server.port}")
  private int port;

  private RestTemplate template = new TestRestTemplate();

  @Test
  public void homePageLoads() {
    ResponseEntity<String> response = template.getForEntity("http://localhost:"
            + port + "/", String.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

}
