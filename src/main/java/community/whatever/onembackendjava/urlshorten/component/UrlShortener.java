package community.whatever.onembackendjava.urlshorten.component;

import java.util.Random;

public class UrlShortener {

    private static final Random RANDOM = new Random();

    public static String shorten() {
        return String.valueOf(RANDOM.nextInt(10000));
    }

}
