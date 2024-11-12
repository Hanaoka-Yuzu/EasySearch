package com.nju.software.download.feign;

import com.nju.software.common.utils.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient( name="user-service/user")
public interface FeignService {

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public Response status();
}
