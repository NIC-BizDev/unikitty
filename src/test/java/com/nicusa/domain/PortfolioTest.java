package com.nicusa.domain;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

/**
 * Created by mchurch on 6/23/15.
 */
public class PortfolioTest {

    private Portfolio portfolio;

    @Before
    public void before() {
        portfolio = new Portfolio();
    }

    @Test
    public void testGetId() {
        assertThat(portfolio.getId(), is(nullValue()));
        portfolio.setId(1L);
        assertThat(portfolio.getId(), is(1L));
    }

    @Test
    public void testGetDrugs() {
        assertThat(portfolio.getDrugs(), is(empty()));
        Collection<Drug> drugs = new ArrayList<>();
        drugs.add(new Drug());
        portfolio.setDrugs(drugs);
        assertThat(portfolio.getDrugs(), is(drugs));
    }

    @Test
    public void testGetName() {
      assertThat(portfolio.getName(), is("My Meds"));
    }

}
