package community.whatever.onembackendjava.repository;

import java.util.Optional;

public interface URLShortenRepository {

	Optional<String> findByShortenedURL(String key);

	String create(String originURL, String shortenUrl);

}
