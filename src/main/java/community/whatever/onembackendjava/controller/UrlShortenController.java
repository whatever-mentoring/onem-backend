package community.whatever.onembackendjava.controller;

import community.whatever.onembackendjava.service.UrlShortenService;
import community.whatever.onembackendjava.dto.CreateShortenUrlRequest;
import community.whatever.onembackendjava.dto.CreateShortenUrlResponse;
import community.whatever.onembackendjava.dto.SearchShortenUrlRequest;
import community.whatever.onembackendjava.dto.SearchShortenUrlResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UrlShortenController {

    private final UrlShortenService urlShortenService;

    @Operation(summary = "단축 URL로 원본 URL 찾기")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "URL 찾기 성공"),
        @ApiResponse(responseCode = "404", description = "URL을 찾을 수 없음", 
                    content = @Content(schema = @Schema(implementation = Object.class)))
    })
    @PostMapping("/shorten-url/search")
    public ResponseEntity<SearchShortenUrlResponse> shortenUrlSearch(@RequestBody SearchShortenUrlRequest request) {
        return ResponseEntity.ok(urlShortenService.searchShortenUrl(request));
    }

    @Operation(summary = "새로운 단축 URL 생성")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "URL 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 URL 형식", 
                    content = @Content(schema = @Schema(implementation = Object.class))),
        @ApiResponse(responseCode = "403", description = "차단된 도메인", 
                    content = @Content(schema = @Schema(implementation = Object.class)))
    })
    @PostMapping("/shorten-url/create")
    public ResponseEntity<CreateShortenUrlResponse> shortenUrlCreate(@Valid @RequestBody CreateShortenUrlRequest request) {
        return ResponseEntity.ok(urlShortenService.createShortenUrl(request));
    }
    
    @Operation(summary = "단축 URL로 원본 URL로 리다이렉트")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "302", description = "리다이렉트 성공"),
        @ApiResponse(responseCode = "404", description = "URL을 찾을 수 없음", 
                    content = @Content(schema = @Schema(implementation = Object.class)))
    })
    @GetMapping("/{code}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String code) {
        String originalUrl = urlShortenService.getOriginalUrl(code);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header("Location", originalUrl)
                .build();
    }
}
