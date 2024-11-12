package com.nju.software.xmltodb.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

/**
 * @Description 包装数据类
 * @Author wxy
 * @Date 2024/3/9
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataVo {
    private String datasetId;
    private String datasetType;
    private File data;
}
