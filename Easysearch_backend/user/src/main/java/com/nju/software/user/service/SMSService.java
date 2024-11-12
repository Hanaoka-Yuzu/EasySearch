package com.nju.software.user.service;

import com.nju.software.common.utils.Response;
import org.springframework.stereotype.Service;

/**
 * 短信模块
 */
@Service
public interface SMSService {

    Response sendCode(String phone, String code);
}
