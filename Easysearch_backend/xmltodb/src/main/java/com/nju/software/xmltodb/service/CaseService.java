package com.nju.software.xmltodb.service;

import com.nju.software.common.entity.CaseInfo;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * @Description 案例文书服务
 * @Author wxy
 * @Date 2024/3/5
 **/
@Service
public interface CaseService {
    /**
     * 批量mysql导入数据
     *
     * @param caseInfoList 文书数据
     */
    Integer uploadMySQLData(List<CaseInfo> caseInfoList);

    /**
     * 批量导入es数据
     *
     * @param caseInfoList 文书数据
     */
    Integer uploadESData(List<CaseInfo> caseInfoList);

    /**
     * 多线程解析文件
     *
     * @param fileList xml文件列表
     * @return 文书
     */
    List<CaseInfo> parse(List<File> fileList);
}
