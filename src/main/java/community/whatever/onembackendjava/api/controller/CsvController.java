package community.whatever.onembackendjava.api.controller;

import community.whatever.onembackendjava.api.service.CsvReaderService;
import community.whatever.onembackendjava.common.util.ResponseFormatter;
import community.whatever.onembackendjava.common.util.model.ResultJson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class CsvController {

    private final CsvReaderService csvReaderService;


    // CSV 파일을 업로드하여 차단 도메인 목록 갱신
    @PostMapping("/block-domains/upload")
    public ResponseEntity<ResultJson> uploadCsv(@RequestPart(name = "blockDomains") MultipartFile file) {
        log.info("file >> {}", file);
        csvReaderService.loadBlockedDomains(file);
        return ResponseFormatter.ConvertResponse();
    }

}
