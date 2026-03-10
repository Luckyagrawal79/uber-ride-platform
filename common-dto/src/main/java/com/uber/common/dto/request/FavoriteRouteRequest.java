package com.uber.common.dto.request;
import jakarta.validation.constraints.*; import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor
public class FavoriteRouteRequest {
    @NotBlank private String name;
    @NotNull private LocationDto departure;
    @NotNull private LocationDto destination;
}
