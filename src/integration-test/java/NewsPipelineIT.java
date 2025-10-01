import com.majkel.emotinews.adapter.HttpClientWrapper;
import com.majkel.emotinews.config.ConfigLoader;
import com.majkel.emotinews.model.NewsWithEmotions;
import com.majkel.emotinews.model.TextEmotion;
import com.majkel.emotinews.service.EmotionsAnalyzer;
import com.majkel.emotinews.service.NewsFetcher;
import com.majkel.emotinews.service.NewsPipeline;
import org.junit.jupiter.api.*;

import java.net.http.HttpClient;
import java.net.http.HttpTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
@DisplayName("NewsPipeline integration tests")
public class NewsPipelineIT {//added only one test because most of the testing cases are covered in NewsPipelineTest(this is unit test) or in other integration tests - NewsFetcherIt or EmotionsAnalyzerIt
    private NewsPipeline newsPipeline;
    private EmotionsAnalyzer stubbedEmotionsAnalyzer;
    private NewsFetcher newsFetcher;
    private static String newsFetcherApiKey;

    @BeforeAll
    public static void checkPrerequisites(){
        newsFetcherApiKey= ConfigLoader.getValue("api.news.key");
        Assumptions.assumeTrue(newsFetcherApiKey!=null && !newsFetcherApiKey.isBlank(), "API key not configured, skipping integration tests");
    }

    @BeforeEach
    public void setUp(){
        stubbedEmotionsAnalyzer=new EmotionsAnalyzer(new HttpClientWrapper(HttpClient.newHttpClient()),"STUB_KEY"){
            @Override
            public List<TextEmotion> parseArticles(List<String> news) throws HttpTimeoutException {
                List<TextEmotion>result=new ArrayList<>();
                news.forEach(description->result.add(new TextEmotion("LABEL_0",0.9)));
                return result;
            }
        };

        newsFetcher=new NewsFetcher(new HttpClientWrapper(HttpClient.newHttpClient()),newsFetcherApiKey);

        newsPipeline=new NewsPipeline(newsFetcher,stubbedEmotionsAnalyzer);
    }

    @Test
    @DisplayName("Pipeline should fetch articles and map them to emotions (real fetcher + stub analyzer)")
    @Timeout(value = 35, unit = TimeUnit.SECONDS)
    public void pipelineShouldMapFetchedArticlesToEmotions() {
        List<NewsWithEmotions>pipelineResult=newsPipeline.loadNews();


        assertNotNull(pipelineResult, "Pipeline result should not be null");
        assertFalse(pipelineResult.isEmpty(), "Pipeline should return at least one NewsWithEmotions");
        assertTrue(pipelineResult.size() <= 20, "Pipeline must truncate to MAX_NUMBER_OF_FETCHED_ARTICLES (<=20)");

        pipelineResult.forEach(newsWithEmotions -> {
            assertAll("Validate NewsWithEmotions element",
                    () -> assertNotNull(newsWithEmotions, "NewsWithEmotions object should not be null"),
                    () -> assertNotNull(newsWithEmotions.getEmotion(), "Emotion should not be null"),
                    () -> assertTrue(
                            newsWithEmotions.getEmotion().matches("LABEL_[0-2]"),
                            "Emotion label should match pattern LABEL_[0-2]"
                    ),
                    () -> assertNotNull(newsWithEmotions.getArticle(), "Article should not be null"),
                    () -> assertNotNull(newsWithEmotions.getArticle().getTitle(), "Article title should not be null"),
                    () -> assertFalse(
                            newsWithEmotions.getArticle().getTitle().isBlank(),
                            "Article title should not be blank"
                    )
            );
        });
    }
}
