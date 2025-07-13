package com.majkel.emotinews;

import com.majkel.emotinews.model.NewsWithEmotions;
import com.majkel.emotinews.service.NewsPipeline;
import java.util.List;

public class MainApp {
    public static void main(String []args){

        List<NewsWithEmotions>newsWithEmotions=NewsPipeline.loadNews();

        for(NewsWithEmotions e: newsWithEmotions){
            System.out.println(e.getEmotion()+" "+e.getArticle());
        }

    }
}
