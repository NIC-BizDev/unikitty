package com.nicusa.converter;

import com.nicusa.controller.DrugController;
import com.nicusa.domain.Drug;
import com.nicusa.resource.DrugResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.EntityManager;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RunWith(MockitoJUnitRunner.class)
public class DrugResourceToDomainConverterTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private DrugResourceToDomainConverter drugResourceToDomainConverter;

    @Test
    public void testConvertNewEntity() throws Exception {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
        DrugResourceToDomainConverter drugResourceToDomainConverter = new DrugResourceToDomainConverter();
        DrugResource drugResource = new DrugResource();
        drugResource.setName("unikitty");
        drugResource.setUnii("6M3C89ZY6R");
        Drug drug = drugResourceToDomainConverter.convert(drugResource);
        assertThat(drugResource.getName(), is(drug.getName()));
        assertThat(drugResource.getUnii(), is(drug.getUnii()));
    }

    @Test
    public void testConvertExistingEntity() {
        Drug persistedDrug = new Drug();
        persistedDrug.setId(1L);
        when(entityManager.find(Drug.class, 1L)).thenReturn(persistedDrug);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
        DrugResource drugResource = new DrugResource();
        drugResource.setName("unikitty");
        drugResource.setUnii("6M3C89ZY6R");
        drugResource.add(linkTo(methodOn(DrugController.class).get(1L)).withRel("self"));
        Drug drug = drugResourceToDomainConverter.convert(drugResource);
        assertThat(drugResource.getName(), is(drug.getName()));
        assertThat(drug.getId(), is(1L));
    }


}
