package com.majkel.emotinews.ui.controller;

import com.majkel.emotinews.model.Callback;
import com.majkel.emotinews.model.NewsWithEmotions;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.scene.control.Label;

public class ChartController {
    @FXML
    private PieChart emotionChart;

    @FXML
    private Label chartLabel;


    public void updateChart(Callback callback){
        List<NewsWithEmotions> news=callback.getNews();
        if(callback.getTopic()==null || callback.getTopic().isEmpty())
            return;
        chartLabel.setText("Topic: "+callback.getTopic());

        emotionChart.getData().clear();

        if(news==null || news.isEmpty())
            return;

        List<String> emotions=List.of("Positive","Neutral","Negative");

        Map<String,Long> emotionMap=news.stream().collect(Collectors.groupingBy(NewsWithEmotions::changedEmotionName,Collectors.counting()));

        for(String emotion: emotions){
            Long count=emotionMap.getOrDefault(emotion,0L);

            PieChart.Data slice= new PieChart.Data(emotion,count);
            emotionChart.getData().add(slice);

        }
    }
}
