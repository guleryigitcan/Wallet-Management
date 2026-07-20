package com.guleryigitcan.WalletManagement.service;

import com.guleryigitcan.WalletManagement.config.RabbitMQConfig;
import com.guleryigitcan.WalletManagement.dto.AssetRequestDTO;
import com.guleryigitcan.WalletManagement.dto.AssetResponseDTO;
import com.guleryigitcan.WalletManagement.model.Asset;
import com.guleryigitcan.WalletManagement.repository.AssetRepository;
import com.guleryigitcan.WalletManagement.repository.AssetSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @CacheEvict(value = "assets", allEntries = true)
    public AssetResponseDTO createAsset(AssetRequestDTO requestDTO) {
        if (requestDTO.getAmount().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("The asset amount can not be negative!");
        }

        Asset asset = new Asset(null, requestDTO.getAssetName(), requestDTO.getAmount());
        Asset savedAsset = assetRepository.save(asset);

        String message = "New asset added: " + savedAsset.getAssetName() + ", Amount: " + savedAsset.getAmount();
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, message);

        return new AssetResponseDTO(savedAsset.getId(), savedAsset.getAssetName(), savedAsset.getAmount());
    }

    @Override
    @Cacheable(value = "assets")
    public List<AssetResponseDTO> getAllAssets() {
        List<Asset> assets = assetRepository.findAll();


        return assets.stream()
                .map(asset -> new AssetResponseDTO(asset.getId(), asset.getAssetName(), asset.getAmount()))
                .collect(Collectors.toList());
    }

    @Override
    public List<AssetResponseDTO> searchAssets(String name, BigDecimal minAmount) {
        List<Asset> filteredAssets = assetRepository.findAll(AssetSpecification.filterAssets(name, minAmount));

        return filteredAssets.stream()
                .map(asset -> new AssetResponseDTO(asset.getId(), asset.getAssetName(), asset.getAmount()))
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = "assets", allEntries = true)
    public void deleteAsset(Long id) {
        if (!assetRepository.existsById(id)) {
            throw new IllegalArgumentException("The entity to be deleted was not found!");
        }
        assetRepository.deleteById(id);
    }

    @Override
    public Page<AssetResponseDTO> searchAssets(String name, BigDecimal minAmount, Pageable pageable) {
        Page<Asset> assetPage = assetRepository.findAll(
                AssetSpecification.filterAssets(name, minAmount),
                pageable
        );

        return assetPage.map(asset -> new AssetResponseDTO(
                asset.getId(),
                asset.getAssetName(),
                asset.getAmount()
        ));
    }
}
