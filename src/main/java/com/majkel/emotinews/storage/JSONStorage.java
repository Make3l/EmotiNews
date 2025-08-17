package com.majkel.emotinews.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.majkel.emotinews.model.NewsWithEmotions;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;



public class JSONStorage {

    private static final Gson gson=new GsonBuilder().setPrettyPrinting().create();

    public static void save(File file,List<NewsWithEmotions> news) throws IOException{
        try(FileWriter fileWriter=new FileWriter(file)){
            gson.toJson(news,fileWriter);
        }
    }

    public static List<NewsWithEmotions> load(File file) throws IOException{
        if(!file.exists() || file.length()==0)
            return List.of();

        try(FileReader fileReader=new FileReader(file)){
            Type type=new TypeToken<List<NewsWithEmotions>>(){}.getType();
            return gson.fromJson(fileReader,type);
        }
    }
}
