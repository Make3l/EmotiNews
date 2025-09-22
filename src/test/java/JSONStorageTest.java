import com.google.gson.JsonSyntaxException;
import com.majkel.emotinews.model.NewsWithEmotions;
import com.majkel.emotinews.storage.JSONStorage;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class JSONStorageTest {
    private final String storageSamplePath="src/test/resources/storage-sample-test.json";
    private final String incorrectStoragePath="src/test/resources/incorrect-storage-sample-test.json" ;//this json have incorrect syntax
    private final String pathToEmptyStorage="src/test/resources/empty-storage-sample-test.json";

    @Test
    public void safeLoadJSONSampleTest(){
        List<NewsWithEmotions> news= JSONStorage.safeLoad(new File(storageSamplePath));
        assertEquals(2,news.size());
        assertEquals("Author 2",news.get(1).getArticle().getAuthor());
    }

    @Test
    public void loadInvalidJSONTest(){
        assertThrows(JsonSyntaxException.class,()->JSONStorage.load(new File(incorrectStoragePath)));
    }

    @Test
    public void safeLoadInvalidJSONTest(){
        List<NewsWithEmotions> news= JSONStorage.safeLoad(new File(incorrectStoragePath));
        assertNotNull(news);
        assertTrue(news.isEmpty());
    }

    @Test
    public void loadInvalidJSONPathTest(){
        assertThrows(IOException.class,()->JSONStorage.load(new File("wrongPath")));
    }

    @Test
    public void safeLoadInvalidJSONPathTest(){
        List<NewsWithEmotions> news= JSONStorage.safeLoad(new File("wrongPath"));
        assertNotNull(news);
        assertTrue(news.isEmpty());
    }
    @Test
    public void safeLoadEmptyJSONTest(){
        List<NewsWithEmotions> news= JSONStorage.safeLoad(new File(pathToEmptyStorage));
        assertNotNull(news);
        assertTrue(news.isEmpty());
    }


}
