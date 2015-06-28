package com.nicusa;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = {"com.nicusa"})
@Import(PersistenceConfiguration.class)
public class TestConfig
{

    @Bean
    public String fdaRecallsRSSurl()
    {
        return "http://www.fda.gov/AboutFDA/ContactFDA/StayInformed/RSSFeeds/Recalls/rss.xml"; 
    }
    
    @Bean
    public String xml2JsonCnvrtrUrl()
    {
        return "http://ajax.googleapis.com/ajax/services/feed/load?v=1.0&num=10&q=";
    }
    
    @Bean
    public String fdaDrugEventRSSurl()
    {
        return "https://api.fda.gov/drug/event.json";
    }

}
