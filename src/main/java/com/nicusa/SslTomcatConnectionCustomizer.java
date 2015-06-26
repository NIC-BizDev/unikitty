package com.nicusa;

import java.io.FileNotFoundException;

import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.util.ResourceUtils;

public class SslTomcatConnectionCustomizer implements TomcatConnectorCustomizer
{
    private static final Logger log = LoggerFactory.getLogger(SslTomcatConnectionCustomizer.class);
    
    private String keystoreFile;
    private String keystorePassword;
    private String keystoreType;
    private String keystoreAlias;

    public SslTomcatConnectionCustomizer(String absoluteKeystoreFile, String keystorePassword, String keystoreType, String keystoreAlias)
    {
        this.keystoreFile = absoluteKeystoreFile;
        this.keystorePassword = keystorePassword;
        this.keystoreType = keystoreType;
        this.keystoreAlias = keystoreAlias;
    }

    @Override
    public void customize (Connector connector)
    {
        if (keystoreFile != null)
        {
            String absoluteKeystoreFile = null;
            try {
                absoluteKeystoreFile = ResourceUtils.getFile(keystoreFile).getAbsolutePath();
    
                connector.setPort(8443);
                connector.setSecure(true);
                connector.setScheme("https");
    
                connector.setAttribute("SSLEnabled", true);
                connector.setAttribute("sslProtocol", "TLS");
                connector.setAttribute("protocol", "org.apache.coyote.http11.Http11Protocol");
                connector.setAttribute("clientAuth", false);
                connector.setAttribute("keystoreFile", absoluteKeystoreFile);
                connector.setAttribute("keystoreType", keystoreType);
                connector.setAttribute("keystorePass", keystorePassword);
                connector.setAttribute("keystoreAlias", keystoreAlias);
                connector.setAttribute("keyPass", keystorePassword);
            } catch (FileNotFoundException fnfe)
            {
                log.error("Could not find keystoreFile:  "+keystoreFile+".");
            }
            
            if(absoluteKeystoreFile == null)
            {
                log.warn("Starting server with SSL encryption turned off");
            }
        }   
    }

}
