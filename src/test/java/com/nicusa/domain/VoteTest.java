package com.nicusa.domain;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

public class VoteTest {

    private Vote vote;

    @Before
    public void before() {
        vote = new Vote();
    }

    @Test
    public void getIdShouldReturnValuePassedInSetId() {
        assertThat(vote.getId(), is(nullValue()));
        vote.setId(1L);
        assertThat(vote.getId(), is(1L));
    }

    @Test
    public void getLikeShouldReturnValuePassedInSetLike() {
        assertThat(vote.getLike(), is(nullValue()));
        vote.setLike(Boolean.TRUE);
        assertThat(vote.getLike(), is(Boolean.TRUE));
    }

}