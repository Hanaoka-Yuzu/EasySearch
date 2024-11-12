package com.nju.software.user.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.nju.software.common.entity.IdentityEnum;
import com.nju.software.common.entity.User;
import com.nju.software.common.utils.CodeEnum;
import com.nju.software.common.utils.Response;
import com.nju.software.user.exception.UserException;
import com.nju.software.user.mapper.UserMapper;
import com.nju.software.user.service.SMSService;
import com.nju.software.user.service.UserService;
import com.nju.software.user.vo.UserRegisterVO;
import com.nju.software.user.vo.UserVO;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final StringRedisTemplate stringRedisTemplate;

    private final SMSService smsService;

    private final UserMapper userMapper;

    public static final String SMS_CODE_CACHE_PREFIX = "ums:code:";

    public static final String CODE_NUM = "random"; //todo


    private static final Pattern PATTERN = Pattern.compile("(13\\d|14[579]|15[^4\\D]|17[^49\\D]|18\\d)\\d{8}");

    @Autowired
    public UserServiceImpl(StringRedisTemplate stringRedisTemplate,@Qualifier("smsServiceImpl") SMSService smsService, UserMapper userMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.smsService = smsService;
        this.userMapper = userMapper;
    }

    @Override
    public Response sendMessageCode(String phone) {
        Matcher matcher = PATTERN.matcher(phone);
        if (!matcher.matches()) {
            return Response.buildFailed(CodeEnum.REGISTER_PHONE_FORMAT_EXCEPTION.getCode(), CodeEnum.REGISTER_PHONE_FORMAT_EXCEPTION.getMsg());
        }
        String redisCode = stringRedisTemplate.opsForValue().get(SMS_CODE_CACHE_PREFIX + phone);
//        String redisCode ="";
        //1.需要防止用户连续发送多个短信验证码
        if (!ObjectUtils.isEmpty(redisCode)) {
            //活动存入redis的时间，用当前时间减去存入redis的时间，判断用户手机号是否在60s内发送验证码
            long currentTime = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - currentTime < 60000) {
                //60s内不能再发
                return Response.buildFailed(CodeEnum.UMS_CODE_TOO_FREQUENT_EXCEPTION.getCode(), CodeEnum.UMS_CODE_TOO_FREQUENT_EXCEPTION.getMsg());
            }
        }
        //2、验证码的再次效验 redis存key为phone,value为code
        //todo 使用随机算法生成验证码 code
        String redisStorage = CODE_NUM + "_" + System.currentTimeMillis();

        // 存入redis，防止同一个手机号在60秒内再次发送验证码，该验证码的有效期为10min
        stringRedisTemplate.opsForValue().set(SMS_CODE_CACHE_PREFIX + phone, redisStorage, 10, TimeUnit.MINUTES);

        //todo 利用消息队列调用第三方服务
        return smsService.sendCode(phone, CODE_NUM);
    }

    @Override
    public Response register(UserRegisterVO userRegisterVO, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> data = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            return Response.buildFailed(CodeEnum.REGISTER_EXCEPTION.getCode(), data);
        }

        //todo 需要 redis 检验验证码，只需要把注释掉的开启就好
        String code = userRegisterVO.getCode();
        String redisCode = stringRedisTemplate.opsForValue().get(SMS_CODE_CACHE_PREFIX + userRegisterVO.getPhone());
//        if (!ObjectUtils.isEmpty(redisCode) && code.equals(redisCode.split("_")[0])) {

        //校验成功
        User user = new User();
        BeanUtils.copyProperties(userRegisterVO, user);
        user.setIdentity(IdentityEnum.fromValue(userRegisterVO.getIdentity()));
        userMapper.insert(user);

        return Response.buildSuccess();
//        }
//
//        return Response.buildFailed(CodeEnum.REGISTER_MESSAGE_CHECK_ERROR.getCode(), "短信验证码校验出错");
    }

    @Override
    public Response login(UserVO vo, HttpSession session) {

        String name = vo.getName();
        String password = vo.getPassword();
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("name", name));
        if (user == null) {
            throw new UserException(CodeEnum.LOGIN_MEMBER_NOT_FOUND_EXCEPTION);
        } else {
            String realPwd = user.getPassword();
            // todo 后续可能需要加密
//            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//            boolean matches = passwordEncoder.matches(password, realPwd);
            boolean matches = realPwd.equals(password);
            if (!matches) {
                throw new UserException(CodeEnum.LOGIN_PASSWORD_ERROR.getCode(), "密码错误");
            }
//            StpUtil.login(id) 方法利用了 Cookie 自动注入的特性，省略了手写返回 Token 的代码。
            StpUtil.login(user.getUid());
        }
        return Response.buildSuccess(user);
    }

}
