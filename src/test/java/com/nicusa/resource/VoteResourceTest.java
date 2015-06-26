package com.nicusa.resource;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

public class VoteResourceTest {

    @Test
    public void getLikeShouldShouldReturnValuePassedInSetLike() {
        VoteResource voteResource = new VoteResource();
        assertThat(voteResource.getLike(), is(nullValue()));
        voteResource.setLike(Boolean.TRUE);
        assertThat(voteResource.getLike(), is(Boolean.TRUE));
    }
}