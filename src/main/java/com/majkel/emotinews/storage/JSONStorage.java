package com.majkel.emotinews.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.majkel.emotinews.adapter.BooleanPropertyAdapter;
import com.majkel.emotinews.model.NewsWithEmotions;
import javafx.beans.property.BooleanProperty;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;



public class JSONStorage {

    private static final Gson gson=new GsonBuilder()
            .registerTypeAdapter(BooleanProperty.class, new BooleanPropertyAdapter())
            .setPrettyPrinting()
            .create();

    public static void save(File file,List<NewsWithEmotions> news) throws IOException{
        try(FileWriter fileWriter=new FileWriter(file)){
            gson.toJson(news,fileWriter);
        }
    }

    public static void safeSave(File file,List<NewsWithEmotions> news){
        try{
            save(file,news);
        } catch (IOException e) {
            System.err.println("Error: Could not save favourites to file: " + file.getAbsolutePath());
        }
    }

    public static List<NewsWithEmotions> load(File file) throws IOException{
        try(FileReader fileReader=new FileReader(file)){
            Type type=new TypeToken<List<NewsWithEmotions>>(){}.getType();
            List<NewsWithEmotions>list= gson.fromJson(fileReader,type);
            return list!=null?list:new ArrayList<>();
        }
    }

    public static List<NewsWithEmotions> safeLoad(File file){
        try{
            return load(file);
        } catch (IOException e) {
            System.err.println("Error: Could not read file: " + file.getAbsolutePath());
            return new ArrayList<>();
        } catch (JsonSyntaxException e) {
            System.err.println("Error: Invalid JSON format in file: " + file.getAbsolutePath());
            return new ArrayList<>();
        }
    }


}
