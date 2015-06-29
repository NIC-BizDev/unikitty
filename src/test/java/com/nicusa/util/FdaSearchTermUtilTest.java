package com.nicusa.util;

import static org.junit.Assert.*;
import org.junit.Test;

public class FdaSearchTermUtilTest {

  @Test
  public void nullsAreSafeTest () {
    FdaSearchTermUtil f = new FdaSearchTermUtil();
    assertEquals( "", f.makeFdaSafe( null ));
    assertEquals( "", f.makeFdaReady( null ));
  }

  @Test
  public void removeAndTest () {
    FdaSearchTermUtil f = new FdaSearchTermUtil();
    assertEquals( "", f.makeFdaSafe( "and And AND and anD" ));
    assertEquals( "ANDY", f.makeFdaSafe( "Andy and and aNd" ));
  }

  @Test
  public void whitelistTest () {
    FdaSearchTermUtil f = new FdaSearchTermUtil();
    assertEquals( "LEONARD-NIMOY", f.makeFdaSafe( "Leonard-nimoy" ));
    assertEquals( "NIMOY LEONARD", f.makeFdaSafe( "nimoy,leonard" ));
  }

  @Test
  public void fdaSafeCharTest () {
    FdaSearchTermUtil f = new FdaSearchTermUtil();
    assertTrue( f.isFdaSafe( 'a' ));
    assertTrue( f.isFdaSafe( '-' ));
    assertFalse( f.isFdaSafe( ',' ));
  }

  @Test
  public void fdaReadyTest () {
    FdaSearchTermUtil f = new FdaSearchTermUtil();
    assertEquals( "ADVIL+AND+PM", f.makeFdaReady( "ADVIL PM" ));
  }
}

