package com.nju.software.user.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient( name="thirdParty-service/thirdParty")
public interface FeignService {

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test();
}
