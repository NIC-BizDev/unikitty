package com.nicusa.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.nicusa.TestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by aveerapaneni on 6/25/2015.
 */

@ContextConfiguration(classes = TestConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("local")
public class DrugControllerIT extends MockMvcTestBase {

    @Test
    public void testAutoComplete() throws Exception
    {
        MvcResult result = mockMvc.perform(get("/autocomplete?name=Adv"))
                .andExpect(status().isOk()).andReturn();
        JsonNode node =  objectMapper.readTree(result.getResponse().getContentAsString());
        assertNotNull(node);
        assertTrue(node.isArray());
        assertTrue(node.size() > 8 );
        assertTrue(node.get(0).asText().trim().equalsIgnoreCase("ADVAIR"));
        assertTrue(node.get(7).asText().trim().equalsIgnoreCase("ADVICOR"));
        assertTrue(node.get(8).asText().trim().equalsIgnoreCase("ADVIL"));
    }
}
