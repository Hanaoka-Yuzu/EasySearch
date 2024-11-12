package com.nju.software.user.service.impl;


import com.nju.software.common.utils.Response;
import com.nju.software.user.service.SMSService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Qualifier("smsServiceImpl")
public class SMSServiceImpl implements SMSService {
    @Override
    public Response sendCode(String phone, String code) {
        // TODO
        return null;
    }
}
