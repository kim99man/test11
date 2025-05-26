package com.effective.festive.controller;

import com.effective.festive.model.Festival;
import com.effective.festive.service.FestivalService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/festivals")
@CrossOrigin(origins = "*") // CORS 허용
public class FestivalController {
    private final FestivalService service;

    public FestivalController(FestivalService service) {
        this.service = service;
    }

    // 전체 축제 조회 API
    @GetMapping
<<<<<<< HEAD
    public ResponseEntity<List<Festival>> getAllFestivals() {
        try {
            List<Festival> festivals = service.getAllFestivals();
            return ResponseEntity.ok(festivals);
        } catch (Exception e) {
            throw new RuntimeException("축제 목록을 조회하는 중 오류가 발생했습니다.", e);
        }
=======
    public ResponseEntity<List<Festival>> getAllFestivals() throws Exception {
        return ResponseEntity.ok(service.getAllFestivals());
>>>>>>> dbff002 (csv 수정3)
    }

    // 날짜 빠른 기준 축제 조회 API
    @GetMapping("/upcoming")
    public ResponseEntity<List<Festival>> upcoming() {
        try {
            List<Festival> festivals = service.getUpcomingFestivals();
            return ResponseEntity.ok(festivals);
        } catch (Exception e) {
            throw new RuntimeException("예정된 축제 목록을 조회하는 중 오류가 발생했습니다.", e);
        }
    }

    // 구군별 축제 조회 API
    @GetMapping("/district/{district}")
    public ResponseEntity<List<Festival>> getByDistrict(@PathVariable String district) throws Exception {
        return ResponseEntity.ok(service.getFestivalsByDistrict(district));
    }

    // 축제명으로 검색 API
    @GetMapping("/search")
    public ResponseEntity<List<Festival>> searchByTitle(@RequestParam String keyword) throws Exception {
        return ResponseEntity.ok(service.searchFestivalsByTitle(keyword));
    }

    // 최근 본 축제 조회 API (쿠키 사용)
    @GetMapping("/recent")
    public ResponseEntity<List<Festival>> recent(
            @CookieValue(name = "recentFestivals", defaultValue = "") String recentCookie) {
        try {
            if (recentCookie.isEmpty()) {
                //쿠키가 없으면 빈 리스트 응답
                return ResponseEntity.ok(Collections.emptyList());
            }
            //"id1,id2,id3" 형태의 문자열을 ,로 분리하여 List<String> 생성
            List<String> ids = Arrays.asList(recentCookie.split(","));
            //해당 ID리스트에 속하는 축제들만 조회
            List<Festival> festivals = service.getFestivalById(ids);
            return ResponseEntity.ok(festivals);
        } catch (Exception e) {
            throw new RuntimeException("최근 본 축제 목록을 조회하는 중 오류가 발생했습니다.", e);
        }
    }

    //축제 상세 조회하면서 쿠키에 기록하기
    @GetMapping("/{id}")
    public ResponseEntity<Festival> detail(
            @PathVariable String id,
            @CookieValue(name = "recentFestivals", defaultValue = "") String recentCookie, 
            HttpServletResponse response) {
        try {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("축제 ID가 올바르지 않습니다.");
            }

            Festival festival = service.getFestivalById(id);

            //쿠키값 업데이트 (가장 최근 조회가 맨 앞, 최대 5개 유지)
            LinkedList<String> recentList = new LinkedList<>(
                    recentCookie.isEmpty() ? Collections.emptyList() : Arrays.asList(recentCookie.split(","))
            );
            recentList.remove(id);
            recentList.addFirst(id);
            if (recentList.size() > 5) recentList.removeLast();

            Cookie cookie = new Cookie("recentFestivals", String.join(",", recentList));
            cookie.setPath("/"); //전체 경로에서 접근 가능
            cookie.setMaxAge(60 * 60 * 24 * 7); //7일간 유지
            response.addCookie(cookie); // 응답 헤더에 set-cookie 추가

            return ResponseEntity.ok(festival);
        } catch (NoSuchElementException e) {
            // GlobalExceptionHandler에서 처리되도록 다시 던짐
            throw e;
        } catch (IllegalArgumentException e) {
            // GlobalExceptionHandler에서 처리되도록 다시 던짐  
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("축제 상세 정보를 조회하는 중 오류가 발생했습니다.", e);
        }
    }

    // 검색 API 추가
    @GetMapping("/search")
    public ResponseEntity<List<Festival>> search(@RequestParam String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                throw new IllegalArgumentException("검색 키워드가 필요합니다.");
            }
            
            List<Festival> festivals = service.searchFestivals(keyword.trim());
            return ResponseEntity.ok(festivals);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("축제 검색 중 오류가 발생했습니다.", e);
        }
    }

    // 설정 정보 조회 API (디버깅용)
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getConfig() {
        try {
            Map<String, Object> config = new HashMap<>();
            config.put("csvFilePath", service.getCsvFilePath());
            config.put("cacheInfo", service.getCacheInfo());
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            throw new RuntimeException("설정 정보를 조회하는 중 오류가 발생했습니다.", e);
        }
    }
}
