package com.uber.common.dto.response;
import lombok.*; import java.time.LocalDate; import java.util.Map;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReportResponse {
    private int totalRides; private double totalDistance; private double totalEarnings;
    private double averageRating;
    private Map<LocalDate, Integer> ridesPerDay;
    private Map<LocalDate, Double> earningsPerDay;
}
