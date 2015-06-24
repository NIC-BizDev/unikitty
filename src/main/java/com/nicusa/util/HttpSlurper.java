package com.nicusa.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class HttpSlurper {

  private static final Logger log = LoggerFactory.getLogger(
    HttpSlurper.class);


  public String getData(String query) {
    String result = "";
    try {
      String charset = StandardCharsets.UTF_8.name();
      URLConnection conn = new URL(query).openConnection();
      conn.setRequestProperty("Accept-Charset", charset);

      BufferedReader in = new BufferedReader(
        new InputStreamReader(
          conn.getInputStream(), charset));

      StringBuilder sb = new StringBuilder();
      for (String line; (line = in.readLine()) != null; ) {
        sb.append(line);
      }
      result = sb.toString().trim();
    } catch (IOException ioe) {
      log.error("Failed getting data for query: " +
        query, ioe);
    }
    return result;
  }

}
