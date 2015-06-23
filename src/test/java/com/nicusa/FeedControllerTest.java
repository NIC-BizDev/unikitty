package com.nicusa;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nicusa.controller.MockMvcTestBase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;


@ContextConfiguration(classes = TestConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class FeedControllerTest extends MockMvcTestBase
{

    
    @Test
    public void testGetFDARecalls() throws Exception
    {
        MvcResult result = mockMvc.perform(get("/recalls")).andExpect(status().isOk()).andReturn();
    }
    
    
    String retStr = 
    "{\"responseData\":"+
            "{\"feed\":"+
            "{\"feedUrl\":\"http://www.fda.gov/AboutFDA/ContactFDA/StayInformed/RSSFeeds/Recalls/rss.xml\","+
            "\"title\":\"Food and Drug Administration--Recalls/Safety Alerts\","+
            "\"link\":\"http://www.fda.gov/AboutFDA/ContactFDA/StayInformed/RSSFeeds/Recalls/rss.xml\","+
            "\"author\":\"\","+
            "\"description\":\"Recall Information from FDA\","+
            "\"type\":\"rss20\","+
            "\"entries\":"+
            "[{\"title\":\"Project 7 Issues Allergy Alert on Undeclared Milk/Dairy Ingredient In Sour Caramel Apple Gum\","+
            "\"link\":\"http://www.fda.gov/Safety/Recalls/ucm452203.htm\","+
            "\"author\":\"\","+
            "\"publishedDate\":"+
            "}]}}, \"responseDetails\": null, \"responseStatus\": 200}\"}";
    
    
}
