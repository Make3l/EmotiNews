package com.majkel.emotinews.utils;

import com.majkel.emotinews.model.NewsArticle;
import com.majkel.emotinews.model.NewsWithEmotions;
import com.majkel.emotinews.model.TextEmotion;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CollectionUtils {
    public static List<String> toStringList(List<NewsArticle> articles){
        if(articles==null || articles.isEmpty())
            return new ArrayList<>();

        List<String> result=new ArrayList<>();
        for(NewsArticle na: articles){
            result.add(na.getDescription().trim());
        }
        return result;
    }
    public static List<NewsWithEmotions> toNewsWithEmotionsList(List<NewsArticle> articles, List<TextEmotion> emotions){
        if(articles==null || emotions==null)
            throw new IllegalArgumentException("articles and emotions cannot be null");

        if(articles.size()!=emotions.size())
            throw new IllegalArgumentException("articles.size() have to be equals to emotions.size() in order to use toNewsWithEmotionsList, method");

        List<NewsWithEmotions> result=new ArrayList<>();
        for(int i=0;i<articles.size();i++)
            result.add(new NewsWithEmotions(emotions.get(i).getLabel(),articles.get(i)));

        return result;
    }

    private static boolean isValid(NewsArticle article){
        return (article.getDescription()!=null && !article.getDescription().isBlank() &&
                article.getTitle()!=null && !article.getTitle().isBlank() &&
                article.getUrl()!=null && !article.getUrl().isBlank());
    }
    public static List<NewsArticle> filterValidNews(List<NewsArticle> articles){
        if(articles==null)
            throw new IllegalArgumentException("articles cannot be null");
        return articles.stream().filter(CollectionUtils::isValid).collect(Collectors.toCollection(ArrayList::new));
    }
}
