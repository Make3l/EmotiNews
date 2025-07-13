package com.majkel.emotinews;

import com.majkel.emotinews.model.NewsArticle;
import com.majkel.emotinews.model.NewsWithEmotions;
import com.majkel.emotinews.model.TextEmotion;
import com.majkel.emotinews.service.EmotionsAnalyzer;
import com.majkel.emotinews.service.NewsFetcher;
import com.majkel.emotinews.utils.CollectionUtils;

import java.util.List;

public class MainApp {
    public static void main(String []args){

        NewsFetcher newsFetcher=new NewsFetcher();
        List<NewsArticle>articles = newsFetcher.getNewsList("top-headlines?q=War");
        /*
        for(NewsArticle article: articles){
            System.out.println(article+" ");
        }
         */
        List<String>lSting= CollectionUtils.toStringList(articles);
        System.out.println(lSting);

        EmotionsAnalyzer emotionsAnalyzer=new EmotionsAnalyzer();
        List<TextEmotion>emotions=emotionsAnalyzer.parseArticles(lSting);
        List<NewsWithEmotions>newsWithEmotions=CollectionUtils.toNewsWithEmotionsList(articles,emotions);

        for(NewsWithEmotions e: newsWithEmotions){
            System.out.println(e.getEmotion()+" "+e.getArticle());
        }



    }
}
