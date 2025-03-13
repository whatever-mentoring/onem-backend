package community.whatever.onembackendjava.api.service;

import community.whatever.onembackendjava.api.domain.BlockedDomainInterface;
import community.whatever.onembackendjava.common.error.BusinessExceptionGenerator;
import community.whatever.onembackendjava.common.error.model.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


@Slf4j
@Service
public class CsvReaderService implements BlockedDomainInterface {

    private final Set<String> blockedDomains = new HashSet<>();

    // CSV 파일을 읽어 BlockedDomains 리스트에 저장
    public void loadBlockedDomains(MultipartFile file) {
        if (file.isEmpty()) throw BusinessExceptionGenerator.createBusinessException(ErrorCode.DB001);

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                blockedDomains.add(line.trim());
            }
        } catch (Exception e) {
            throw BusinessExceptionGenerator.createBusinessException("RU001", e.getMessage());
        }
    }

    // 차단된 도메인 목록 반환
    public Set<String> getBlockedDomains() {
        return blockedDomains;
    }

    // URL이 차단된 도메인에 포함되는지 확인
    public boolean isBlocked(String url) {
        for (String domain : blockedDomains) {
            if (url.contains(domain)) {
                return true;
            }
        }
        return false;
    }

}
