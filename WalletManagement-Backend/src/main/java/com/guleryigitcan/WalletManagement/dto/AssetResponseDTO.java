package com.guleryigitcan.WalletManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetResponseDTO {
    private Long id;
    private String assetName;
    private BigDecimal amount;
}
