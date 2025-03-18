package community.whatever.onembackendjava.api.service;

import community.whatever.onembackendjava.api.domain.BlockedDomainInterface;
import community.whatever.onembackendjava.api.domain.ExpiringUrl;
import community.whatever.onembackendjava.api.dto.ShortenUrlDto;
import community.whatever.onembackendjava.common.error.BusinessExceptionGenerator;
import community.whatever.onembackendjava.common.error.model.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlShortenService {

    private final BlockedDomainInterface blockedDomainInterface;
    private static final String UrlRegex = "https?://(?:www\\.)?[a-zA-Z0-9./]+";
    private static final Pattern URL_PATTERN = Pattern.compile(UrlRegex);

    private final Map<String, ExpiringUrl> shortenUrls = new HashMap<>();

    public ShortenUrlDto.Get.Response shortenUrlSearch(ShortenUrlDto.Get.Request param){
        if (!shortenUrls.containsKey(param.getKey())) {
            throw BusinessExceptionGenerator.createBusinessException(ErrorCode.DB003);
        }

        return ShortenUrlDto.Get.Response.builder()
                .originUrl(getOriginalUrl(param.getKey()))
                .build();
    }

    public ShortenUrlDto.Create.Response shortenUrlCreate(ShortenUrlDto.Create.Request param){
        if (validateOriginUrl(param.getOriginUrl())) {
            throw BusinessExceptionGenerator.createBusinessException(ErrorCode.DB001);
        }
        if(blockedDomainInterface.isBlocked(param.getOriginUrl())){
            throw BusinessExceptionGenerator.createBusinessException(ErrorCode.DB004);
        }

        Random random = new Random();
        String returnKey = String.valueOf(random.nextInt(10000000));

        while (shortenUrls.containsKey(returnKey)) { // 이미 존재하는 키면 다시 생성
            returnKey = String.valueOf(random.nextInt(10000000));
        }

        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(param.getExpirationMin());
        shortenUrls.put(returnKey, new ExpiringUrl(param.getOriginUrl(), expiryTime)); // 새로운 키를 저장

        return ShortenUrlDto.Create.Response.builder()
                .key(returnKey)
                .build();
    }

    public static boolean isValidURL(String url) {
        Matcher matcher = URL_PATTERN.matcher(url);
        return matcher.matches();
    }

    private boolean validateOriginUrl(String originUrl) {
        return !StringUtils.hasText(originUrl) || !isValidURL(originUrl);
    }

    /*
     *  30분마다 만료된 URL 삭제
     */
    @Scheduled(fixedRate = 1_800_000)
    private void startCleanupTask() {
        LocalDateTime now = LocalDateTime.now();
        shortenUrls.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    public String getOriginalUrl(String shortUrl) {
        ExpiringUrl expiringUrl = shortenUrls.get(shortUrl);
        if (expiringUrl == null ) {
            shortenUrls.remove(shortUrl);
            throw BusinessExceptionGenerator.createBusinessException(ErrorCode.DB002);
        }
        if (expiringUrl.isExpired()){
            shortenUrls.remove(shortUrl);
            throw BusinessExceptionGenerator.createBusinessException(ErrorCode.DB002);
        }
        return expiringUrl.getOriginalUrl();
    }

}
