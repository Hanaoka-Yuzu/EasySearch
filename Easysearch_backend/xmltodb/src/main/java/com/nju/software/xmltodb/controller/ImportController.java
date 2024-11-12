package com.nju.software.xmltodb.controller;

import com.nju.software.common.utils.Result;
import com.nju.software.xmltodb.service.ImportService;
import com.nju.software.xmltodb.vo.DatasetVo;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

import static com.nju.software.common.utils.Constant.*;

/**
 * @Description 数据导入
 * @Author wxy
 * @Date 2024/2/2
 **/
@RestController
@RequestMapping("/import")
@Validated
public class ImportController {
    private final ImportService importService;

    @Autowired
    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @PostMapping("/upload")
    public Result uploadZipFile(@NotNull @RequestParam("zipFile") MultipartFile zipFile) {
        String datasetId = importService.uploadData(zipFile);
        return Result.ok().data(DATASET_ID, datasetId);
    }

    @PostMapping("/upload/local")
    public Result uploadData(String path) {
        String datasetId = importService.uploadData(path, false);
        return Result.ok().data(DATASET_ID, datasetId);
    }

    @PostMapping("/upload/sync")
    public Result syncUpload(String path) {
        String datasetId = importService.uploadData(path, true);
        return Result.ok().data(DATASET_ID, datasetId);
    }

    @GetMapping("/status")
    public Result getUploadStatus(String datasetId) {
        if (Objects.nonNull(datasetId) && StringUtils.isNotBlank(datasetId)) {
            DatasetVo datasetVo = importService.getStatusByDatasetId(datasetId);
            return Result.ok().data(DATASET_VO, datasetVo);
        } else {
            List<DatasetVo> datasetVoList = importService.getUploadStatus();
            return Result.ok().data(DATASET_VO_LIST, datasetVoList);
        }


    }


}
