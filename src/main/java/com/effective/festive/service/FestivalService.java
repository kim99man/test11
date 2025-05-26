package com.effective.festive.service;

import com.effective.festive.config.FestivalConfig;
import com.effective.festive.model.Festival;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FestivalService {
    
    private final FestivalConfig festivalConfig;
    
    // 메모리 캐시 - 성능 향상을 위해
    private List<Festival> cachedFestivals = null;
    private final Object cacheLock = new Object();
    private long lastCacheUpdate = 0;

    @Autowired
    public FestivalService(FestivalConfig festivalConfig) {
        this.festivalConfig = festivalConfig;
    }

    //CSV 파일에서 전체 축제 목록 가져옴 (캐싱 적용)
    public List<Festival> getAllFestivals() throws Exception {
        // 캐시 갱신 여부 확인
        boolean shouldRefreshCache = false;
        if (festivalConfig.getCache().isEnabled()) {
            long currentTime = System.currentTimeMillis();
            shouldRefreshCache = cachedFestivals == null || 
                (currentTime - lastCacheUpdate) > festivalConfig.getCache().getRefreshInterval();
        }
        
        if (cachedFestivals != null && !shouldRefreshCache) {
            return new ArrayList<>(cachedFestivals); // 복사본 반환으로 안전성 확보
        }
        
        synchronized (cacheLock) {
            if (cachedFestivals != null && !shouldRefreshCache) {
                return new ArrayList<>(cachedFestivals);
            }
            
            List<Festival> festivals = new ArrayList<>();
            
            try {
                String csvFilePath = festivalConfig.getCsv().getFilePath();
                String encoding = festivalConfig.getCsv().getEncoding();
                
                ClassPathResource resource = new ClassPathResource(csvFilePath);
                if (!resource.exists()) {
                    throw new Exception("CSV 파일을 찾을 수 없습니다: " + csvFilePath);
                }
                
                Charset charset = Charset.forName(encoding);
                CSVParser parser = CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .withTrim()  // 헤더와 값의 공백 제거
                        .parse(new InputStreamReader(resource.getInputStream(), charset));
                
                for (CSVRecord record : parser) {
                    try {
                        Festival festival = createFestivalFromRecord(record);
                        if (festival != null) {
                            festivals.add(festival);
                        }
                    } catch (Exception e) {
                        // 개별 레코드 파싱 실패는 로그만 남기고 계속 진행
                        System.err.println("레코드 파싱 실패: " + e.getMessage());
                    }
                }
                
                parser.close();
                
                if (festivalConfig.getCache().isEnabled()) {
                    cachedFestivals = festivals; // 캐시에 저장
                    lastCacheUpdate = System.currentTimeMillis();
                }
                
            } catch (IOException e) {
                throw new Exception("CSV 파일을 읽는 중 오류가 발생했습니다: " + e.getMessage(), e);
            }
            
            return new ArrayList<>(festivals);
        }
    }
    
    // CSV 레코드에서 Festival 객체 생성
    private Festival createFestivalFromRecord(CSVRecord record) {
        Festival festival = new Festival();
        
        // 필수 필드 검증
        String id = getColumnValue(record, "콘텐츠ID");
        if (id == null || id.trim().isEmpty()) {
            return null; // ID가 없으면 무효한 레코드
        }
        
        festival.setSeq(id);
        festival.setContentName(getColumnValue(record, "콘텐츠명"));
        festival.setDistrict(getColumnValue(record, "구군"));
        
        // 위도, 경도 파싱 (null 체크)
        try {
            String latStr = getColumnValue(record, "위도");
            String lngStr = getColumnValue(record, "경도");
            if (latStr != null && !latStr.trim().isEmpty()) {
                festival.setLatitude(Double.parseDouble(latStr));
            }
            if (lngStr != null && !lngStr.trim().isEmpty()) {
                festival.setLongitude(Double.parseDouble(lngStr));
            }
        } catch (NumberFormatException e) {
            // 파싱 실패시 null로 유지
        }
        
        festival.setLocation(getColumnValue(record, "장소"));
        festival.setTitle(getColumnValue(record, "제목"));
        festival.setSubtitle(getColumnValue(record, "부제목"));
        festival.setMainPlace(getColumnValue(record, "주요장소"));
        festival.setAddress(getColumnValue(record, "주소"));
        festival.setAddressEtc(getColumnValue(record, "주소 기타"));
        festival.setContact(getColumnValue(record, "연락처"));
        festival.setHomepage(getColumnValue(record, "홈페이지"));
        festival.setTransportation(getColumnValue(record, "교통정보"));
        festival.setOperationPeriod(getColumnValue(record, "운영기간"));
        festival.setOperationTime(getColumnValue(record, "이용요일 및 시간"));
        festival.setFee(getColumnValue(record, "이용요금"));
        festival.setImageUrl(getColumnValue(record, "이미지URL"));
        festival.setThumbUrl(getColumnValue(record, "썸네일이미지URL"));
        festival.setDetailContent(getColumnValue(record, "상세내용"));
        festival.setFacilities(getColumnValue(record, "편의시설"));
        
        return festival;
    }
    
    // 안전하게 컬럼 값을 가져오는 메서드
    private String getColumnValue(CSVRecord record, String columnName) {
        try {
            if (record.isMapped(columnName)) {
                String value = record.get(columnName);
                return (value != null && !value.trim().isEmpty()) ? value.trim() : null;
            }
            // 컬럼 이름에 공백이 있는 경우 트림된 버전으로 다시 시도
            String trimmedColumnName = columnName.trim();
            if (record.isMapped(trimmedColumnName)) {
                String value = record.get(trimmedColumnName);
                return (value != null && !value.trim().isEmpty()) ? value.trim() : null;
            }
            // 헤더 목록에서 비슷한 이름 찾기
            for (String header : record.getParser().getHeaderNames()) {
                if (header.trim().equals(columnName.trim())) {
                    String value = record.get(header);
                    return (value != null && !value.trim().isEmpty()) ? value.trim() : null;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    //날짜 기준 오름차순 조회 (미래 축제만)
    public List<Festival> getUpcomingFestivals() throws Exception {
        LocalDate today = LocalDate.now();
        return getAllFestivals()
                .stream()
                .filter(f -> f.getStartDate() != null) // 날짜가 있는 것만 필터링
                .filter(f -> f.getStartDate().isAfter(today) || f.getStartDate().equals(today)) // 오늘 이후 축제만
                .sorted(Comparator.comparing(Festival::getStartDate))
                .collect(Collectors.toList());
    }

    // ID 리스트로 개별 축제 조회 (ID는 축제 고유번호(seq))
    public List<Festival> getFestivalById(List<String> ids) throws Exception {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        
        return getAllFestivals()
                .stream()
                .filter(f -> f.getSeq() != null && ids.contains(f.getSeq()))
                .collect(Collectors.toList());
    }

    //단일 축제 상세
    public Festival getFestivalById(String id) throws Exception {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("축제 ID가 필요합니다.");
        }
        
        return getAllFestivals()
                .stream()
                .filter(f -> f.getSeq() != null && f.getSeq().equals(id.trim()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("ID가 " + id + "인 축제를 찾을 수 없습니다."));
    }
    
    // 축제 검색 기능 추가
    public List<Festival> searchFestivals(String keyword) throws Exception {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        String lowerKeyword = keyword.toLowerCase().trim();
        
        return getAllFestivals()
                .stream()
                .filter(f -> matchesKeyword(f, lowerKeyword))
                .collect(Collectors.toList());
    }
    
    // 키워드 매칭 로직
    private boolean matchesKeyword(Festival festival, String keyword) {
        return (festival.getTitle() != null && festival.getTitle().toLowerCase().contains(keyword)) ||
               (festival.getContentName() != null && festival.getContentName().toLowerCase().contains(keyword)) ||
               (festival.getDistrict() != null && festival.getDistrict().toLowerCase().contains(keyword)) ||
               (festival.getLocation() != null && festival.getLocation().toLowerCase().contains(keyword)) ||
               (festival.getMainPlace() != null && festival.getMainPlace().toLowerCase().contains(keyword)) ||
               (festival.getDetailContent() != null && festival.getDetailContent().toLowerCase().contains(keyword));
    }
    
    // 캐시 초기화 (필요시 사용)
    public void clearCache() {
        synchronized (cacheLock) {
            cachedFestivals = null;
            lastCacheUpdate = 0;
        }
    }
    
    // CSV 파일 경로 정보 조회 (디버깅용)
    public String getCsvFilePath() {
        return festivalConfig.getCsv().getFilePath();
    }
    
    // 캐시 상태 정보 조회 (디버깅용)
    public Map<String, Object> getCacheInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("cacheEnabled", festivalConfig.getCache().isEnabled());
        info.put("refreshInterval", festivalConfig.getCache().getRefreshInterval());
        info.put("cachedFestivalCount", cachedFestivals != null ? cachedFestivals.size() : 0);
        info.put("lastCacheUpdate", new Date(lastCacheUpdate));
        return info;
    }
}
