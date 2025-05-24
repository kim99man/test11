package com.effective.festive.service;

import com.effective.festive.model.Festival;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FestivalService {
    @Value("${festive.api.url}")
    private String apiUrl;

    private final RestTemplate rt = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    //공공  데이터에서 전체 축제 목록 가져옴
    public List<Festival> getAllFestivals() throws Exception {
        String json = rt.getForObject(apiUrl, String.class);
        JsonNode items = mapper.readTree(json)
                .path("response")
                .path("body")
                .path("items")
                .path("item");
        List<Festival> list = new ArrayList<>();
        for (JsonNode node : items) {
            list.add(mapper.treeToValue(node, Festival.class));
        }
        return list;
    }

    //날짜 기준 오름차순 조회
    public List<Festival> getUpcomingFestivals() throws Exception {
        return getAllFestivals()
                .stream().sorted(Comparator.comparing(Festival::getStartDate))
                .collect(Collectors.toList());
    }

    // ID 리스트로 개별 축제 조회 (ID는 축제 고유번호(seq))
    public List<Festival> getFestivalById(List<String> ids) throws Exception {
        return getAllFestivals()
                .stream()
                .filter(f -> ids.contains(f.getSeq()))
                .collect(Collectors.toList());
    }

    //단일 축제 상세
    public Festival getFestivalById(String id) throws Exception {
        return getAllFestivals()
                .stream()
                .filter(f -> f.getSeq().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("축제를 찾을 수 없습니다"));
    }

}
