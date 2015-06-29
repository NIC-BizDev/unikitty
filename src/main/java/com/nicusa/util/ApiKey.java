package com.nicusa.util;

import com.google.common.util.concurrent.RateLimiter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ApiKey {
  private static final Logger log = LoggerFactory.getLogger(ApiKey.class);

  // 4 requests per second per key
  private static RateLimiter rateLimiter = null;

  private static int totalRequests = 0;

  @Autowired
  @Value("${api.fda.keys:}")
  String[] fdaApiKeys;

  public boolean hasKeys () {
    return ( fdaApiKeys != null &&
        fdaApiKeys.length > 0 &&
        fdaApiKeys[0] != null &&
        fdaApiKeys[0].trim().length() > 1 );
  }

  String getFdaApiKey () {
    if ( this.hasKeys() ) {
      if ( rateLimiter == null ) {
        rateLimiter = RateLimiter.create( this.fdaApiKeys.length * 3.5 );
      }
      rateLimiter.acquire(); // may wait
      String rv = this.fdaApiKeys[totalRequests++ % this.fdaApiKeys.length];
      return rv;
    } else {
      return null;
    }
  }

  public String getFdaApiKeyName () {
    return "api_key";
  }

  public String getFdaApiKeyQuery () {
    if ( this.hasKeys() ) {
      return "&" + this.getFdaApiKeyName() + "=" + this.getFdaApiKey();
    } else {
      return "";
    }
  }

  public void addToUriComponentsBuilder (
      UriComponentsBuilder builder ) {
    if ( this.hasKeys() ) {
      builder.queryParam( this.getFdaApiKeyName(), this.getFdaApiKey() );
    }
  }
}
