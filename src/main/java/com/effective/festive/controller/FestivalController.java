package com.effective.festive.controller;

import com.effective.festive.model.Festival;
import com.effective.festive.service.FestivalService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/festivals")
public class FestivalController {
    private final FestivalService service;

    public FestivalController(FestivalService service) {
        this.service = service;
    }

    // 날짜 빠른 기준 축제 조회 API
    @GetMapping("/upcoming")
    public ResponseEntity<List<Festival>> upcoming() throws Exception {
        return ResponseEntity.ok(service.getUpcomingFestivals());
    }

    // 최근 본 축제 조회 API (쿠키 사용)
    @GetMapping("/recent")
    public ResponseEntity<List<Festival>> recent(
            @CookieValue(name = "recentFestivals", defaultValue = "") String recentCookie) throws Exception {
        if (recentCookie.isEmpty()) {
            //쿠키가 없으묜 빈 리스트 응답
            return ResponseEntity.ok(Collections.emptyList());
        }
        //"id1,id2,id3" 형태의 문자열을 ,로 분리하여 List<String> 생성
        List<String> ids = Arrays.asList(recentCookie.split(","));
        //해당 ID리스트에 속하는 축제들만 조회
        return ResponseEntity.ok(service.getFestivalById(ids));
    }

    //축제 상세 조회하면서 쿠키에 기록하기
    @GetMapping("/{id}")
    public ResponseEntity<Festival> detail(
            @PathVariable String id,
            @CookieValue(name = "recentFestivals", defaultValue = "") String recentCookie, HttpServletResponse response) throws Exception {
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
    }
}
