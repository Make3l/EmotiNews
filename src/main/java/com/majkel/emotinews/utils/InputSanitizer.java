package com.majkel.emotinews.utils;

public class InputSanitizer {

    private static final int MAX_CODEPOINTS=128;

    public static String filterTopic(String topic){
        if(topic==null || topic.isBlank())
            return "";

        String result=topic.trim();

        result=result.replaceAll("\\s+"," ");//replacing multiple white characters with a single space

        result=result.replace("\\", "");// deletes any \ with

        result=result.replaceAll("\\p{C}","");// \p{C} = invisible control characters


        int codePoints=result.codePointCount(0,result.length());
        if(codePoints>MAX_CODEPOINTS){
            int end=result.offsetByCodePoints(0,MAX_CODEPOINTS);
            result=result.substring(0,end);
        }

        return result;
    }

}
