package com.nju.software.user.service;


import com.nju.software.common.utils.Response;
import com.nju.software.user.vo.UserRegisterVO;
import com.nju.software.user.vo.UserVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户模块
 */
@Service
public interface UserService {

    /**
     * 发送短信
     *
     * @param phone 手机号
     * @return
     */
    Response sendMessageCode(@RequestParam("phone") String phone);

    /**
     * 注册
     *
     * @param
     * @param result
     * @return
     */
    Response register(UserRegisterVO userVO, BindingResult result);

    /**
     * 登录
     *
     * @param vo      登录对象
     * @param session
     * @return
     */
    Response login(UserVO vo, HttpSession session);

}
