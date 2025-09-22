import com.google.gson.JsonSyntaxException;
import com.majkel.emotinews.model.NewsArticle;
import com.majkel.emotinews.model.NewsWithEmotions;
import com.majkel.emotinews.storage.JSONStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class JSONStorageTest {
    private final String storageSamplePath="src/test/resources/storage-sample-test.json";
    private final String incorrectStoragePath="src/test/resources/incorrect-storage-sample-test.json" ;//this json have incorrect syntax
    private final String pathToEmptyStorage="src/test/resources/empty-storage-sample-test.json";

    @TempDir
    File tempDir;

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

    @Test
    public void safeSaveJSONSampleTest() throws Exception{
        File file=new File(tempDir,"test.json");

        List<NewsWithEmotions>news=new ArrayList<>(Arrays.asList(
                new NewsWithEmotions("Positive",new NewsArticle("Title0", "Description0","Url0"))));
        JSONStorage.safeSave(file,news);

        String result= Files.readString(file.toPath());

        assertTrue(result.contains("Positive"));
        assertTrue(result.contains("Description0"));
    }

    @Test
    public void safeSaveNullNewsListTest(){
        File file=new File(tempDir,"test.json");

        JSONStorage.safeSave(file,null);

        assertEquals(0,file.length());
    }

    @Test
    public void saveInvalidJSONPathTest(){
        List<NewsWithEmotions>news=new ArrayList<>(Arrays.asList(
                new NewsWithEmotions("Positive",new NewsArticle("Title0", "Description0","Url0"))));
        assertThrows(IOException.class,()->JSONStorage.save(new File("wrong:/wrong/path.json"),news));
    }

    @Test
    public void integrationSafeLoadAndSafeSaveTest(){
        File file=new File(tempDir,"test.json");

        List<NewsWithEmotions>news=new ArrayList<>();
        for(int i=0;i<5;i++){
            news.add(new NewsWithEmotions("Positive",new NewsArticle("Title "+i,"Description "+i,"Url "+i)));
        }
        JSONStorage.safeSave(file,news);

        List<NewsWithEmotions>loadedNews=JSONStorage.safeLoad(file);

        assertEquals(news,loadedNews);
    }

}
