package com.majkel.emotinews.exception;

public class NewsApiException extends RuntimeException{
  public NewsApiException(String msg){
    super(msg);
  }

  public NewsApiException(String msg, Throwable cause){
    super(msg,cause);
  }

}
