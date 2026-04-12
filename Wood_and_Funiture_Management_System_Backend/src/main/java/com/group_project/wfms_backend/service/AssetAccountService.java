package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.dto.auth.AssetAccountDTO;
import com.group_project.wfms_backend.model.AssetAccount;
import com.group_project.wfms_backend.model.AssetType;
import com.group_project.wfms_backend.model.User;
import com.group_project.wfms_backend.repository.AssetAccountRepository;
import com.group_project.wfms_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetAccountService {
    @Autowired
    private AssetAccountRepository assetRepository;
    @Autowired
    private UserRepository userRepository;

    public AssetAccountDTO saveAsset(AssetAccountDTO dto) {
        AssetAccount asset = new AssetAccount();
        // Update කරනවා නම් පවතින record එක ගන්න
        if (dto.getAssetId() != null) {
            asset = assetRepository.findById(dto.getAssetId()).orElse(new AssetAccount());
        }

        asset.setAssetName(dto.getAssetName());
        asset.setAssetType(AssetType.valueOf(dto.getAssetType()));
        asset.setPurchaseDate(dto.getPurchaseDate());
        asset.setPurchaseValue(dto.getPurchaseValue());
        asset.setCurrentValue(dto.getCurrentValue());
        asset.setDepreciationRate(dto.getDepreciationRate());
        asset.setDescription(dto.getDescription());

        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId()).orElse(null);
            asset.setCreatedBy(user);
        }

        AssetAccount savedAsset = assetRepository.save(asset);
        return dto; // සරල කිරීමට නැවත DTO එකම එවන්න
    }

    public List<AssetAccount> getAllAssets() {
        return assetRepository.findAll();
    }

    public void deleteAsset(Integer id) {
        assetRepository.deleteById(id);
    }

    public AssetAccount getAssetById(Integer id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found with ID: " + id));
    }
}
