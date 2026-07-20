package com.guleryigitcan.WalletManagement.service;

import com.guleryigitcan.WalletManagement.dto.AssetRequestDTO;
import com.guleryigitcan.WalletManagement.dto.AssetResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface AssetService {
    AssetResponseDTO createAsset(AssetRequestDTO requestDTO);
    List<AssetResponseDTO> getAllAssets();
    List<AssetResponseDTO> searchAssets(String name, BigDecimal minAmount);
    void deleteAsset(Long id);
    Page<AssetResponseDTO> searchAssets(String name, BigDecimal minAmount, Pageable pageable);
}