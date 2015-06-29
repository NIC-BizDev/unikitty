package com.nicusa.util;


public class FdaSearchTermUtil {

  public String makeFdaSafe ( String searchTerm ) {
    if ( searchTerm == null ) {
      return "";
    }

    searchTerm = searchTerm.toUpperCase();

    StringBuilder safe = new StringBuilder();

    // whitelist special characters because many normal characters such as
    // commas can make FDA return HTTP 500 errors.
    boolean lastSpace = true;
    for ( int i = 0; i < searchTerm.length(); i++ ) {
      char c = searchTerm.charAt( i );
      if ( this.isFdaSafe( c )) {
        lastSpace = false;
        safe.append( c );
      } else if ( !lastSpace ) {
        safe.append( ' ' );
        lastSpace = true;
      }
    }

    String rv = safe.toString().trim();

    // AND is a special FDA search keyword, remove it
    if ( rv.contains( "AND" )) {
      String[] removeAnd = rv.split( " " );
      safe = new StringBuilder( rv.length() );
      for ( int i = 0; i < removeAnd.length; i++ ) {
        if ( !"AND".equals( removeAnd[i] )) {
          if( safe.length() > 0 ) {
            safe.append( ' ' );
          }
          safe.append( removeAnd[i] );
        }
      }
      rv = safe.toString();
    }

    return rv;
  }

  public String makeFdaReady ( String searchTerm ) {
    if ( searchTerm == null ) {
      return "";
    }
    return searchTerm.replaceAll( " ", "+AND+" );
  }

  public boolean isFdaSafe ( char c ) {
    return Character.isLetter( c ) ||
      Character.isDigit( c ) ||
      c == '-';
  }

}
