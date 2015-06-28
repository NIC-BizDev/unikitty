package com.nicusa.service;

import com.nicusa.domain.AdverseEffectDescription;
import com.nicusa.util.HttpSlurper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;
import javax.xml.parsers.DocumentBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Service(AdverseEffectService.NAME)
public class AdverseEffectService
{
    Logger log = LoggerFactory.getLogger(AdverseEffectService.class);
    public static final String NAME = "adverseEffectService";
    

    @PersistenceContext
    EntityManager entityManager;
    
    @Autowired
    DocumentBuilder documentBuilder;
    
    @Autowired
    @Value("${merriam.webster.key:}")
    private String merriamWebsterKey;

    @Autowired
    @Value("${meriam.webster.medical.url:http://www.dictionaryapi.com/api/v1/references/medical/xml/}")
    private String meriamWebsterUrl;
    

    public String findEffectDescription(String effectName) throws IOException {
        String retval = null;
        
        AdverseEffectDescription desc = findEventDescriptionInCache(effectName);
        if(desc == null)
        {
            desc = retrieveDescriptionFromDictionary(effectName);
        }
        return desc.getDescription();
    }
    
    @Transactional
    private AdverseEffectDescription retrieveDescriptionFromDictionary(String effectName) throws IOException
    {
        AdverseEffectDescription desc = new AdverseEffectDescription();
        desc.setFdaName(effectName);
        desc.setDescription(effectName.toLowerCase());
        try {
            if(merriamWebsterKey != null && merriamWebsterKey.length() > 0)
            {
                String eval = URLEncoder.encode(effectName, "UTF-8");
                HttpSlurper slurp = new HttpSlurper();
                String s = slurp.getData(meriamWebsterUrl+eval+"?key="+merriamWebsterKey);
                String definition = null;
    
                definition = parseDefinition(s);
                if(definition != null)
                {
                    desc.setDescription(definition);
                }
                
                entityManager.persist(desc);
            }
        } catch (IOException | SAXException ioe)
        {
            log.warn("Unable to parse definition for "+effectName);
        } catch (TransactionRequiredException tre)
        {
            log.warn("unable to cache adverse effect description");
        }
        return desc;
    }
    
    private AdverseEffectDescription findEventDescriptionInCache(String fdaEventName) {
        AdverseEffectDescription retval = null;
        Query q = entityManager.createQuery("SELECT a FROM AdverseEffectDescription a WHERE a.fdaName = :fdaName");
        Collection<AdverseEffectDescription> aeDescriptions = q.setParameter("fdaName", fdaEventName).getResultList();
        if(aeDescriptions.size() > 0)
        {
            retval = (AdverseEffectDescription)aeDescriptions.toArray()[0];
        }
        return retval;
    }
    
    private String parseDefinition(String xml) throws IOException, SAXException
    {
        String retval = null;
        InputStream is = new ByteArrayInputStream(xml.getBytes());
        
        Document dom = documentBuilder.parse(is);
        Element root = dom.getDocumentElement();
        NodeList dts = root.getElementsByTagName("dt");
        if(dts != null && dts.getLength() > 0)
        {
            Element dt = (Element)dts.item(0);
            retval = dt.getTextContent();
        }
        return retval;
    }
    

}
