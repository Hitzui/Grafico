package com.dysconcsa.app.grafico.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.DefaultPropertiesPersister;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

@Component
public class PropertiesFile {

    Logger logger = LoggerFactory.getLogger(getClass());
    private final Properties configProp = new Properties();
    DefaultPropertiesPersister p = new DefaultPropertiesPersister();

    public PropertiesFile() {
        try {
            File file = new File("c:\\config\\appProperties.properties");
            if (!file.exists()) {
                boolean mkd= file.createNewFile();
                if(mkd) logger.info("Se ha creado el direcctorio de configuracion.");
                else logger.warn("No se pudo crear el directorio, revise la informacion pertinente");
            }
            FileInputStream in = new FileInputStream("c:\\config\\appProperties.properties");
            logger.info(String.valueOf(in));
            configProp.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return configProp.getProperty(key);
    }

    public Set<String> getAllPropertyNames() {
        return configProp.stringPropertyNames();
    }

    public boolean containsKey(String key) {
        return configProp.containsKey(key);
    }

    public void saveParamChanges(String key, String value) {
        try {
            // create and set properties into properties object
            configProp.setProperty(key, value);
            FileOutputStream out = new FileOutputStream("c:\\config\\appProperties.properties");
            configProp.store(out, "---No Comment---");
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
