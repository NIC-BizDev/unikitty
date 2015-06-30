package com.nicusa;

import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;

public class HttpTomcatConnectorCustomizer implements TomcatConnectorCustomizer
{
    private static final Logger log = LoggerFactory.getLogger(HttpTomcatConnectorCustomizer.class);

    private Integer httpPort;
    private Integer httpsRedirectPort;

    public HttpTomcatConnectorCustomizer(Integer httpPort, Integer httpsRedirectPort)
    {
        this.httpPort = httpPort;
        this.httpsRedirectPort = httpsRedirectPort;
    }

    @Override
    public void customize (Connector connector)
    {
        log.info("httpPort: "+httpPort);
        log.info("httpsRedirectPort: "+httpsRedirectPort);
        connector.setScheme("http");
        connector.setSecure(false);
        connector.setPort(httpPort);
        if(httpsRedirectPort != null)
        {
            connector.setRedirectPort(httpsRedirectPort);
        }
    }
}
