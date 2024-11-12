package com.nju.software.xmltodb.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author wxy
 * @Date 2024/3/6
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatasetVo {

    /**
     * 数据集id
     */
    private String datasetId;

    /**
     * 数据集文件名
     */
    private String datasetName;

    /**
     * 数据集类型（压缩包 or 文件夹）
     */
    private String datasetType;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 是否处理完成
     */
    private Boolean finished;

    /**
     * 是否出错
     */
    private Boolean error;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 压缩包内xml文件数量
     */
    private Integer totalCount;

    /**
     * 成功上传数量
     */
    private Integer uploadCount;

    /**
     * 保存文件数量
     */
    private Integer saveCount;

    /**
     * 数据集导入状态
     */
    private String status;

}
