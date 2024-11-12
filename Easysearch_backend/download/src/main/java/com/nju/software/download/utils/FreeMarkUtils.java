package com.nju.software.download.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.Map;


public class FreeMarkUtils {
    private static Logger logger = LoggerFactory.getLogger(FreeMarkUtils.class);

    public static Configuration getConfiguration(){
        //创建配置实例
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);
        //设置编码
        configuration.setDefaultEncoding("utf-8");
        configuration.setClassForTemplateLoading(FreeMarkUtils.class, "/templates");//换成自己对应的目录
        return configuration;
    }

    /**
     * 获取模板字符串输入流
     * @param dataMap   参数
     * @param templateName  模板名称
     * @return
     */
    public static ByteArrayInputStream getFreemarkerContentInputStream(Map dataMap, String templateName) {
        ByteArrayInputStream in = null;
        try {
            //获取模板
            Template template = getConfiguration().getTemplate(templateName);
            StringWriter swriter = new StringWriter();
            template.process(dataMap, swriter);
            in = new ByteArrayInputStream(swriter.toString().getBytes("utf-8"));
        } catch (Exception e) {
            logger.error("模板生成错误！");
        }
        return in;
    }
}