package com.nju.software.search.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
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
    void insertBatchSomeColumn(@Param("list") List<T> batchList);

    /**
     * 通过ID批量更新数据
     *
     * @param batchList 数据列表
     */
    void updateBatchById(@Param("list") List<T> batchList, @Param("ew") Wrapper<T> updateWrapper);
}