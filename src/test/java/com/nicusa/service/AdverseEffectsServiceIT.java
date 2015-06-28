package com.nicusa.service;

import com.nicusa.PersistenceConfiguration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PersistenceConfiguration.class)
@EnableAutoConfiguration
@ActiveProfiles("local")
public class AdverseEffectsServiceIT
{
    Logger log = LoggerFactory.getLogger(AdverseEffectsServiceIT.class);

    @PersistenceContext 
    EntityManager entityManager;
    
    @Autowired
    private AdverseEffectService adverseEffectService;

    
//    @Test
//    public void testMerriamWebsterCall() throws IOException
//    {
//        RestTemplate rest = new RestTemplate();
//        HttpSlurper slurp = new HttpSlurper();
//        String val = "CEREBROVASCULAR ACCIDENT";
//        String eval = URLEncoder.encode(val, "UTF-8");
//        String s = slurp.getData("http://www.dictionaryapi.com/api/v1/references/medical/xml/"+eval+"?key=21a8b25b-2e1b-4969-9a28-d522f5782b26");
//        if(s.contains("<dt>"))
//        {
//            String def = s.substring(s.indexOf("<dt>")+4, s.indexOf("</dt>"));
//            System.out.println(def);
//        }
//    }
}
