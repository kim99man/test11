package com.effective.festive.model;

<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
=======
import com.opencsv.bean.CsvBindByName;
>>>>>>> dbff002 (csv 수정3)
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

<<<<<<< HEAD
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

=======
>>>>>>> dbff002 (csv 수정3)
@Data //getter, setter 자동 생성
@NoArgsConstructor //기본 생성자 자동 생성
@AllArgsConstructor //모든 필드를 인자로 받는 생성자 자동 생성
public class Festival {
<<<<<<< HEAD
    @JsonProperty("id")
    private String seq; //축제 고유 ID (sequence number)
    
    @JsonProperty("contentName")
    private String contentName; //콘텐츠명
    
    @JsonProperty("district")
    private String district; //구군
    
    @JsonProperty("latitude")
    private Double latitude; //위도
    
    @JsonProperty("longitude")
    private Double longitude; //경도
    
    @JsonProperty("location")
    private String location; //장소
    
    @JsonProperty("title")
    private String title; //축제명

    @JsonProperty("subtitle")
    private String subtitle; //부제목
    
    @JsonProperty("mainPlace")
    private String mainPlace; //주요장소
    
    @JsonProperty("address")
    private String address; //주소
    
    @JsonProperty("addressEtc")
    private String addressEtc; //주소 기타
    
    @JsonProperty("contact")
    private String contact; //연락처
    
    @JsonProperty("homepage")
    private String homepage; //홈페이지
    
    @JsonProperty("transportation")
    private String transportation; //교통정보
    
    @JsonProperty("operationPeriod")
    private String operationPeriod; //운영기간
    
    @JsonProperty("operationTime")
    private String operationTime; //이용요일 및 시간
    
    @JsonProperty("fee")
    private String fee; //이용요금

    @JsonProperty("imageUrl")
    private String imageUrl;

    @JsonProperty("thumbUrl")
    private String thumbUrl;

    @JsonProperty("detailContent")
    private String detailContent; //상세내용
    
    @JsonProperty("facilities")
    private String facilities; //편의시설

    // 시작일과 종료일을 운영기간에서 파싱하는 메서드 (개선됨)
    @JsonProperty("startDate")
    public LocalDate getStartDate() {
        return parseDateFromOperationPeriod(true);
    }

    @JsonProperty("endDate")
    public LocalDate getEndDate() {
        return parseDateFromOperationPeriod(false);
    }
    
