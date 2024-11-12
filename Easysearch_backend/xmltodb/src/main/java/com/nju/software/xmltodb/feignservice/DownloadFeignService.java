package com.nju.software.xmltodb.feignservice;

import com.nju.software.common.entity.CaseText;
import com.nju.software.common.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @Author wxy
 * @Date 2024/4/30
 **/
@FeignClient( name="download-service")
public interface DownloadFeignService {

    @RequestMapping(value = "/download/saveDoc", method = RequestMethod.POST)
    Result saveDocByFeign(List<CaseText> caseTextList);
}
