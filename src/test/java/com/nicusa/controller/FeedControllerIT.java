package com.nicusa.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.nicusa.TestConfig;
import com.nicusa.UiApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by aveerapaneni on 6/23/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@WebAppConfiguration
@ActiveProfiles("local")

public class FeedControllerIT extends MockMvcTestBase {

    @Test
    public void testGetFDARecalls() throws Exception
    {
        MvcResult result = mockMvc.perform(get("/recalls")).andExpect(status().isOk()).andReturn();
    }

    @Test
    public void testGetDrugRecalls() throws Exception
    {
        MvcResult result = mockMvc.perform(get("/drug/recalls")).andExpect(status().isOk()).andReturn();
    }

    @Test
    public void testGetDrugRecallsWithLimit() throws Exception
    {
        MvcResult result = mockMvc.perform(get("/drug/recalls?limit=10"))
                .andExpect(status().isOk()).andReturn();
        JsonNode node =  objectMapper.readTree(result.getResponse().getContentAsString());
        assertNotNull(node);
        assertTrue(node.get("results").isArray());
        assertTrue(10 == node.get("results").size());
    }

    @Test
    public void testGetDrugRecallsWithFromDtAndToDt() throws Exception
    {
        MvcResult result = mockMvc.perform(get("/drug/recalls?fromDt=20150101&toDt=20150301"))
                .andExpect(status().isOk()).andReturn();
        JsonNode node =  objectMapper.readTree(result.getResponse().getContentAsString());
        assertNotNull(node);
        assertTrue(node.get("results").isArray());
        assertTrue(5 == node.get("results").size());
    }

    @Test
    public void testGetDrugRecallsForUnii() throws Exception
    {
        //9 - 6M3C89ZY6R, 12-YI7VU623SF, 4 -T2410KM04A, 17- 0CD5FD6S2M
        MvcResult result = mockMvc.perform(get("/drug/enforcements?unii=6M3C89ZY6R"))
                .andExpect(status().isOk()).andReturn();
        JsonNode node =  objectMapper.readTree(result.getResponse().getContentAsString());
        assertNotNull(node);
        assertTrue(node.get("results").isArray());
        assertTrue(node.get("results").size() == node.get("meta").get("results").get("total").asInt());
    }

    @Test
    public void testGetDrugRecallsForUniiWithLimit() throws Exception
    {
        //9 - 6M3C89ZY6R, 12-YI7VU623SF, 4 -T2410KM04A, 17- 0CD5FD6S2M
        MvcResult result = mockMvc.perform(get("/drug/enforcements?unii=0CD5FD6S2M&limit=5"))
                .andExpect(status().isOk()).andReturn();
        JsonNode node =  objectMapper.readTree(result.getResponse().getContentAsString());
        assertNotNull(node);
        assertTrue(node.get("results").isArray());
        assertTrue(5 == node.get("results").size());
    }


    @Test
    public void testGetDrugRecallsForUniiNotGiven() throws Exception
    {
        MvcResult result = mockMvc.perform(get("/drug/enforcements"))
                .andExpect(status().isOk()).andReturn();
        JsonNode node =  objectMapper.readTree(result.getResponse().getContentAsString());
        assertNotNull(node);
        assertTrue(node.get("results").isArray());
        assertTrue(10 == node.get("results").size());
    }

    @Test
    public void testGetDrugRecallsForUniiNotGivenWithLimit() throws Exception
    {
        MvcResult result = mockMvc.perform(get("/drug/enforcements?limit=15"))
                .andExpect(status().isOk()).andReturn();
        JsonNode node =  objectMapper.readTree(result.getResponse().getContentAsString());
        assertNotNull(node);
        assertTrue(node.get("results").isArray());
        assertTrue(15 == node.get("results").size());
    }

    @Test
    public void testGetDrugRecallsForUniiNotFound() throws Exception
    {
        MvcResult result = mockMvc.perform(get("/drug/enforcements?unii=12345678"))
                .andExpect(status().isOk()).andReturn();
        JsonNode node =  objectMapper.readTree(result.getResponse().getContentAsString());
        assertNotNull(node);
        assertNotNull(node.get("error"));
        assertTrue(node.get("error").get("code").asText().equals("NOT_FOUND"));
    }

}
