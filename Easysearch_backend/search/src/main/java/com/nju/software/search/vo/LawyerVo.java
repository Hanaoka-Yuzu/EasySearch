package com.nju.software.search.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 律师信息
 * @Author wxy
 * @Date 2024/3/29
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LawyerVo {
    /**
     * 律师姓名
     */
    private String name;

    /**
     * 律所
     */
    private String lawFirm;
}
