package com.nju.software.user.controller;

import cn.dev33.satoken.stp.StpUtil;

import com.nju.software.common.utils.CodeEnum;
import com.nju.software.common.utils.Response;
import com.nju.software.user.feign.FeignService;
import com.nju.software.user.service.UserService;
import com.nju.software.user.vo.UserRegisterVO;
import com.nju.software.user.vo.UserVO;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


/**
 * @Author wxy 用户模块
 * @Date 2024/1/30
 **/
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private final UserService userService;

    @Autowired
    private final FeignService feignService;

    public UserController(UserService userService, FeignService feignService) {
        this.userService = userService;
        this.feignService = feignService;
    }


    /**
     * 测试连通性
     *
     * @return
     */
    @GetMapping("/test")
    public Response test() {
        return Response.buildSuccess();
    }

    /**
     * 测试feign调用短信服务
     *
     * @return
     */
    @GetMapping("/testSMS")
    public Response testSMS() {
        return Response.buildSuccess(feignService.test());
    }

    @GetMapping("/status")
    public Response status() {
        String loginId = (String) StpUtil.getLoginIdDefaultNull();
        if (null == loginId)
            return Response.buildFailed(CodeEnum.NOT_LOGIN.getCode(), CodeEnum.NOT_LOGIN.getMsg());
        return Response.buildSuccess(loginId);
    }

    @PostMapping("/register")
    public Response register(@Valid @RequestBody UserRegisterVO userRegisterVO, BindingResult result) {
        return userService.register(userRegisterVO, result);
    }

    @PostMapping(value = "/login")
    public Response login(@RequestBody UserVO vo, HttpSession session) {
        return userService.login(vo, session);
    }
}
