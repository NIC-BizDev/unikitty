package com.nicusa.service;

import com.nicusa.PersistenceConfiguration;
import com.nicusa.TestConfig;

import java.io.IOException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
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

    @Test
    public void testFindEffectDescription() throws IOException {
      String desc = adverseEffectService.findEffectDescription( "CEREBROVASCULAR ACCIDENT" );
      assertTrue( desc.contains( "cerebro" ));
    }

}
