package org.arquillian.example.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

@Dependent
public class PropertyReader {

    @Produces
    @PropertiesFromFile
    public Properties provideServerProperties(InjectionPoint ip) {
        //get filename from annotation
        String filename = ip.getAnnotated().getAnnotation(PropertiesFromFile.class).value();
        return readProperties(filename);
    }
    
    private Properties readProperties(String fileInClasspath) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileInClasspath);
        
        try {
            Properties properties = new Properties();
            properties.load(is);
            return properties;
        } catch (IOException e) {
            System.err.println("Could not read properties from file " + fileInClasspath + " in classpath. " + e);
        }
        
        return null;
    }
}