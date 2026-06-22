package com.group_project.wfms_backend.dto;

import com.group_project.wfms_backend.model.ProductStock;
import com.group_project.wfms_backend.model.UnitOfMeasurement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductStockDTO {
    private Integer stockId;
    private Integer productCatId;
    private String materialCategory;
    private String description;
    private UnitOfMeasurement unitOfMeasurement;
    private BigDecimal unitPrice;
    private BigDecimal availableQuantity;

    public static ProductStockDTO fromEntity(ProductStock stock) {
        ProductStockDTO dto = new ProductStockDTO();
        dto.setStockId(stock.getStockId());
        dto.setAvailableQuantity(stock.getAvailableQuantity());

        if (stock.getProductCategory() != null) {
            dto.setProductCatId(stock.getProductCategory().getProductCatId());
            dto.setMaterialCategory(stock.getProductCategory().getMaterialCategory());
            dto.setDescription(stock.getProductCategory().getDescription());
            dto.setUnitOfMeasurement(stock.getProductCategory().getUnitOfMeasurement());
            dto.setUnitPrice(stock.getProductCategory().getUnitPrice());
        }

        return dto;
    }
}
