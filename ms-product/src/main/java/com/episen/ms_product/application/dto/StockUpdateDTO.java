package com.episen.ms_product.application.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockUpdateDTO {
    @Min(value = 0, message = "Le stock ne peut pas être négatif")
    private Integer newStock;
}