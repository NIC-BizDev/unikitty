package com.nicusa.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;


@RestController
public class FeedController {

    private static final Logger log = LoggerFactory.getLogger(FeedController.class);

    @Autowired
    @Value("${rss.fda.recalls.url:http://www.fda.gov/AboutFDA/ContactFDA/StayInformed/RSSFeeds/Recalls/rss.xml}")
    private String fdaRecallsRSSurl;

    @Autowired
    @Value("${gl.api.xml2json.url:http://ajax.googleapis.com/ajax/services/feed/load?v=1.0&num=10&q=}")
    private String xml2JsonCnvrtrUrl;

    @RequestMapping("/feed")
    @ResponseBody
    public String feed() {
        return "<h1>FDA Feeds</h1>";
    }

    @RequestMapping("/recalls")
    @ResponseBody
    public JsonNode getFDARecalls() {
        RestTemplate rest = new RestTemplate();
        JsonNode node = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            log.info(xml2JsonCnvrtrUrl+fdaRecallsRSSurl);
            node = mapper.readTree(rest.getForObject(xml2JsonCnvrtrUrl + fdaRecallsRSSurl, String.class));
        } catch (IOException e) {
            log.error("Error receiving FDA recalls", e);
            log.error(e.getStackTrace().toString());
        }

        return node;
    }

}
