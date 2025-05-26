package com.effective.festive.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data //getter, setter 자동 생성
@NoArgsConstructor //기본 생성자 자동 생성
@AllArgsConstructor //모든 필드를 인자로 받는 생성자 자동 생성
public class Festival {
    @JsonProperty("콘텐츠ID")
    private String seq; //축제 고유 ID (sequence number)
    
    @JsonProperty("콘텐츠명")
    private String contentName; //콘텐츠명
    
    @JsonProperty("구군")
    private String district; //구군
    
    @JsonProperty("위도")
    private Double latitude; //위도
    
    @JsonProperty("경도")
    private Double longitude; //경도
    
    @JsonProperty("장소")
    private String location; //장소
    
    @JsonProperty("제목")
    private String title; //축제명

    @JsonProperty("부제목")
    private String subtitle; //부제목
    
    @JsonProperty("주요장소")
    private String mainPlace; //주요장소
    
    @JsonProperty("주소")
    private String address; //주소
    
    @JsonProperty("주소 기타")
    private String addressEtc; //주소 기타
    
    @JsonProperty("연락처")
    private String contact; //연락처
    
    @JsonProperty("홈페이지")
    private String homepage; //홈페이지
    
    @JsonProperty("교통정보")
    private String transportation; //교통정보
    
    @JsonProperty("운영기간")
    private String operationPeriod; //운영기간
    
    @JsonProperty("이용요일 및 시간")
    private String operationTime; //이용요일 및 시간
    
    @JsonProperty("이용요금")
    private String fee; //이용요금

    @JsonProperty("이미지URL")
    private String imageUrl;

    @JsonProperty("썸네일이미지URL")
    private String thumbUrl;

    @JsonProperty("상세내용")
    private String detailContent; //상세내용
    
    @JsonProperty("편의시설")
    private String facilities; //편의시설

    // 시작일과 종료일을 운영기간에서 파싱하는 메서드
    public LocalDate getStartDate() {
        if (operationPeriod != null && operationPeriod.contains("~")) {
            try {
                String startDateStr = operationPeriod.split("~")[0].trim();
                return LocalDate.parse(startDateStr);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public LocalDate getEndDate() {
        if (operationPeriod != null && operationPeriod.contains("~")) {
            try {
                String endDateStr = operationPeriod.split("~")[1].trim();
                return LocalDate.parse(endDateStr);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
    
    // 기존 API 호환성을 위한 메서드들
    public String getPlace() {
        return this.mainPlace != null ? this.mainPlace : this.location;
    }
    
    public String getUrl() {
        return this.homepage;
    }
}
