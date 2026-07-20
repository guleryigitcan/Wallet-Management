package com.guleryigitcan.WalletManagement.controller;

import com.guleryigitcan.WalletManagement.dto.AssetRequestDTO;
import com.guleryigitcan.WalletManagement.dto.AssetResponseDTO;
import com.guleryigitcan.WalletManagement.model.Asset;
import com.guleryigitcan.WalletManagement.service.AssetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AssetController {

    private final AssetService assetService;


    @PostMapping
    public ResponseEntity<AssetResponseDTO> createAsset(@Valid @RequestBody AssetRequestDTO requestDTO) {
        return new ResponseEntity<>(assetService.createAsset(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AssetResponseDTO>> getAllAssets() {
        return ResponseEntity.ok(assetService.getAllAssets());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsset(@PathVariable Long id) {
        assetService.deleteAsset(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<AssetResponseDTO>> searchAssets(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minAmount,
            @PageableDefault(page = 0, size = 10, sort = "amount", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(assetService.searchAssets(name, minAmount, pageable));
    }
}
