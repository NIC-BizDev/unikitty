package com.nicusa.util;

import java.util.Comparator;

public class DrugSearchComparator implements Comparator<DrugSearchResult> {
  final String searchTerm;
  final String[] terms;

  public DrugSearchComparator( String searchTerm ) {
    if ( searchTerm == null ) {
      this.searchTerm = "";
      this.terms = new String[0];
    } else {
      this.searchTerm = searchTerm.trim().toUpperCase();
      this.terms = this.searchTerm.split( " " );
    }
  }

  /**
   * Give a DrugSearchResult a rating based on brand name matching with
   * the search term. Lower means a better match.
   */
  public int rate ( DrugSearchResult r1 ) {
    String bn = r1.getBrandName();
    if ( bn == null ) {
      return 0;
    } else {
      bn = bn.trim().toUpperCase();
    }
    int count = 0;
    for ( String term : this.terms ) {
      if ( bn.contains( term )) {
        count++;
      }
    }
    if ( bn.equals( this.searchTerm )) {
      return count * -10;
    } else {
      return count * -1;
    }
  }

  public int compare ( DrugSearchResult r1, DrugSearchResult r2 ) {
    int rating = this.rate( r1 ) - this.rate( r2 );

    // if both equally match the search term, fall back to alphabetical
    // brand names
    if ( rating == 0 &&
        r1.getBrandName() != null &&
        r2.getBrandName() != null ) {
      rating = r1.getBrandName().compareTo( r2.getBrandName() );
    }

    // if brand names failed to disambiguate, fall back to alphabetical
    // on unii
    if ( rating == 0 &&
        r1.getUnii() != null &&
        r2.getUnii() != null ) {
      rating = r1.getUnii().compareTo( r2.getUnii() );
    }
    return rating;
  }

  public boolean equals ( Object o ) {
    if ( o instanceof DrugSearchComparator ) {
      return this.searchTerm.equals( ((DrugSearchComparator)o).searchTerm );
    } else {
      return false;
    }
  }

}
