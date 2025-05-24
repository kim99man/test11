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
    private String seq; //축제 고유 ID (sequence number)
    private String title; //축제명

    @JsonProperty("startDate")
    private LocalDate startDate; //시작일

    @JsonProperty("endDate")
    private LocalDate endDate;

    @JsonProperty("MAIN_IMG_NORMAL")
    private String imageUrl;

    @JsonProperty("MAIN_IMG_THUMB")
    private String thumbUrl;

    private String place; //장소
    private String url; //상세 페이지
}
