package com.nju.software.user.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nju.software.common.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
* @author musss
* @description 针对表【user_info】的数据库操作Mapper mybatis plus
* @createDate 2024-01-30 15:41:35
* @Entity generator.domain.UserInfo
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




