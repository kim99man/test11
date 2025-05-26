package com.effective.festive.service;

import com.effective.festive.model.Festival;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FestivalService {
    private static final String CSV_FILE_PATH = "busan_festivals.csv";

    //CSV 파일에서 전체 축제 목록 가져옴
    public List<Festival> getAllFestivals() throws Exception {
        List<Festival> festivals = new ArrayList<>();
        
        try {
            ClassPathResource resource = new ClassPathResource(CSV_FILE_PATH);
            CSVParser parser = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withTrim()  // 헤더와 값의 공백 제거
                    .parse(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            
            for (CSVRecord record : parser) {
                Festival festival = new Festival();
                
                // 안전한 방법으로 컬럼 값 가져오기
                festival.setSeq(getColumnValue(record, "콘텐츠ID"));
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
                festival.setAddressEtc(getColumnValue(record, "주소 기타"));  // 공백 포함된 헤더
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
                
                festivals.add(festival);
            }
            
            parser.close();
        } catch (IOException e) {
            throw new Exception("CSV 파일을 읽는 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
        
        return festivals;
    }
    
    // 안전하게 컬럼 값을 가져오는 메서드
    private String getColumnValue(CSVRecord record, String columnName) {
        try {
            if (record.isMapped(columnName)) {
                return record.get(columnName);
            }
            // 컬럼 이름에 공백이 있는 경우 트림된 버전으로 다시 시도
            String trimmedColumnName = columnName.trim();
            if (record.isMapped(trimmedColumnName)) {
                return record.get(trimmedColumnName);
            }
            // 헤더 목록에서 비슷한 이름 찾기
            for (String header : record.getParser().getHeaderNames()) {
                if (header.trim().equals(columnName.trim())) {
                    return record.get(header);
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    //날짜 기준 오름차순 조회
    public List<Festival> getUpcomingFestivals() throws Exception {
        return getAllFestivals()
                .stream()
                .filter(f -> f.getStartDate() != null) // 날짜가 있는 것만 필터링
                .sorted(Comparator.comparing(Festival::getStartDate))
                .collect(Collectors.toList());
    }

    // ID 리스트로 개별 축제 조회 (ID는 축제 고유번호(seq))
    public List<Festival> getFestivalById(List<String> ids) throws Exception {
        return getAllFestivals()
                .stream()
                .filter(f -> f.getSeq() != null && ids.contains(f.getSeq()))
                .collect(Collectors.toList());
    }

    //단일 축제 상세
    public Festival getFestivalById(String id) throws Exception {
        return getAllFestivals()
                .stream()
                .filter(f -> f.getSeq() != null && f.getSeq().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("축제를 찾을 수 없습니다"));
    }
}
