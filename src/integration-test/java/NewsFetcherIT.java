import com.majkel.emotinews.adapter.HttpClientWrapper;
import com.majkel.emotinews.config.ConfigLoader;
import com.majkel.emotinews.service.NewsFetcher;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.time.LocalDate;
import java.util.List;

import com.majkel.emotinews.model.NewsArticle;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
public class NewsFetcherIT {

    @Test
    public void fetchingNewsIntegrationTest(){//testing fetching news with the specific topic - query value
        String query="Trump".toLowerCase(); //Trump, chose him because news with him are easy to find and also often have his name in title
        String apiKey=ConfigLoader.getValue("api.news.key");

        Assumptions.assumeTrue(apiKey!=null || !apiKey.isBlank(), "API key not configured, skipping integration test");

        NewsFetcher newsFetcher=new NewsFetcher(new HttpClientWrapper(HttpClient.newHttpClient()), apiKey);

        List<NewsArticle>news=newsFetcher.getNewsList("everything?q=" + query + "&from=" + LocalDate.now().minusDays(2) + "&sortBy=popularity");

        assertNotNull(news, "News list should not be null");
        assertFalse(news.isEmpty(), "Expected at least one article from NewsAPI");

        boolean anyContainsQuery=news.stream().anyMatch(article->article.getTitle().toLowerCase().contains(query));//CollectionUtils.filterValidNews() is used to filter null title news soo i don't check it here

        assertTrue(anyContainsQuery,"Expected at least one article with title containing: " + query);

    }
}