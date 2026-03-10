package com.uber.common.dto.request;
import jakarta.validation.constraints.*; import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LocationDto {
    @NotNull private Double latitude;
    @NotNull private Double longitude;
    private String address;
}
