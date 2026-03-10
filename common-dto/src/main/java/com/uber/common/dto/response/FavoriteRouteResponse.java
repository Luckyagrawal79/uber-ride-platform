package com.uber.common.dto.response;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FavoriteRouteResponse {
    private Long id; private String name;
    private double departureLatitude; private double departureLongitude; private String departureAddress;
    private double destinationLatitude; private double destinationLongitude; private String destinationAddress;
}
