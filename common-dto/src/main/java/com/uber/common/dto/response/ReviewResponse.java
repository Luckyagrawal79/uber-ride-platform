package com.uber.common.dto.response;
import lombok.*; import java.time.LocalDateTime;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReviewResponse {
    private Long id; private int driverRating; private String driverComment;
    private int vehicleRating; private String vehicleComment;
    private LocalDateTime createdAt; private Long passengerId; private String passengerName;
}
