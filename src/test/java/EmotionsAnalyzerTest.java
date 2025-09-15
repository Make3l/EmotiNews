import com.majkel.emotinews.exception.ParsingNewsApiException;
import com.majkel.emotinews.model.TextEmotion;
import com.majkel.emotinews.service.EmotionsAnalyzer;
import com.majkel.emotinews.service.HttpClientPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.Parameter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EmotionsAnalyzerTest {
    private HttpClientPort httpClient;
    private HttpResponse<String> response;
    private EmotionsAnalyzer emotionsAnalyzer;

    @BeforeEach
    public void setUp(){
        httpClient=mock(HttpClientPort.class);
        response=mock(HttpResponse.class);
        emotionsAnalyzer=new EmotionsAnalyzer(httpClient,"API_KEY");
    }

    @Test
    public void testEmotionAnalystSuccess() throws Exception{
        String body = "[[{\"label\":\"LABEL_2\",\"score\":0.9},{\"label\":\"LABEL_0\",\"score\":0.9},{\"label\":\"LABEL_2\",\"score\":0.9}]]";

        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn(body);

        when(httpClient.send(any())).thenReturn(response);

        List<String> articlesInStringList=new ArrayList<>(Arrays.asList("Hi, ","This is","test"));
        List<TextEmotion> emotions=emotionsAnalyzer.parseArticles(articlesInStringList);

        assertEquals(3,emotions.size());
        assertEquals("LABEL_2",emotions.get(2).getLabel());

        verify(httpClient,times(1)).send(any());
    }

    @Test
    public void testNullProvidedStringList() throws Exception{
        List<TextEmotion>emotions=emotionsAnalyzer.parseArticles(null);

        assertNotNull(emotions);
        assertTrue(emotions.isEmpty());
    }

    @Test
    public void testNullResponse() throws Exception{
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn(null);

        when(httpClient.send(any())).thenReturn(response);

        List<String> articlesInStringList=new ArrayList<>(Arrays.asList("Hi, ","This is","test"));
        List<TextEmotion>emotions=emotionsAnalyzer.parseArticles(articlesInStringList);

        assertNotNull(emotions);
        assertTrue(emotions.isEmpty());
    }

    @Test
    public void testInvalidJson() throws Exception{
        String body="{Invalid Json}";

        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn(body);

        when(httpClient.send(any())).thenReturn(response);

        List<String> articlesInStringList=new ArrayList<>(Arrays.asList("Hi, ","This is","test"));

        assertThrows(ParsingNewsApiException.class,()->emotionsAnalyzer.parseArticles(articlesInStringList));
    }

    @ParameterizedTest
    @ValueSource(ints={401,403,429,400})
    public void testWrongStatusCodes(int code) throws Exception{
        when(response.statusCode()).thenReturn(code);

        when(httpClient.send(any())).thenReturn(response);

        List<String> articlesInStringList=new ArrayList<>(Arrays.asList("Hi, ","This is","test"));

        assertThrows(ParsingNewsApiException.class,()->emotionsAnalyzer.parseArticles(articlesInStringList));
    }

    @Test
    public void testThrowingHttpTimeoutException() throws Exception{
        when(httpClient.send(any())).thenThrow(new HttpTimeoutException(null));

        List<String> articlesInStringList=new ArrayList<>(Arrays.asList("Hi, ","This is","test"));
        assertThrows(HttpTimeoutException.class,()->emotionsAnalyzer.parseArticles(articlesInStringList));
    }

    @Test
    public void testThrowingIOException() throws Exception{
        when(httpClient.send(any())).thenThrow(new IOException());

        List<String> articlesInStringList=new ArrayList<>(Arrays.asList("Hi, ","This is","test"));
        assertThrows(ParsingNewsApiException.class,()->emotionsAnalyzer.parseArticles(articlesInStringList));
    }
}
