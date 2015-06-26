package com.nicusa.resource;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

public class FeatureResourceTest {

    @Test
    public void getRequestTextShouldReturnValuePassedInSetRequestText() {
        FeatureResource featureResource = new FeatureResource();
        assertThat(featureResource.getRequestText(), is(nullValue()));
        featureResource.setRequestText("Unikitty");
        assertThat(featureResource.getRequestText(), is("Unikitty"));
    }

}