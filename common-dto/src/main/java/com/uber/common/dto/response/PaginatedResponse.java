package com.uber.common.dto.response;
import lombok.*; 
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class PaginatedResponse<T> { 
    private int totalCount; 
    private List<T> results; 
}
