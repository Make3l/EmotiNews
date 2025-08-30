package com.majkel.emotinews.model;

public class CallbackFav {
    private final NewsWithEmotions news;
    private final boolean addDelete;//true - add, false - delete

    public CallbackFav(NewsWithEmotions news, boolean addDelete){
        this.news=news;
        this.addDelete=addDelete;
    }

    public NewsWithEmotions getNews(){
        return news;
    }
    public boolean getAddDelete(){
        return addDelete;
    }

}
