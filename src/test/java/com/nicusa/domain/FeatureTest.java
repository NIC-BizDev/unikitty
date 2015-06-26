package com.nicusa.domain;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

public class FeatureTest {

    private Feature feature;

    @Before
    public void before() {
        feature = new Feature();
    }

    @Test
    public void getIdShouldReturnValuePassedInSetId() {
        assertThat(feature.getId(), is(nullValue()));
        feature.setId(1L);
        assertThat(feature.getId(), is(1L));
    }

    @Test
    public void getRequestTextShouldReturnValuePassedInSetRequestText() {
        assertThat(feature.getRequestText(), is(nullValue()));
        feature.setRequestText("Unikitty");
        assertThat(feature.getRequestText(), is("Unikitty"));
    }

}