package com.nju.software.download.controller;


import com.nju.software.common.entity.CaseText;
import com.nju.software.common.utils.Result;
import com.nju.software.download.service.DownloadService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

import static com.nju.software.common.utils.Constant.STORE_DOC_COUNT;

@RestController
@RequestMapping("/download")
public class DownloadController {

    private final DownloadService downloadService;


    @Autowired
    public DownloadController(DownloadService downloadService) {
        this.downloadService = downloadService;
    }

    @PostMapping("/saveDoc")
    public Result saveDocByFeign(@RequestBody List<CaseText> caseTextList) {
        Integer storeDocCount = downloadService.storeDoc(caseTextList);
        return Result.ok().data(STORE_DOC_COUNT, storeDocCount);
    }

    @GetMapping("/downloadDoc")
    public void downloadDoc(HttpServletResponse response, @NotNull String caseOrder,@NotNull String userID) {
        downloadService.downloadDoc(response, caseOrder,userID);
    }

    @GetMapping("/batchDownloadDoc")
    public void batchDownloadDoc(HttpServletResponse response, @NotNull String[] caseOrder,@NotNull String userID) {
        downloadService.batchDownloadDoc(response, Arrays.stream(caseOrder).toList(),userID);
    }

    @GetMapping("/test")
    public Result test() {
        return Result.ok().code(201);
    }


}