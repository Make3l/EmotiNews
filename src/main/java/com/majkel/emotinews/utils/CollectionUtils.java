package com.majkel.emotinews.utils;

import com.majkel.emotinews.model.NewsArticle;
import com.majkel.emotinews.model.NewsWithEmotions;
import com.majkel.emotinews.model.TextEmotion;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtils {
    public static List<String> toStringList(List<NewsArticle> articles){
        List<String> result=new ArrayList<>();
        for(NewsArticle na: articles){
            result.add(na.getDescription());
        }
        return result;
    }
    public static List<NewsWithEmotions> toNewsWithEmotionsList(List<NewsArticle> articles, List<TextEmotion> emotions){
        List<NewsWithEmotions> result=new ArrayList<>();
        for(int i=0;i<articles.size();i++)
            result.add(new NewsWithEmotions(emotions.get(i).getLabel(),articles.get(i)));

        return result;
    }
}
