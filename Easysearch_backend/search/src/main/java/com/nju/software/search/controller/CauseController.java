package com.nju.software.search.controller;


import com.nju.software.common.utils.Result;
import com.nju.software.search.service.CauseService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author wxy
 * @Date 2024/3/15
 **/
@RestController
@RequestMapping("/cause")
@Validated
public class CauseController {
    private final CauseService causeService;

    @Autowired
    public CauseController(CauseService causeService) {
        this.causeService = causeService;
    }


    @PostMapping("/upload")
    public Result uploadCauseLayer(@NotNull @RequestParam("excel") MultipartFile excelFile) {
        causeService.analyseExcel(excelFile);
        return Result.ok();
    }

    @PutMapping("/update")
    public Result updateNodeInfo() {
        causeService.updateNodeInfo();
        return Result.ok();
    }


    @PutMapping("/sync")
    public Result syncCauseCountData() {
        causeService.syncCauseCountData();
        return Result.ok();
    }

}
