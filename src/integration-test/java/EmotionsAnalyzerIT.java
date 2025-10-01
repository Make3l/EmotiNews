import com.majkel.emotinews.adapter.HttpClientWrapper;
import com.majkel.emotinews.config.ConfigLoader;
import com.majkel.emotinews.exception.ParsingNewsApiException;
import com.majkel.emotinews.model.TextEmotion;
import com.majkel.emotinews.service.EmotionsAnalyzer;
import org.junit.jupiter.api.*;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
@DisplayName("EmotionsAnalyzer integration tests")
public class EmotionsAnalyzerIT {
    private static String apiKey;
    private EmotionsAnalyzer emotionsAnalyzer;

    @BeforeAll
    public static void checkPrerequisites(){
        apiKey=ConfigLoader.getValue("api.huggingface.emotions.analizer");
        Assumptions.assumeTrue(apiKey!=null && !apiKey.isBlank(), "API key not configured, skipping integration tests");
    }

    @BeforeEach
    public void setUp(){
        emotionsAnalyzer=new EmotionsAnalyzer(new HttpClientWrapper(HttpClient.newHttpClient()),apiKey);
    }

private void assertValidEmotionStructure(List<TextEmotion>emotions){
    emotions.forEach(emotion->{
        assertAll("Emotion structure",
                ()->assertNotNull(emotion.getLabel(),"Label must not be null"),
                ()->assertTrue(emotion.getLabel().matches("LABEL_[0-2]"),"Label should be LABEL_0, LABEL_1 or LABEL_2 "),
                ()->assertTrue(emotion.getConfidence()>=0.0 && emotion.getConfidence()<=1.0,"Confidence should be in between 0 and 1")
        );
    });
}

    @Test
    @Timeout(value = 35,unit= TimeUnit.SECONDS)
    @DisplayName("Should analyze emotions for basic text samples")
    public void analyzeBasicTexts() throws Exception{
        List<String> textToAnalyze=new ArrayList<>(Arrays.asList("I love java","I really hate fixing errors at 4 am"));

        List<TextEmotion>result=emotionsAnalyzer.parseArticles(textToAnalyze);

        assertNotNull(result);
        assertEquals(2,result.size(),"Should return emotion for each input text");

        assertValidEmotionStructure(result);
    }

    @Test
    @Timeout(value = 45,unit= TimeUnit.SECONDS)
    @DisplayName("Should analyze emotions for larger batch of texts")
    public void analyzeLargerBatch() throws Exception{

        List<String> textToAnalyze = new ArrayList<>(Arrays.asList(
                "Donald Trump visits New York",
                "Stock market crashes after announcement",
                "Technology companies unveil new AI tools",
                "SpaceX launches another satellite into orbit",
                "Climate change protests spread worldwide",
                "New vaccine shows promising results",
                "Football team wins championship after penalty shootout",
                "Government announces new tax reforms",
                "Famous actor stars in upcoming movie",
                "Oil prices rise amid global tensions",
                "Scientists discover potential new exoplanet",
                "Major earthquake hits coastal region",
                "Startup secures record funding round",
                "Cybersecurity breach affects millions of users",
                "Musician releases chart-topping album"
        ));

        List<TextEmotion>result=emotionsAnalyzer.parseArticles(textToAnalyze);

        assertNotNull(result);
        assertEquals(15,result.size(),"Should return emotion for each input text");

        assertValidEmotionStructure(result);
    }

    @Test
    @Timeout(value = 35,unit= TimeUnit.SECONDS)
    @DisplayName("Should analyze emotions for longer paragraph-length texts")
    public void analyzeLongerTexts() throws Exception{

        List<String> textToAnalyze = new ArrayList<>(Arrays.asList(
                "Global stock markets experienced significant turbulence today after unexpected economic data was released, causing major indexes to drop sharply and investors to worry about a potential recession.",
                "Several leading technology companies announced the launch of advanced artificial intelligence products this week, emphasizing their potential to revolutionize industries ranging from healthcare to finance.",
                "SpaceX successfully launched another batch of satellites into low-Earth orbit, marking a major step in expanding global internet coverage and reinforcing the company’s leadership in private space exploration.",
                "In response to increasing concerns about climate change, thousands of people across multiple countries took to the streets in organized protests, demanding stronger policies and immediate government action."
        ));

        List<TextEmotion>result=emotionsAnalyzer.parseArticles(textToAnalyze);

        assertNotNull(result);
        assertEquals(4,result.size(),"Should return emotion for each input text");

        assertValidEmotionStructure(result);
    }

    @Test
    @Timeout(value = 35,unit= TimeUnit.SECONDS)
    @DisplayName("Should handle special characters and Unicode in texts")
    public void analyzeTextsWithSpecialCharacters() throws Exception{

        List<String> textToAnalyze = new ArrayList<>(Arrays.asList(
                "Amazing product! 😊👍",
                "Café owner's 'revolutionary' naïve approach",
                "Price: $100 & €50 — what a deal!",
                "Quote: \"He said 'yes' to the proposal\""
        ));

        List<TextEmotion>result=emotionsAnalyzer.parseArticles(textToAnalyze);

        assertNotNull(result);
        assertEquals(4,result.size(),"Should return emotion for each input text");

        assertValidEmotionStructure(result);
    }

    @Test
    @DisplayName("Should throw ParsingNewsApiException with invalid API key")
    public void invalidApiKey(){
        List<String>textToAnalyze=new ArrayList<>(Arrays.asList(
                "Parse testing",
                "Hope it will work :D"
        ));
        
        EmotionsAnalyzer invalidApiEmotionAnalyzer=new EmotionsAnalyzer(new HttpClientWrapper(HttpClient.newHttpClient()),"InvalidApiKey");
        
        assertThrows(ParsingNewsApiException.class,
                ()->invalidApiEmotionAnalyzer.parseArticles(textToAnalyze),
                "Should throw ParsingNewsApiException for invalid API key");
    }
}
