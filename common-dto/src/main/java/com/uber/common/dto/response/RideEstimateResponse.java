package com.uber.common.dto.response;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RideEstimateResponse {
    private double estimatedTimeMinutes; private double estimatedCost; private double distanceKm;
}
