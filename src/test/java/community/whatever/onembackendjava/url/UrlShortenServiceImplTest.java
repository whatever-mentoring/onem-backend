package community.whatever.onembackendjava.url;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class UrlShortenServiceImplTest {

    private static UrlShortenRepository urlShortenRepository ;
    private static UrlShortenService urlShortenService ;

    private static String tmpString ;
    @BeforeAll
    public static void setUp(){
        urlShortenService = new UrlShortenServiceImpl(new UrlShortenRepository() ) ;
    }

    @Test
    @DisplayName("키 생성")
    void keyCreate() {
        String createKey = urlShortenService.keyCreate("https://www.naver.com");
        assertThat(createKey).isNotEmpty() ;
    }

}