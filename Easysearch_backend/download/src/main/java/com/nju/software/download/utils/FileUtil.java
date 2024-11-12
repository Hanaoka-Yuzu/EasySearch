package com.nju.software.download.utils;

import com.nju.software.common.entity.CaseText;
import com.nju.software.download.entity.StandardMultipartFile;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.core.ApplicationPart;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static java.util.Map.entry;

@Slf4j
@Service
public class FileUtil {

    @Value("${config.storePath}")
    private String storePath;

    @Autowired
    MinioTemplate minioTemplate;

    @Value("${config.templatePath}")
    private String templatePath;

    public Integer storeDoc(List<CaseText> caseTextList) {
        try {
            for (CaseText each : caseTextList) {

                Map<String, Object> dataMap = Map.ofEntries(
                        entry("caseOrder", Optional.ofNullable(each.getCaseOrder()).orElse("")),
                        entry("textWhole", Optional.ofNullable(each.getTextWhole()).orElse("")),
                        entry("caseLitigant", Optional.ofNullable(each.getCaseLitigant()).orElse("")),
                        entry("caseLitigationRecord", Optional.ofNullable(each.getCaseLitigationRecord()).orElse("")),
                        entry("caseBasic", Optional.ofNullable(each.getCaseBasic()).orElse("")),
                        entry("caseAnalysis", Optional.ofNullable(each.getCaseAnalysis()).orElse("")),
                        entry("caseResult", Optional.ofNullable(each.getCaseResult()).orElse("")),
                        entry("textEnd", Optional.ofNullable(each.getTextEnd()).orElse(""))
                );
                File tempFile = File.createTempFile("temp", ".docx");
                createDocx(dataMap, tempFile);

                minioTemplate.upLoadFile("document", each.getCaseOrder(), getMultipartFile(tempFile));

                tempFile.delete();
            }
        } catch (IOException ioException) {
            log.error(ioException.toString());
        }

        return caseTextList.size();
    }

    public void createDocx(Map dataMap, File tempFile) {
        try (
                ByteArrayInputStream documentInput = FreeMarkUtils.getFreemarkerContentInputStream(dataMap, templatePath);
                ZipOutputStream zipout = new ZipOutputStream(new FileOutputStream(tempFile))
        ) {
            byte[] buffer = new byte[1024];
            int len = -1;

            ClassPathResource classPathResource = new ClassPathResource("/templates/模板.zip");
            InputStream zipInputStream = classPathResource.getInputStream();
            ZipInputStream zipFile = new ZipInputStream(zipInputStream);

            ZipEntry next;
            while ((next = zipFile.getNextEntry()) != null) {
                if (!next.toString().contains("media")) {
                    zipout.putNextEntry(new ZipEntry(next.getName()));
                    if ("word/document.xml".equals(next.getName()) && documentInput != null) {
                        while ((len = documentInput.read(buffer)) != -1) {
                            zipout.write(buffer, 0, len);
                        }
                    } else {
                        while ((len = zipFile.read(buffer)) != -1) {
                            zipout.write(buffer, 0, len);
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("模板加载失败");
            e.printStackTrace();
        }
    }

    public void downloadFile(HttpServletResponse response, String fileName) {
        minioTemplate.downloadFile(response, fileName , "/document/");
    }

    public void batchDownloadFile(HttpServletResponse response, List<String> caseOrders) {
        minioTemplate.batchDownloadDoc(response, caseOrders);
    }

    public static MultipartFile getMultipartFile(File file) {
        FileItem item = new DiskFileItemFactory().createItem("FORM_FIELD_NAME", MediaType.MULTIPART_FORM_DATA_VALUE, true, file.getName());
        try (InputStream inputStream = new FileInputStream(file);
             OutputStream outputStream = item.getOutputStream();) {
            IOUtils.copy(inputStream, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Part part = new ApplicationPart(item, file);
        return new StandardMultipartFile(part, file.getName());
    }
}
