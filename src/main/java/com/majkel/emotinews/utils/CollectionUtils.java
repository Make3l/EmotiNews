package com.majkel.emotinews.utils;

import com.majkel.emotinews.model.NewsArticle;
import com.majkel.emotinews.model.NewsWithEmotions;
import com.majkel.emotinews.model.TextEmotion;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CollectionUtils {
    public static List<String> toStringList(List<NewsArticle> articles){
        List<String> result=new ArrayList<>();
        for(NewsArticle na: articles){
            result.add(na.getDescription().trim());
        }
        return result;
    }
    public static List<NewsWithEmotions> toNewsWithEmotionsList(List<NewsArticle> articles, List<TextEmotion> emotions){
        List<NewsWithEmotions> result=new ArrayList<>();
        for(int i=0;i<articles.size();i++)
            result.add(new NewsWithEmotions(emotions.get(i).getLabel(),articles.get(i)));

        return result;
    }

    private static boolean isValid(NewsArticle article){
        if(article.getDescription()==null || article.getDescription().isBlank() ||
                article.getTitle()==null || article.getTitle().isBlank() ||
                article.getUrl()==null || article.getUrl().isBlank())
            return false;
        return true;
    }
    public static List<NewsArticle> filterValidNews(List<NewsArticle> articles){
        return articles.stream().filter(CollectionUtils::isValid).collect(Collectors.toCollection(ArrayList::new));
    }
}
