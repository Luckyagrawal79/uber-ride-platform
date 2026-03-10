package com.uber.common.dto.request;
import jakarta.validation.constraints.*; import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor
public class PanicRequest { @NotBlank private String reason; }
