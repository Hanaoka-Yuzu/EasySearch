package com.nju.software.xmltodb.utils;

import cn.hutool.core.util.ZipUtil;
import com.nju.software.common.entity.CaseInfo;
import com.nju.software.common.utils.CodeEnum;
import com.nju.software.xmltodb.exception.XmltodbException;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.nju.software.common.utils.Constant.FILE_FILTER;

/**
 * @Author wxy
 * @Date 2024/2/18
 **/
@Slf4j
@Service
public class FileUtil {

    @Value("${config.uploadPath}")
    private String uploadPath;

    @Value("${config.uploadPath}")
    private String extractPath;

    @Resource
    private RedisUtil redisUtil;

    /**
     * 将MultipartFile转换为File
     *
     * @param multipartFile 上传文件
     * @return 文件
     */
    public File convert(MultipartFile multipartFile) throws IOException {
        // 检查文件名是否为空
        String fileName = multipartFile.getOriginalFilename();
        if (fileName == null) {
            throw new XmltodbException(CodeEnum.UPLOAD_FILE_NAME_EMPTY.getMsg(), CodeEnum.UPLOAD_FILE_NAME_EMPTY.getCode());
        }
        // 检查文件格式是否为zip
        String fileFormat = fileName.substring(fileName.lastIndexOf("."));
        if (!".zip".equals(fileFormat)) {
            log.error("上传文件格式错误，只接受.zip格式文件{}", fileFormat);
            throw new XmltodbException(CodeEnum.UPLOAD_ZIP_FILE_FORMAT_ERROR.getMsg(), CodeEnum.UPLOAD_ZIP_FILE_FORMAT_ERROR.getCode());
        }
        log.info("zip file upload Path: {}", uploadPath);
        //压缩包存储目标文件对象
        File destFile = new File(uploadPath + fileName);
        //文件上传路径对象
        File fileDirectory = new File(uploadPath);
        //当上传路径不存在时，生成上传路径
        if (!fileDirectory.exists()) {
            makeDir(fileDirectory);
        }
        //得到压缩包内文件
        multipartFile.transferTo(destFile);
        return destFile;
    }

    /**
     * 解析xml文件
     * @param fileList 文件列表
     * @return 返回解析后的CaseInfo列表
     */
    public List<CaseInfo> parse(List<File> fileList) {
        List<CaseInfo> caseInfoList = new ArrayList<>();
        try {
            for (File file : fileList) {
                InputStream inputStream = new FileInputStream(file);
                SAXParserFactory spf = SAXParserFactory.newInstance();
                SAXParser parser = spf.newSAXParser();
                SaxHandler handler = new SaxHandler(redisUtil.getIncNum(null));
                parser.parse(inputStream, handler);
                CaseInfo caseInfo = handler.getCaseInfo();
                caseInfoList.add(caseInfo);
                inputStream.close();
            }
        } catch (IOException | ParserConfigurationException | SAXException ioException) {
            log.error(ioException.toString());
            throw new RuntimeException(ioException);
        }

        return caseInfoList;
    }

    /**
     * 处理压缩包文件
     *
     * @param zipFile 指定压缩包路径
     * @return 返回解压后的文件流
     */
    public List<File> dealZip(File zipFile) {
        //解压目标文件夹对象（压缩文件解压到此文件夹中）
        String fileName = getFileName(zipFile);
        File extractFolder = new File(extractPath + fileName + "/");
        log.info("xml file uploadPath: {}{}/", uploadPath, fileName);
        if (!extractFolder.exists()) {
            makeDir(extractFolder);
        }
        ZipUtil.unzip(zipFile, extractFolder);
        return traverseFolder(extractFolder);
    }

    public List<File> traverseFolder(File folder) {
        // 获取解压后目录下所有的文件
        IOFileFilter directoryFilter = new NotFileFilter(new NameFileFilter(FILE_FILTER));
        IOFileFilter xmlFilter = new SuffixFileFilter(".xml");
        List<File> fileList = (List<File>) FileUtils.listFiles(folder, xmlFilter, directoryFilter);
        // 对获取到的文件数组进行判空校验
        if (fileList.isEmpty()) {
            throw new XmltodbException(CodeEnum.UPLOAD_ZIP_FILE_EMPTY.getMsg(), CodeEnum.UPLOAD_ZIP_FILE_EMPTY.getCode());
        }
        log.info("解压完成, 共计{}条数据", fileList.size());
        return fileList;
    }


    private void makeDir(@NotNull File dir) {
        if (!dir.exists()) {
            boolean result = dir.mkdirs();
            if (!result) {
                log.error("文件创建失败{}", dir.getName());
            }
        }
    }

    private String getFileName(File file) {
        return file.getName().split("\\.")[0];
    }
}
