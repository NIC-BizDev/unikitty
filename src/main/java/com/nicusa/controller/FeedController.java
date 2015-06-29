package com.nicusa.controller;

import com.nicusa.util.ApiKey;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nicusa.util.NormalizeStateCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


@RestController
public class FeedController {

    private static final Logger log = LoggerFactory.getLogger(FeedController.class);

    @Autowired
    ApiKey apiKey;

    @Autowired
    @Value("${rss.fda.recalls.url:http://www.fda.gov/AboutFDA/ContactFDA/StayInformed/RSSFeeds/Recalls/rss.xml}")
    private String fdaRecallsRSSurl;

    @Autowired
    @Value("${gl.api.xml2json.url:http://ajax.googleapis.com/ajax/services/feed/load?v=1.0&num=10&q=}")
    private String xml2JsonCnvrtrUrl;

    @Autowired
    @Value("${api.fda.drug.enfrcmnt.url:https://api.fda.gov/drug/enforcement.json?}")
    private String searchDrugEnfrcmntUrl;

    @Autowired
    @Value("${drug.most.recent.recalls.total.limit:50}")
    private int mostRecentRecallsLimit;

    @Autowired
    @Value("${drug.recalls.unii.total.limit:99}")
    private int uniiRecallsLimit;


    @RequestMapping("/recalls")
    @ResponseBody
    public JsonNode getFDARecalls() throws IOException {
        RestTemplate rest = new RestTemplate();
        JsonNode node = null;
        ObjectMapper mapper = new ObjectMapper();
        node = mapper.readTree(rest.getForObject(xml2JsonCnvrtrUrl + fdaRecallsRSSurl, String.class));

        return node;
    }

    @RequestMapping("/drug/recalls")
    @ResponseBody
    public JsonNode getDrugRecalls(@RequestParam(value = "limit", defaultValue = "5") int limit,
                                   @RequestParam(value = "fromDt", defaultValue = "") String fromDt,
                                   @RequestParam(value = "toDt", defaultValue = "") String toDt) throws Exception {
        RestTemplate rest = new RestTemplate();
        JsonNode node;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(searchDrugEnfrcmntUrl)
                .queryParam("search", getDefaultReportDateQuery(fromDt, toDt))
                .queryParam("limit", mostRecentRecallsLimit);
        this.apiKey.addToUriComponentsBuilder( builder );
        ObjectMapper mapper = new ObjectMapper();
        String json = rest.getForObject(builder.build().toUri(), String.class);
        node = mapper.readTree(json);
        ((ObjectNode)node).set("results", getSortedResults(node.get("results"), limit));

        return node;
    }

    @RequestMapping("/drug/enforcements")
    @ResponseBody
    public JsonNode getDrugRecallsForUnii(
            @RequestParam(value = "unii", defaultValue = "") String unii,
            @RequestParam(value = "limit", defaultValue = "0") int limit) throws Exception {
        JsonNode node;
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ObjectMapper mapper = new ObjectMapper();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(searchDrugEnfrcmntUrl)
                .queryParam("limit", uniiRecallsLimit);
        this.apiKey.addToUriComponentsBuilder( builder );
        if (unii != null && unii.trim().length() > 0) {
            builder.queryParam("search", "openfda.unii:" + unii);
            try {
                node = mapper.readTree(rest.getForObject(builder.build().toUri(), String.class));
                ((ObjectNode)node).set("results", getSortedResults(node.get("results"), limit) );
            } catch (HttpClientErrorException ex) {
                if (ex.getStatusCode().value() == 404) {
                    node = new ObjectMapper().readTree("{\"error\":{\"code\":\"NOT_FOUND\", \"message\":\"No matches found!\"}}");
                } else {
                    throw ex;
                }
            }
        } else {
            node = getDrugRecalls(limit == 0 ? 10 : limit, null, null);
        }
        return node;
    }

    private static ArrayNode getSortedResults(JsonNode results, int objectLimit) throws Exception {
        JsonNodeFactory factory = JsonNodeFactory.instance;
        ArrayNode sortedArray = new ArrayNode(factory);
        Field innerArrayNode = ArrayNode.class.getDeclaredField("_children");
        innerArrayNode.setAccessible(true);
        List<JsonNode> recallsList = (List<JsonNode>) innerArrayNode.get(results);
        Collections.sort(recallsList, new EnforcementReportDateComparator());
        for (JsonNode obj : recallsList) {
            String jsonDistPattern = NormalizeStateCode.parseToJson(obj.get("distribution_pattern").asText());
            ((ObjectNode) obj).replace("distribution_pattern", new ObjectMapper().readTree(jsonDistPattern));
        }
        sortedArray.addAll(recallsList.subList(0, (objectLimit == 0 || objectLimit > recallsList.size()) ? recallsList.size() : objectLimit));
        return sortedArray;
    }

    private static String getDefaultFromDate() {
        Calendar now = Calendar.getInstance();
        StringBuilder fromDt = new StringBuilder().append(now.get(now.YEAR))
                .append((String.valueOf(now.get(now.MONTH) - 1).length() == 1) ? "0" + String.valueOf(now.get(now.MONTH) - 1) : String.valueOf(now.get(now.MONTH) - 1))
                .append(getCurrentDay());
        return fromDt.toString();
    }

    private static String getDefaultToDate() {
        Calendar now = Calendar.getInstance();
        StringBuilder toDt = new StringBuilder().append(now.get(now.YEAR))
                .append((String.valueOf(now.get(now.MONTH) + 1).length() == 1) ? "0" + String.valueOf(now.get(now.MONTH) + 1) : String.valueOf(now.get(now.MONTH) + 1))
                .append(getCurrentDay());
        return toDt.toString();
    }

    private static String getCurrentDay() {
        Calendar now = Calendar.getInstance();
        return (String.valueOf(now.get(now.DAY_OF_MONTH)).length() == 1 ? "0" + String.valueOf(now.get(now.DAY_OF_MONTH)) : String.valueOf(now.get(now.DAY_OF_MONTH)));
    }

    private String getDefaultReportDateQuery(String fromDt, String toDt) {
        Calendar now = Calendar.getInstance();
        StringBuilder reportDtquery = new StringBuilder()
                .append("report_date:[")
                .append((fromDt != null && fromDt.trim().length() == 8) ? fromDt : getDefaultFromDate())
                .append("+TO+")
                .append((toDt != null && toDt.trim().length() == 8) ? toDt : getDefaultToDate())
                .append("]");
        return reportDtquery.toString();
    }

}

class EnforcementReportDateComparator implements Comparator<JsonNode> {

    @Override
    public int compare(JsonNode o1, JsonNode o2) {
        return (o2.get("report_date").asInt() - o1.get("report_date").asInt());
    }
}
