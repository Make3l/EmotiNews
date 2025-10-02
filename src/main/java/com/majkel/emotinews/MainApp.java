package com.majkel.emotinews;

import com.majkel.emotinews.config.ConfigLoader;
import com.majkel.emotinews.model.NewsWithEmotions;
import com.majkel.emotinews.service.NewsPipeline;
import com.majkel.emotinews.storage.JSONStorage;
import com.majkel.emotinews.utils.InputSanitizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainApp {
    public static void main(String []args){

        System.out.println(InputSanitizer.filterTopic("abc"));

        List<String>sList=new ArrayList<>(Arrays.asList("tennis", "       spaces ","są","multiple    spaces"));
        sList=sList.stream().map(InputSanitizer::filterTopic).collect(Collectors.toList());
        System.out.println(sList);

    }
}
