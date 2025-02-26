package community.whatever.onembackendjava.repository;

import java.util.Optional;

public interface URLShortenRepository {

	public Optional<String> findByShortenedURL(String key);

	public String create(String originURL, String shortenUrl);

}
