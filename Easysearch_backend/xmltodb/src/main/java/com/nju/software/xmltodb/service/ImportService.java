package com.nju.software.xmltodb.service;

import com.nju.software.xmltodb.vo.DatasetVo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Description 数据导入服务
 * @Author wxy
 * @Date 2024/2/5
 **/
@Service
public interface ImportService {

    /**
     * 上传数据压缩包，解析数据并导入mysql数据库
     *
     * @param multipartFile 文件
     * @return 导入数据集id
     */
    String uploadData(MultipartFile multipartFile);

    /**
     * 上传数据文件夹目录，解析数据并导入mysql数据库
     *
     * @param path 文件路径
     * @param sync 是否同步
     * @return 导入数据集id
     */
    String uploadData(String path, Boolean sync);

    /**
     * 获取数据集导入状态
     *
     * @return 所有数据集导入状态
     */
    List<DatasetVo> getUploadStatus();

    /**
     * 根据数据集id获取导入状态
     *
     * @param datasetId 数据集id
     * @return 数据集导入状态
     */
    DatasetVo getStatusByDatasetId(String datasetId);

    /**
     * 同步数据到es
     */
    void syncData();
}
