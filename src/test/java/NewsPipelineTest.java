import com.majkel.emotinews.exception.NewsApiException;
import com.majkel.emotinews.exception.ParsingNewsApiException;
import com.majkel.emotinews.model.NewsArticle;
import com.majkel.emotinews.model.NewsWithEmotions;
import com.majkel.emotinews.model.TextEmotion;
import com.majkel.emotinews.service.EmotionsAnalyzer;
import com.majkel.emotinews.service.NewsFetcher;
import com.majkel.emotinews.service.NewsPipeline;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.HttpTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NewsPipelineTest {
    private NewsFetcher newsFetcher;
    private EmotionsAnalyzer emotionsAnalyzer;
    private NewsPipeline pipeline;

    @BeforeEach
    public void setUp(){
        newsFetcher=mock(NewsFetcher.class);
        emotionsAnalyzer=mock(EmotionsAnalyzer.class);
        pipeline=new NewsPipeline(newsFetcher,emotionsAnalyzer);
    }

    @Test
    public void success() throws Exception{
        List<NewsArticle>fetcherResult=new ArrayList<>(
                Arrays.asList(new NewsArticle("Title0","Descryption0","Url0"),new NewsArticle("Title1","Descryption1","Url1")));
        List<TextEmotion> analizerResult=new ArrayList<>(Arrays.asList(new TextEmotion("Label0",0.9),new TextEmotion("Label1",0.9)));

        when(newsFetcher.getNewsList(any())).thenReturn(fetcherResult);
        when(emotionsAnalyzer.parseArticles(any())).thenReturn(analizerResult);

        List<NewsWithEmotions>pipelineResult=pipeline.loadNews();
        assertEquals(2,pipelineResult.size());
        assertEquals(fetcherResult.get(1).getTitle(),pipelineResult.get(1).getArticle().getTitle());
        assertEquals(analizerResult.get(1).getLabel(),pipelineResult.get(1).getEmotion());
    }

    @Test
    public void newsFetcherReturnedEmptyTest() throws Exception{
        when(newsFetcher.getNewsList(any())).thenReturn(new ArrayList<>());

        List<NewsWithEmotions>pipelineResult=pipeline.loadNews();

        assertNotNull(pipelineResult);
        assertTrue(pipelineResult.isEmpty());
    }

    @Test
    public void emotionsAnalyzerReturnedEmptyTest() throws Exception{
        List<NewsArticle>fetcherResult=new ArrayList<>(
                Arrays.asList(new NewsArticle("Title0","Descryption0","Url0"),new NewsArticle("Title1","Descryption1","Url1")));
        when(newsFetcher.getNewsList(any())).thenReturn(fetcherResult);
        when(emotionsAnalyzer.parseArticles(any())).thenReturn(new ArrayList<>());

        List<NewsWithEmotions>pipelineResult=pipeline.loadNews();

        assertNotNull(pipelineResult);
        assertTrue(pipelineResult.isEmpty());
    }

    @Test
    public void newsFetcherThrowsNewsApiException() throws Exception{
        when(newsFetcher.getNewsList(any())).thenThrow(new NewsApiException("Test exception"));

        List<NewsWithEmotions>pipelineResult=pipeline.loadNews();

        assertNotNull(pipelineResult);
        assertEquals(1,pipelineResult.size());
        assertEquals(NewsArticle.createFallBackNews("Test exception"),pipelineResult.getFirst().getArticle());
    }

    @Test
    public void emotionsAnalyzerThrowsHttpTimeoutException() throws Exception{
        List<NewsArticle>fetcherResult=new ArrayList<>(
                Arrays.asList(new NewsArticle("Title0","Descryption0","Url0"),new NewsArticle("Title1","Descryption1","Url1")));
        when(newsFetcher.getNewsList(any())).thenReturn(fetcherResult);
        when(emotionsAnalyzer.parseArticles(any())).thenThrow(new HttpTimeoutException("test msg"));

        List<NewsWithEmotions>pipelineResult=pipeline.loadNews();

        assertNotNull(pipelineResult);
        assertEquals(1,pipelineResult.size());
        assertEquals(NewsArticle.createAnalyzingNewsFallBackNews("Request to HuggingFace timed out"),pipelineResult.getFirst().getArticle());
    }

    @Test
    public void emotionsAnalyzerThrowsParsingNewsApiException() throws Exception{
        List<NewsArticle>fetcherResult=new ArrayList<>(
                Arrays.asList(new NewsArticle("Title0","Descryption0","Url0"),new NewsArticle("Title1","Descryption1","Url1")));
        when(newsFetcher.getNewsList(any())).thenReturn(fetcherResult);
        when(emotionsAnalyzer.parseArticles(any())).thenThrow(new ParsingNewsApiException("Invalid JSON received from HuggingFace API"));

        List<NewsWithEmotions>pipelineResult=pipeline.loadNews();

        assertNotNull(pipelineResult);
        assertEquals(1,pipelineResult.size());
        assertEquals(NewsArticle.createAnalyzingNewsFallBackNews("Invalid JSON received from HuggingFace API"),pipelineResult.getFirst().getArticle());
    }

    @Test
    public void articlesTrimmedTo20() throws Exception{
        List<NewsArticle>fetcherResult=new ArrayList<>();
        for(int i=0;i<30;i++)
            fetcherResult.add(new NewsArticle("Title "+i,"Description "+i,"Url "+i));

        List<TextEmotion>analyzerResults=new ArrayList<>();
        for(int i=0;i<20;i++)//articles are being trimmed so emotions are equals to articles trimmed
            analyzerResults.add(new TextEmotion("Emotion "+i,0.9));

        when(newsFetcher.getNewsList(any())).thenReturn(fetcherResult);
        when(emotionsAnalyzer.parseArticles(any())).thenReturn(analyzerResults);

        List<NewsWithEmotions>pipelineResults=pipeline.loadNews();

        assertNotNull(pipelineResults);
        assertEquals(20,pipelineResults.size());
        assertEquals("Title 16", pipelineResults.get(16).getArticle().getTitle());
    }
}
