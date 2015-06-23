package com.nicusa;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.nicusa"})
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
}
