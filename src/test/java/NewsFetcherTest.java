import com.majkel.emotinews.exception.NewsApiException;
import com.majkel.emotinews.model.NewsArticle;
import com.majkel.emotinews.service.HttpClientPort;
import com.majkel.emotinews.service.NewsFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class NewsFetcherTest {

    private HttpClientPort httpClient;
    private NewsFetcher newsFetcher;
    private HttpResponse<String> response;

    @BeforeEach
    public void setUp(){
        httpClient=mock(HttpClientPort.class);
        response=mock(HttpResponse.class);
        newsFetcher=new NewsFetcher(httpClient,"API_KEY");
    }

    @Test
    public void testFetchSuccess() throws Exception {
        String responseBody="{\"status\":\"ok\",\"totalResults\":2,\"articles\":[{\"source\":{\"id\":\"Id\",\"name\":\"Name\"},\"author\":\"Author\",\"title\":\"Title\",\"description\":\"Description\",\"url\":\"https://example.com/article1\",\"urlToImage\":\"https://example.com/image1.jpg\",\"publishedAt\":\"2025-09-14T12:00:00Z\",\"content\":\"Content of the article 1...\"},{\"source\":{\"id\":\"associated-press\",\"name\":\"Associated Press\"},\"author\":\"AP Reporter\",\"title\":\"Breaking News Title\",\"description\":\"Breaking news description...\",\"url\":\"https://apnews.com/example\",\"urlToImage\":\"https://apnews.com/image.jpg\",\"publishedAt\":\"2025-09-14T13:00:00Z\",\"content\":\"Full article content...\"}]}";
        String urlPath="testUrlPath";
        Map<String,String> urlParams=new LinkedHashMap<>();
        urlParams.put("q","test");

        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn(responseBody);

        when(httpClient.send(any())).thenReturn(response);

        List<NewsArticle>articles= newsFetcher.getNewsList(urlPath,urlParams);

        assertEquals(2,articles.size());
        assertEquals("Author", articles.get(0).getAuthor());

        verify(httpClient,times(1)).send(any());
    }

    @ParameterizedTest
    @ValueSource(ints = {401, 301, 400})
    public void testWrongStatusCodes(int statusCodeNumber) throws Exception{
        String urlPath="testUrlPath";
        Map<String,String> urlParams=new LinkedHashMap<>();
        urlParams.put("q","test");

        when(response.statusCode()).thenReturn(statusCodeNumber);

        when(httpClient.send(any())).thenReturn(response);

        assertThrows(NewsApiException.class,()->newsFetcher.getNewsList(urlPath,urlParams));
    }

    @Test
    public void nullResponse() throws Exception{
        String urlPath="testUrlPath";
        Map<String,String> urlParams=new LinkedHashMap<>();
        urlParams.put("q","test");

        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn(null);

        when(httpClient.send(any())).thenReturn(response);

        List<NewsArticle> articles=newsFetcher.getNewsList(urlPath,urlParams);

        assertNotNull(articles);
        assertEquals(0,articles.size());
    }

    @Test
    public void emptyArticlesResponse() throws Exception{
        String body="{\"status\":\"ok\",\"totalResults\":0,\"articles\":[]}";
        String urlPath="testUrlPath";
        Map<String,String> urlParams=new LinkedHashMap<>();
        urlParams.put("q","test");

        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn(body);

        when(httpClient.send(any())).thenReturn(response);

        List<NewsArticle>articles=newsFetcher.getNewsList(urlPath,urlParams);

        assertNotNull(articles);
        assertEquals(0,articles.size());
    }

    @Test
    public void invalidJsonThrowsException() throws Exception{
        String body="{invalid json}";
        String urlPath="testUrlPath";
        Map<String,String> urlParams=new LinkedHashMap<>();
        urlParams.put("q","test");

        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn(body);

        when(httpClient.send(any())).thenReturn(response);

        assertThrows(NewsApiException.class, ()->newsFetcher.getNewsList(urlPath,urlParams));
    }

    @Test
    public void ioExceptionIsWrapped() throws Exception{
        String urlPath="testUrlPath";
        Map<String,String> urlParams=new LinkedHashMap<>();
        urlParams.put("q","test");

        when(httpClient.send(any())).thenThrow(new IOException("Network error"));

        assertThrows(NewsApiException.class,()->newsFetcher.getNewsList(urlPath,urlParams));
    }

    @Test
    public void nullUrlPath(){
        String urlPath=null;
        Map<String,String> urlParams=new LinkedHashMap<>();
        urlParams.put("q","test");

        assertThrows(IllegalArgumentException.class, ()->newsFetcher.getNewsList(urlPath,urlParams));
    }

}