    // 날짜 파싱 로직 개선
    @JsonIgnore
    private LocalDate parseDateFromOperationPeriod(boolean isStartDate) {
        if (operationPeriod == null || operationPeriod.trim().isEmpty()) {
            return null;
        }
        
        try {
            // 다양한 날짜 형식 패턴들
            Pattern[] patterns = {
                // 2024. 07. 26.(금) ~ 07. 28.(일) 형태
                Pattern.compile("(\\d{4})\\. (\\d{1,2})\\. (\\d{1,2})\\.[^~]*~[^\\d]*(\\d{1,2})\\. (\\d{1,2})\\.[^\\d]*"),
                // 2024.07.26 ~ 2024.07.28 형태
                Pattern.compile("(\\d{4})\\.(\\d{1,2})\\.(\\d{1,2})[^~]*~[^\\d]*(\\d{4})\\.(\\d{1,2})\\.(\\d{1,2})"),
                // 2024-07-26 ~ 2024-07-28 형태
                Pattern.compile("(\\d{4})-(\\d{1,2})-(\\d{1,2})[^~]*~[^\\d]*(\\d{4})-(\\d{1,2})-(\\d{1,2})"),
                // 매년 X월 형태 (올해로 가정)
                Pattern.compile("매년\\s*(\\d{1,2})월"),
            };
            
            for (Pattern pattern : patterns) {
                Matcher matcher = pattern.matcher(operationPeriod);
                if (matcher.find()) {
                    if (pattern.pattern().contains("매년")) {
                        // 매년 X월 형태인 경우
                        int month = Integer.parseInt(matcher.group(1));
                        int currentYear = LocalDate.now().getYear();
                        return isStartDate ? 
                            LocalDate.of(currentYear, month, 1) :
                            LocalDate.of(currentYear, month, LocalDate.of(currentYear, month, 1).lengthOfMonth());
                    } else if (pattern.pattern().contains("\\. (\\d{1,2})\\. (\\d{1,2})\\.[^~]*~[^\\d]*(\\d{1,2})\\. (\\d{1,2})")) {
                        // 2024. 07. 26.(금) ~ 07. 28.(일) 형태
                        String year = matcher.group(1);
                        String startMonth = String.format("%02d", Integer.parseInt(matcher.group(2)));
                        String startDay = String.format("%02d", Integer.parseInt(matcher.group(3)));
                        String endMonth = String.format("%02d", Integer.parseInt(matcher.group(4)));
                        String endDay = String.format("%02d", Integer.parseInt(matcher.group(5)));
                        
                        if (isStartDate) {
                            return LocalDate.parse(year + "-" + startMonth + "-" + startDay);
                        } else {
                            return LocalDate.parse(year + "-" + endMonth + "-" + endDay);
                        }
                    } else {
                        // 다른 형태들
                        if (isStartDate) {
                            String year = matcher.group(1);
                            String month = String.format("%02d", Integer.parseInt(matcher.group(2)));
                            String day = String.format("%02d", Integer.parseInt(matcher.group(3)));
                            return LocalDate.parse(year + "-" + month + "-" + day);
                        } else {
                            String year = matcher.group(4);
                            String month = String.format("%02d", Integer.parseInt(matcher.group(5)));
                            String day = String.format("%02d", Integer.parseInt(matcher.group(6)));
                            return LocalDate.parse(year + "-" + month + "-" + day);
                        }
                    }
                }
            }
            
            // 단일 날짜인 경우 (~ 없는 경우)
            Pattern singleDatePattern = Pattern.compile("(\\d{4})\\. (\\d{1,2})\\. (\\d{1,2})\\.");
            Matcher singleMatcher = singleDatePattern.matcher(operationPeriod);
            if (singleMatcher.find()) {
                String year = singleMatcher.group(1);
                String month = String.format("%02d", Integer.parseInt(singleMatcher.group(2)));
                String day = String.format("%02d", Integer.parseInt(singleMatcher.group(3)));
                return LocalDate.parse(year + "-" + month + "-" + day);
            }
            
        } catch (Exception e) {
            // 파싱 실패시 null 반환
        }
        
        return null;
    }
    
    // 기존 API 호환성을 위한 메서드들
    @JsonProperty("place")
    public String getPlace() {
        return this.mainPlace != null ? this.mainPlace : this.location;
    }
    
    @JsonProperty("url")
    public String getUrl() {
        return this.homepage;
=======
    @CsvBindByName(column = "콘텐츠ID")
    private String seq; //축제 고유 ID

    @CsvBindByName(column = "콘텐츠명")
    private String title; //축제명

    @CsvBindByName(column = "구군")
    private String district; //구군

    @CsvBindByName(column = "위도")
    private String latitude; //위도

    @CsvBindByName(column = "경도")
    private String longitude; //경도

    @CsvBindByName(column = "장소")
    private String location; //장소

    @CsvBindByName(column = "제목")
    private String subtitle; //제목

    @CsvBindByName(column = "부제목")
    private String description; //부제목

    @CsvBindByName(column = "주요장소")
    private String mainPlace; //주요장소

    @CsvBindByName(column = "주소")
    private String address; //주소

    @CsvBindByName(column = "연락처")
    private String contact; //연락처

    @CsvBindByName(column = "홈페이지")
    private String homepage; //홈페이지

    @CsvBindByName(column = "교통정보")
    private String transportation; //교통정보

    @CsvBindByName(column = "운영기간")
    private String operatingPeriod; //운영기간

    @CsvBindByName(column = "이용요금")
    private String fee; //이용요금

    @CsvBindByName(column = "이미지URL")
    private String imageUrl; //이미지URL

    @CsvBindByName(column = "썸네일이미지URL")
    private String thumbUrl; //썸네일이미지URL

    @CsvBindByName(column = "상세내용")
    private String detailContent; //상세내용

    @CsvBindByName(column = "편의시설")
    private String facilities; //편의시설

    // 기존 API 호환성을 위한 메서드들
    public String getStartDate() {
        return operatingPeriod;
    }

    public String getEndDate() {
        return operatingPeriod;
    }

    public String getPlace() {
        return mainPlace != null ? mainPlace : address;
    }

    public String getUrl() {
        return homepage;
>>>>>>> dbff002 (csv 수정3)
    }
}
