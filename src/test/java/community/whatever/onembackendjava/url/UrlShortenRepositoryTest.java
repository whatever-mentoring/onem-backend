package community.whatever.onembackendjava.url;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class UrlShortenRepositoryTest {

    static UrlShortenRepository urlShortenRepository ;

    @BeforeAll
    public static void setUp(){
        urlShortenRepository = new UrlShortenRepository() ;
        // 테스트용
        urlShortenRepository.testInsertValue("1234" , "https://docs.oracle.com");
        urlShortenRepository.createKey("https://www.daum.net");
    }



    @Test
    @DisplayName("키 생성")
    public void createKey(){
        String createKey = urlShortenRepository.createKey("https://www.naver.com");
        //System.out.println("createKey = " + createKey);
        assertThat(createKey).isNotEmpty() ;
    }

    @Test
    @DisplayName("url 검색")
    public void searchUrl(){
        String result = urlShortenRepository.getUrl("1234");
        assertThat(result).isNotEmpty() ;
    }

}
