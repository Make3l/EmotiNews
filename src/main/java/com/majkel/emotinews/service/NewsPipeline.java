package com.majkel.emotinews.service;

import com.majkel.emotinews.exception.NewsApiException;
import com.majkel.emotinews.exception.ParsingNewsApiException;
import com.majkel.emotinews.model.NewsArticle;
import com.majkel.emotinews.model.NewsWithEmotions;
import com.majkel.emotinews.model.TextEmotion;
import com.majkel.emotinews.utils.CollectionUtils;

import java.net.http.HttpTimeoutException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NewsPipeline {

    private static List<NewsWithEmotions>load(boolean option,String tag){
        NewsFetcher newsFetcher=new NewsFetcher();
        List<NewsArticle>articles=null;

        if(option)//true and false to differ to "modes"
        {
            LocalDate localDate=LocalDate.now();
            try{
                articles = newsFetcher.getNewsList("everything?q="+tag+"&from="+localDate.minusDays(2)+"&sortBy=popularity");
            }catch (NewsApiException e){
                List<NewsWithEmotions> list=new ArrayList<>();
                list.add(new NewsWithEmotions("LABEL_0",NewsArticle.createFallBackNews(e.getMessage())));
                return list;
            }
        }else{
            try{
                articles = newsFetcher.getNewsList("everything?q=technology&from="+LocalDate.now().minusDays(4));
            }catch (NewsApiException e){
                List<NewsWithEmotions> list=new ArrayList<>();
                list.add(new NewsWithEmotions("LABEL_0",NewsArticle.createFallBackNews(e.getMessage())));
                return list;
            }
        }

        if(articles==null)
            return new ArrayList<>();

        if(articles.size()>20)
            articles.subList(20,articles.size()).clear();

        List<String>lSting= CollectionUtils.toStringList(articles);

        List<NewsWithEmotions>newsWithEmotions=null;

        EmotionsAnalyzer emotionsAnalyzer=new EmotionsAnalyzer();
        try {
            List<TextEmotion> emotions = emotionsAnalyzer.parseArticles(lSting);
            newsWithEmotions=CollectionUtils.toNewsWithEmotionsList(articles,emotions);
        } catch (HttpTimeoutException e) { // added 3 catch that do the same in order be easier to overwrite in future
            newsWithEmotions=new ArrayList<>();
            newsWithEmotions.add(new NewsWithEmotions("LABEL_0",NewsArticle.createAnalyzingNewsFallBackNews("Request to HuggingFace timed out")));
        } catch (ParsingNewsApiException e){
            newsWithEmotions=new ArrayList<>();
            newsWithEmotions.add(new NewsWithEmotions("LABEL_0",NewsArticle.createAnalyzingNewsFallBackNews(e.getMessage())));
        } catch (Exception e) {
            newsWithEmotions=new ArrayList<>();
            newsWithEmotions.add(new NewsWithEmotions("LABEL_0",NewsArticle.createAnalyzingNewsFallBackNews(e.getMessage())));
        }

        return newsWithEmotions;

    }

    public static List<NewsWithEmotions> loadNews(String tag){
        return load(true,tag);//true and false to differ to "modes"
    }

    public static List<NewsWithEmotions> loadNews(){
        return load(false,"");//true and false to differ to "modes"
    }

}
