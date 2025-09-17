import com.majkel.emotinews.model.NewsArticle;
import com.majkel.emotinews.model.NewsWithEmotions;
import com.majkel.emotinews.model.TextEmotion;
import com.majkel.emotinews.utils.CollectionUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CollectionUtilsTest {
    @Test
    public void toStringListTest(){
        List<NewsArticle> articles=new ArrayList<>();
        for(int i=0;i<4;i++)
            articles.add(new NewsArticle("Title "+i,"Description" +i,"Url "+i));

        List<String>result= CollectionUtils.toStringList(articles);

        assertNotNull(result);
        assertEquals(4,result.size());
        assertEquals(articles.get(2).getDescription().trim(),result.get(2));
    }

    @Test
    public void toStringListNullTest(){
        assertTrue(CollectionUtils.toStringList(null).isEmpty());
        assertTrue(CollectionUtils.toStringList(new ArrayList<>()).isEmpty());
    }

    @Test
    public void toNewsWithEmotionsListTest(){
        List<NewsArticle> articles=new ArrayList<>();
        for(int i=0;i<4;i++)
            articles.add(new NewsArticle("Title "+i,"Description" +i,"Url "+i));
        List<TextEmotion> emotions=new ArrayList<>();
        for(int i=0;i<4;i++)
            emotions.add(new TextEmotion("Label "+i,0.9));

        List<NewsWithEmotions>result=CollectionUtils.toNewsWithEmotionsList(articles,emotions);

        assertNotNull(result);
        assertEquals(4,result.size());
        assertEquals(new NewsWithEmotions(emotions.get(2).getLabel(),articles.get(2)),result.get(2));
    }

    @Test
    public void toNewsWithEmotionsListThrowsExceptionTest() {//different list sizes
        List<NewsArticle> articles = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            articles.add(new NewsArticle("Title " + i, "Description" + i, "Url " + i));
        List<TextEmotion> emotions = new ArrayList<>();
        for (int i = 0; i < 2; i++)
            emotions.add(new TextEmotion("Label " + i, 0.9));

        assertThrows(IllegalArgumentException.class,()->CollectionUtils.toNewsWithEmotionsList(articles,emotions));
    }

    @Test
    public void toNewsWithEmotionsListEmptyTest(){
        List<NewsWithEmotions>result=CollectionUtils.toNewsWithEmotionsList(new ArrayList<>(),new ArrayList<>());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void toNewsWithEmotionsListNullTest() {
        assertThrows(IllegalArgumentException.class,()->CollectionUtils.toNewsWithEmotionsList(null,new ArrayList<>()));
        assertThrows(IllegalArgumentException.class,()->CollectionUtils.toNewsWithEmotionsList(new ArrayList<>(),null));
    }

    @Test
    public void filterValidNewsTest(){
        List<NewsArticle> articlesToFilter=new ArrayList<>();
        for(int i=0;i<20;i++)
            articlesToFilter.add(new NewsArticle("Title " + i, "Description " + i, "Url " + i));

        for(int i=0;i<20;i++)//null title
            articlesToFilter.add(new NewsArticle(null, "Description " + i, "Url " + i));

        for(int i=0;i<3;i++)//null description
            articlesToFilter.add(new NewsArticle("Title " + i, null, "Url " + i));

        for(int i=0;i<3;i++)//null url
            articlesToFilter.add(new NewsArticle("Title " + i, "Description " + i, null));

        for(int i=0;i<3;i++)//everything null
            articlesToFilter.add(new NewsArticle());

        List<NewsArticle>result=CollectionUtils.filterValidNews(articlesToFilter);
        assertEquals(20,result.size());
        assertIterableEquals(articlesToFilter.subList(0,result.size()),result);
    }

    @Test
    public void filterValidNewsNullTest(){
        assertThrows(IllegalArgumentException.class,()->CollectionUtils.filterValidNews(null));
    }
}
