package com.guleryigitcan.WalletManagement.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetRequestDTO {

    @NotBlank(message = "The asset name can not ve empty!")
    @Size(min = 2, max = 50, message = "The asset name must be between 2 and 50 characters long!")
    private String assetName;

    @NotNull(message = "The quantity field cannot be left blank0!")
    @DecimalMin(value = "0.0", inclusive = true, message = "The asset amount can not be negative!")
    private BigDecimal amount;
}
