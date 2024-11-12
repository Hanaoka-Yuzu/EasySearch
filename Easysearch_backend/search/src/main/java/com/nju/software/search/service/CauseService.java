package com.nju.software.search.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description 案由层级服务
 * @Author wxy
 * @Date 2024/3/15
 **/
@Service
public interface CauseService {
    /**
     * 解析 excel，获取案由层级
     * @param excelFile excel 文件
     */
    void analyseExcel(MultipartFile excelFile);

    /**
     * 更新案由节点信息
     */
    void updateNodeInfo();

    /**
     * 同步案由统计数据
     */
    void syncCauseCountData();
}
