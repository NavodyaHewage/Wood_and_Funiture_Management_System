package com.group_project.wfms_backend.controller;

import com.group_project.wfms_backend.dto.auth.AssetAccountDTO;
import com.group_project.wfms_backend.model.AssetAccount;
import com.group_project.wfms_backend.service.AssetAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@CrossOrigin
public class AssetAccountController {
    @Autowired
    private AssetAccountService assetService;

    // 1. Create - අලුත් Asset එකක් ඇතුළත් කිරීම
    @PostMapping("/save")
    public ResponseEntity<AssetAccountDTO> saveAsset(@RequestBody AssetAccountDTO dto) {
        return ResponseEntity.ok(assetService.saveAsset(dto));
    }

    // 2. Read All - සියලුම Assets ලබා ගැනීම
    @GetMapping("/all")
    public ResponseEntity<List<AssetAccount>> getAllAssets() {
        return ResponseEntity.ok(assetService.getAllAssets());
    }

    // 3. Read by ID - එක් නිශ්චිත Asset එකක් ලබා ගැනීම
    @GetMapping("/{id}")
    public ResponseEntity<AssetAccount> getAssetById(@PathVariable Integer id) {
        // Service එකේ findById එකක් අවශ්‍ය නම් සරලව මෙසේ ලබාගත හැක
        return ResponseEntity.ok(assetService.getAssetById(id));
    }

    // 4. Update - පවතින Asset එකක් යාවත්කාලීන කිරීම
    @PutMapping("/update")
    public ResponseEntity<AssetAccountDTO> updateAsset(@RequestBody AssetAccountDTO dto) {
        if (dto.getAssetId() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(assetService.saveAsset(dto));
    }

    // 5. Delete - Asset එකක් ඉවත් කිරීම
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAsset(@PathVariable Integer id) {
        assetService.deleteAsset(id);
        return ResponseEntity.ok("Asset Deleted Successfully with ID: " + id);
    }
}
