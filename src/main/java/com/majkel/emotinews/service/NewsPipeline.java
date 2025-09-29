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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NewsPipeline {

    private NewsFetcher newsFetcher;
    private EmotionsAnalyzer emotionsAnalyzer;
    private final int MAX_NUMBER_OF_FETCHED_ARTICLES=20;

    public NewsPipeline(NewsFetcher newsFetcher,EmotionsAnalyzer emotionsAnalyzer){
        this.newsFetcher=newsFetcher;
        this.emotionsAnalyzer=emotionsAnalyzer;
    }

    private List<NewsWithEmotions>load(boolean option,String tag){
        List<NewsArticle>articles=null;
        Map<String,String>urlPrams=new LinkedHashMap<>();
        String urlPath="everything";
        try {
            if (option) {//true and false to differ to "modes"
                urlPrams.put("q",tag);
                urlPrams.put("from",LocalDate.now().minusDays(2).toString());
                urlPrams.put("sortBy","popularity");
            } else {
                urlPrams.put("q","technology");
                urlPrams.put("from",LocalDate.now().minusDays(4).toString());
                urlPrams.put("sortBy","popularity");
            }
            articles=newsFetcher.getNewsList(urlPath,urlPrams);
        } catch (NewsApiException e) {
            List<NewsWithEmotions> list = new ArrayList<>();
            list.add(new NewsWithEmotions("LABEL_0", NewsArticle.createFallBackNews(e.getMessage())));
            return list;
        }

        if(articles==null || articles.isEmpty())
            return new ArrayList<>();

        if(articles.size()>MAX_NUMBER_OF_FETCHED_ARTICLES)
            articles.subList(MAX_NUMBER_OF_FETCHED_ARTICLES,articles.size()).clear();

        List<String>lSting= CollectionUtils.toStringList(articles);

        List<NewsWithEmotions>newsWithEmotions=null;

        try {
            List<TextEmotion> emotions = emotionsAnalyzer.parseArticles(lSting);
            if(emotions.isEmpty())
                return new ArrayList<>();
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
