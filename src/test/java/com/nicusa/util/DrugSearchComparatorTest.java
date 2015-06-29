package com.nicusa.util;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.IOException;

public class DrugSearchComparatorTest {

  @Test
  public void equalsTest () {
    DrugSearchComparator c1 = new DrugSearchComparator( "blah" );
    DrugSearchComparator c2 = new DrugSearchComparator( "blah" );
    assertTrue( c1.equals( c2 ));

    DrugSearchComparator c3 = new DrugSearchComparator( "notblah" );
    assertFalse( c1.equals( c3 ));
  }

  @Test
  public void rateTest () {
    DrugSearchComparator c = new DrugSearchComparator( "blah onion tomato" );
    DrugSearchResult r = new DrugSearchResult();
    r.setBrandName( "tomato" );
    assertTrue( c.rate( r ) < 0 );
    
    r.setBrandName( "blah" );
    assertTrue( c.rate( r ) < 0 );

    r.setBrandName( "onion" );
    assertTrue( c.rate( r ) < 0 );

    r.setBrandName( "pepper" );
    assertEquals( 0, c.rate( r ));
  }

  @Test
  public void compareTest () {
    DrugSearchComparator c = new DrugSearchComparator( "blah onion tomato" );
    DrugSearchResult r1 = new DrugSearchResult();
    DrugSearchResult r2 = new DrugSearchResult();

    r1.setBrandName( "blah" );
    r2.setBrandName( "pepper" );
    assertTrue( c.compare( r1, r2 ) < 0 );
    assertTrue( c.compare( r2, r1 ) > 0 );

    r2.setBrandName( "blah" );
    assertEquals( 0, c.compare( r1, r2 ));
  }

}
