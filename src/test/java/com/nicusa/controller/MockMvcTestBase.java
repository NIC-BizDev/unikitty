package com.nicusa.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class MockMvcTestBase
{
    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    protected ObjectMapper objectMapper;

    @Before
    public void setup ()
    {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
    }

    protected <T> T mapObject (MvcResult result, TypeReference<T> typeReference) throws JsonParseException, JsonMappingException,
            UnsupportedEncodingException, IOException
    {
        return objectMapper.readValue(result.getResponse().getContentAsString(), typeReference);
    }

    protected <T> T mapObject (MvcResult result, Class<T> clazz) throws JsonParseException, JsonMappingException,
            UnsupportedEncodingException, IOException
    {
        return objectMapper.readValue(result.getResponse().getContentAsString(), clazz);
    }
}
