package com.majkel.emotinews;

import com.majkel.emotinews.model.NewsWithEmotions;
import com.majkel.emotinews.service.NewsPipeline;
import com.majkel.emotinews.storage.JSONStorage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainApp {
    public static void main(String []args){

        List<NewsWithEmotions>newsWithEmotions=NewsPipeline.loadNews();

        for(NewsWithEmotions e: newsWithEmotions){
            System.out.println(e.getEmotion()+" "+e.getArticle());
        }

        File file=new File("src/main/resources/news.json");
        try{
            JSONStorage.save(file,newsWithEmotions);
        }catch(IOException e){
            System.out.println("Json storage save: IOEXCEPTION");
        }

        List<NewsWithEmotions>loadedList=List.of();
        try{
           loadedList=JSONStorage.load(file);
        }catch(IOException e){
            System.out.println("Json storage load: IOEXCEPTION");
        }

        if(newsWithEmotions.equals(loadedList))
            System.out.println("Git jest!!!");
    }
}
