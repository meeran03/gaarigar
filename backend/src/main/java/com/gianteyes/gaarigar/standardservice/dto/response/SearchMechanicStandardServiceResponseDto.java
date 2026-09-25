package com.gianteyes.gaarigar.standardservice.dto.response;


import com.gianteyes.gaarigar.Order.dto.OrderUser;
import com.gianteyes.gaarigar.Order.dto.StandardService;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class SearchMechanicStandardServiceResponseDto {
    private Long id;
    private Double price;
    private OrderUser mechanic;
    private StandardService standardService;
    private Boolean isActive;
}
