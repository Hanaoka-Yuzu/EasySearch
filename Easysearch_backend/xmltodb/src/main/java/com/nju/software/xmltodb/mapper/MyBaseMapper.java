package com.nju.software.xmltodb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description 自定义批量插入mapper
 * @Author wxy
 * @Date 2024/3/4
 **/
public interface MyBaseMapper<T> extends BaseMapper<T> {

    /**
     * 批量插入(底层foreach实现）
     *
     * @param batchList 数据列表
     */
    int insertBatchSomeColumn(@Param("list") List<T> batchList);

    /**
     * 基于insertBatchSomeColumn的insertIgnore
     *
     * @param batchList 数据列表
     */
    int insertIgnore(@Param("list") List<T> batchList);
}