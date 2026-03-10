package com.uber.common.dto.request;
import com.uber.common.enums.*; import jakarta.validation.constraints.*; import lombok.*;
import java.time.LocalDateTime; import java.util.List;
@Data @NoArgsConstructor @AllArgsConstructor
public class RideRequest {
    @NotNull private LocationDto departure;
    @NotNull private LocationDto destination;
    @NotNull private VehicleType vehicleType;
    private boolean babyTransport;
    private boolean petTransport;
    private PaymentMethod paymentMethod = PaymentMethod.CASH;
    private LocalDateTime scheduledTime;
    private List<Long> additionalPassengerIds;
}
