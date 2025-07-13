package com.majkel.emotinews.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final String path="/config.properties";

    public static String getValue(String key){
        String result="";

        try(InputStream input=ConfigLoader.class.getResourceAsStream(path)){
            if(input==null)
                throw new IllegalArgumentException("ERROR: File "+path+ " not found");
            Properties properties=new Properties();
            properties.load(input);
            result=properties.getProperty(key);
        }catch(IOException e){
            System.out.println("ERROR: IOException"+e.getMessage());
        }
        if(result==null || result.isEmpty())
            throw new IllegalArgumentException("ERROR: Key does not have value");
        return result;
    }
}
