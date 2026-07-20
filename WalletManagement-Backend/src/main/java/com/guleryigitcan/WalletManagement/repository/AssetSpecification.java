package com.guleryigitcan.WalletManagement.repository;

import com.guleryigitcan.WalletManagement.model.Asset;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class AssetSpecification {

    public static Specification<Asset> filterAssets(String searchName, BigDecimal minAmount) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (searchName != null && !searchName.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("assetName")),
                        "%" + searchName.toLowerCase() + "%"
                ));
            }

            if (minAmount != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), minAmount));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
