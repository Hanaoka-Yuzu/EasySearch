package com.nju.software.common.utils;

import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @Description 字符串压缩工具类
 * @Author wxy
 * @Date 2024/4/3
 **/
@Slf4j
public class StringCompressUtil {

    // 压缩字符串
    public static String compress(String uncompressedString) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
            gzipOutputStream.write(uncompressedString.getBytes(StandardCharsets.UTF_8));
            gzipOutputStream.close();
        } catch (IOException e) {
            log.error("压缩字符串失败", e);
        } finally {
            IoUtil.close(outputStream);
        }
        byte[] compressedBytes = outputStream.toByteArray();
        return Base64.getMimeEncoder().encodeToString(compressedBytes);
    }

    // 解压缩字符串
    public static String decompress(String compressedString) {
        byte[] compressedBytes = Base64.getMimeDecoder().decode(compressedString);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(compressedBytes);
        try (GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream)) {
            byte[] buffer = new byte[1024];
            int length;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            while ((length = gzipInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            return outputStream.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("解压缩字符串失败", e);
        } finally {
            IoUtil.close(inputStream);
        }
        return null;
    }
}
