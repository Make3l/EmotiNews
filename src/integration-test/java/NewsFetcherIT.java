import com.majkel.emotinews.adapter.HttpClientWrapper;
import com.majkel.emotinews.config.ConfigLoader;
import com.majkel.emotinews.exception.NewsApiException;
import com.majkel.emotinews.service.NewsFetcher;
import org.junit.jupiter.api.*;

import java.net.http.HttpClient;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.majkel.emotinews.model.NewsArticle;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
@DisplayName("NewsFetcher integration tests")
public class NewsFetcherIT {

    private static String apiKey;
    private NewsFetcher newsFetcher;

    @BeforeAll
    public static void checkPrerequisites(){
        apiKey=ConfigLoader.getValue("api.news.key");
        Assumptions.assumeTrue(apiKey!=null && !apiKey.isBlank(), "API key not configured, skipping integration tests");
    }

    @BeforeEach
    public void setUp(){
        newsFetcher=new NewsFetcher(new HttpClientWrapper(HttpClient.newHttpClient()), apiKey);
    }

    private void assertValidNewsStructure(List<NewsArticle>newsArticles){
        newsArticles.forEach(news->
            assertAll("Validate News",
                    () -> assertNotNull(news.getTitle(), "Article should have title"),
                    () -> assertNotNull(news.getUrl(), "Article should have uri"),
                    () -> assertNotNull(news.getDescription(), "Article should have description")
        ));
    }

    @Test
    @Timeout(value=35,unit=TimeUnit.SECONDS)
    @DisplayName("Should fetch news articles with everything path and technology topic")
    public void fetchNewsWithEverythingEndpoint() throws Exception{

        String uriPath="everything";
        Map<String, String> uriParams= new LinkedHashMap<>(Map.of(
                "q","technology",
                "from",LocalDate.now().minusDays(4).toString(),
                "sortBy","popularity",
                "pageSize","5"
        ));

        List<NewsArticle>news=newsFetcher.getNewsList(uriPath,uriParams);

        assertNotNull(news, "News list should not be null");
        assertFalse(news.isEmpty(), "Expected at least one article from NewsAPI");
        assertTrue(news.size()<=5,"Should respect pageSize limit");

        assertValidNewsStructure(news);
    }

    @Test
    @Timeout(value=35,unit=TimeUnit.SECONDS)
    @DisplayName("Should fetch news articles with top-headlines path and technology topic")
    public void fetchNewsWithTopHeadlinesEndpoint() throws Exception{
        String uriPath="top-headlines";
        Map<String,String>uriParams=new LinkedHashMap<>(Map.of(
                "q","technology",
                "sortBy","popularity",
                "pageSize","5"
        ));

        List<NewsArticle>news=newsFetcher.getNewsList(uriPath,uriParams);

        assertNotNull(news, "News list should not be null");
        assertFalse(news.isEmpty(), "Expected at least one article from NewsAPI");
        assertTrue(news.size()<=5,"Should respect pageSize limit");

        assertValidNewsStructure(news);
    }


    @Test
    @Timeout(value=35,unit=TimeUnit.SECONDS)
    @DisplayName("Should fetch news articles with -5 days to -2 days range")
    public void fetchNewsWithinDateRange() throws Exception{
        String uriPath="everything";
        LocalDate minusFiveDaysDate=LocalDate.now(ZoneOffset.UTC).minusDays(5);//had to get zone UTC because results come in that zone(in UTC (+000))
        LocalDate minusTwoDaysDate=LocalDate.now(ZoneOffset.UTC).minusDays(2);
        Map<String,String>uriParams=new LinkedHashMap<>(Map.of(
                "q","technology",
                "from", minusFiveDaysDate.toString(),
                "to",minusTwoDaysDate.toString(),
                "sortBy","popularity",
                "pageSize","8"
        ));

        List<NewsArticle>news=newsFetcher.getNewsList(uriPath,uriParams);

        assertNotNull(news, "News list should not be null");
        assertFalse(news.isEmpty(), "Expected at least one article from NewsAPI");
        assertTrue(news.size()<=8,"Should respect pageSize limit");

        assertValidNewsStructure(news);


        //checks if not all articles are being filtered
        long articlesWithDate = news.stream()
                .filter(article -> article.getPublishedAt() != null && !article.getPublishedAt().isBlank())
                .count();

        assertTrue(articlesWithDate > 0, "At least one article should have publishedAt");


        //checks if news articles with -5 days to -2 days range
        assertTrue(news.stream()
                .filter(newsArticle -> newsArticle.getPublishedAt()!=null && !newsArticle.getPublishedAt().isBlank())
                .allMatch(newsArticle-> {
                    String dateToConvert=newsArticle.getPublishedAt();
                    Instant instant=Instant.parse(dateToConvert);
                    LocalDate convertedDate=instant.atZone(ZoneOffset.UTC).toLocalDate();

                    // Check: convertedDate ∈ [today-5, today-2]
                    boolean inRange = (convertedDate.isAfter(minusFiveDaysDate) || convertedDate.isEqual(minusFiveDaysDate)) &&
                            (convertedDate.isBefore(minusTwoDaysDate) || convertedDate.isEqual(minusTwoDaysDate));
                    return inRange;
                }),"All articles should be within date range [" + minusFiveDaysDate + ", " + minusTwoDaysDate + "]");


    }


    @Test
    @Timeout(value=35,unit=TimeUnit.SECONDS)
    @DisplayName("Should fetch news articles with topic containing special characters e.g. space")
    public void fetchNewsWithSpecialCharacters() throws Exception{

        String uriPath="everything";
        Map<String, String> uriParams= new LinkedHashMap<>(Map.of(
                "q","ai technology",
                "from",LocalDate.now().minusDays(4).toString(),
                "sortBy","popularity",
                "pageSize","8"
        ));

        List<NewsArticle>news=newsFetcher.getNewsList(uriPath,uriParams);

        assertNotNull(news, "News list should not be null");
        assertFalse(news.isEmpty(), "Expected at least one article from NewsAPI");
        assertTrue(news.size()<=8,"Should respect pageSize limit");

        assertValidNewsStructure(news);
    }


    @Test
    @DisplayName("Should throw NewsApiException")
    public void invalidApiKeyThrowsException(){
        String uriPath="everything";
        Map<String, String> uriParams= new LinkedHashMap<>(Map.of(
                "q","technology",
                "from",LocalDate.now().minusDays(4).toString(),
                "sortBy","popularity",
                "pageSize","5"
        ));

        NewsFetcher invalidApiNewsFetcher=new NewsFetcher(new HttpClientWrapper(HttpClient.newHttpClient()), "InvalidApiKey");

        assertThrows(NewsApiException.class,()->invalidApiNewsFetcher.getNewsList(uriPath,uriParams),
                "Should throw NewsApiException for invalid API key"
        );
    }
}