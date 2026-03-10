package com.uber.common.dto.request;
import jakarta.validation.constraints.*; import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor
public class ReviewRequest {
    @NotNull @Min(1) @Max(5) private Integer driverRating;
    private String driverComment;
    @NotNull @Min(1) @Max(5) private Integer vehicleRating;
    private String vehicleComment;
}
