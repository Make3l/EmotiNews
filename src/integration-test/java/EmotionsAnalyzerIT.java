import com.majkel.emotinews.adapter.HttpClientWrapper;
import com.majkel.emotinews.config.ConfigLoader;
import com.majkel.emotinews.model.TextEmotion;
import com.majkel.emotinews.service.EmotionsAnalyzer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("integration")
public class EmotionsAnalyzerIT {

    @Test
    public void analyzingEmotionsIntegrationTest() throws Exception{
        List<String> textToAnalyze=new ArrayList<>(Arrays.asList("I love java","I really hate fixing errors at 4 am"));

        EmotionsAnalyzer emotionsAnalyzer=new EmotionsAnalyzer(new HttpClientWrapper(HttpClient.newHttpClient()), ConfigLoader.getValue("api.huggingface.emotions.analizer"));
        List<TextEmotion>result=emotionsAnalyzer.parseArticles(textToAnalyze);

        assertNotNull(result);
        assertEquals(2,result.size());
        assertEquals("LABEL_2",result.get(0).getLabel());
        assertEquals("LABEL_0",result.get(1).getLabel());
    }
}
