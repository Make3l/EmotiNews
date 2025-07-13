package com.majkel.emotinews.service;

import com.majkel.emotinews.model.NewsArticle;
import com.majkel.emotinews.model.NewsWithEmotions;
import com.majkel.emotinews.model.TextEmotion;
import com.majkel.emotinews.utils.CollectionUtils;

import java.time.LocalDate;
import java.util.List;

public class NewsPipeline {
    public static List<NewsWithEmotions> loadNews(String tag){
        NewsFetcher newsFetcher=new NewsFetcher();
        List<NewsArticle>articles = newsFetcher.getNewsList("top-headlines?q="+tag);
        if(articles.size()>15) articles.subList(15,articles.size()).clear();
        List<String>lSting= CollectionUtils.toStringList(articles);

        EmotionsAnalyzer emotionsAnalyzer=new EmotionsAnalyzer();
        List<TextEmotion>emotions=emotionsAnalyzer.parseArticles(lSting);
        List<NewsWithEmotions>newsWithEmotions=CollectionUtils.toNewsWithEmotionsList(articles,emotions);

        return newsWithEmotions;
    }

    public static List<NewsWithEmotions> loadNews(){
        NewsFetcher newsFetcher=new NewsFetcher();
        List<NewsArticle>articles = newsFetcher.getNewsList("everything?q=technology&from="+LocalDate.now().minusDays(2));
        if(articles.size()>15) articles.subList(15,articles.size()).clear();
        List<String>lSting= CollectionUtils.toStringList(articles);

        EmotionsAnalyzer emotionsAnalyzer=new EmotionsAnalyzer();
        List<TextEmotion>emotions=emotionsAnalyzer.parseArticles(lSting);
        List<NewsWithEmotions>newsWithEmotions=CollectionUtils.toNewsWithEmotionsList(articles,emotions);

        return newsWithEmotions;
    }

}
