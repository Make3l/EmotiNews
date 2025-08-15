package com.majkel.emotinews.model;

public class NewsArticle{
    private String author;
    private String title;
    private String description;
    private String url;
    private String publishedAt;
    private String content;

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    @Override
    public String toString(){
        StringBuilder str=new StringBuilder();
        str.append(author).append(" ").append(title).append(" ").append(description);
        return str.toString();
    }


}
