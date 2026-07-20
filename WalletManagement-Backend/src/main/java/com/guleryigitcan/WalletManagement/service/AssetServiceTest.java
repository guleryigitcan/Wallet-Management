package com.guleryigitcan.WalletManagement.service;

import com.guleryigitcan.WalletManagement.dto.AssetRequestDTO;
import com.guleryigitcan.WalletManagement.dto.AssetResponseDTO;
import com.guleryigitcan.WalletManagement.model.Asset;
import com.guleryigitcan.WalletManagement.repository.AssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;


import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private AssetServiceImpl assetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateAssetSuccessfully() {
        AssetRequestDTO requestDTO = new AssetRequestDTO("Bitcoin", new BigDecimal("1.5"));

        Asset savedAsset = new Asset(1L, "Bitcoin", new BigDecimal("1.5"));

        when(assetRepository.save(any(Asset.class))).thenReturn(savedAsset);

        AssetResponseDTO result = assetService.createAsset(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Bitcoin", result.getAssetName());
        assertEquals(new BigDecimal("1.5"), result.getAmount());


        verify(assetRepository, times(1)).save(any(Asset.class));
        verify(rabbitTemplate, times(1)).convertAndSend(any(String.class), any(String.class));
    }

    @Test
    void shouldThrowExceptionWhenAssetAmountIsNegative() {
        AssetRequestDTO negativeRequest = new AssetRequestDTO("Dollar", new BigDecimal("-50"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            assetService.createAsset(negativeRequest);
        });

        assertEquals("The asset amount can not be negative!", exception.getMessage());


        verify(assetRepository, never()).save(any());
        verify(rabbitTemplate, never()).convertAndSend(any(String.class), any(String.class));
    }
}
