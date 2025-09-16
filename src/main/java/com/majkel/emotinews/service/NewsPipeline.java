package com.majkel.emotinews.service;

import com.majkel.emotinews.adapter.HttpClientWrapper;
import com.majkel.emotinews.config.ConfigLoader;
import com.majkel.emotinews.exception.NewsApiException;
import com.majkel.emotinews.exception.ParsingNewsApiException;
import com.majkel.emotinews.model.NewsArticle;
import com.majkel.emotinews.model.NewsWithEmotions;
import com.majkel.emotinews.model.TextEmotion;
import com.majkel.emotinews.utils.CollectionUtils;

import java.net.http.HttpClient;
import java.net.http.HttpTimeoutException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NewsPipeline {

    private NewsFetcher newsFetcher;
    private EmotionsAnalyzer emotionsAnalyzer;

    public NewsPipeline(NewsFetcher newsFetcher,EmotionsAnalyzer emotionsAnalyzer){
        this.newsFetcher=newsFetcher;
        this.emotionsAnalyzer=emotionsAnalyzer;
    }

    private List<NewsWithEmotions>load(boolean option,String tag){
        List<NewsArticle>articles=null;

        try {
            if (option) {//true and false to differ to "modes"
                articles = newsFetcher.getNewsList("everything?q=" + tag + "&from=" + LocalDate.now().minusDays(2) + "&sortBy=popularity");
            } else {
                articles = newsFetcher.getNewsList("everything?q=technology&from=" + LocalDate.now().minusDays(4));
            }
        } catch (NewsApiException e) {
            List<NewsWithEmotions> list = new ArrayList<>();
            list.add(new NewsWithEmotions("LABEL_0", NewsArticle.createFallBackNews(e.getMessage())));
            return list;
        }

        if(articles==null || articles.isEmpty())
            return new ArrayList<>();

        if(articles.size()>20)
            articles.subList(20,articles.size()).clear();

        List<String>lSting= CollectionUtils.toStringList(articles);

        List<NewsWithEmotions>newsWithEmotions=null;

        try {
            List<TextEmotion> emotions = emotionsAnalyzer.parseArticles(lSting);
            newsWithEmotions=CollectionUtils.toNewsWithEmotionsList(articles,emotions);
        } catch (HttpTimeoutException e) { // added 3 catches that do the same in order to be easier to overwrite(each individually) in future
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

    public List<NewsWithEmotions> loadNews(String tag){
        return load(true,tag);//true and false to differ to "modes"
    }

    public List<NewsWithEmotions> loadNews(){
        return load(false,"");//true and false to differ to "modes"
    }

}
