package com.nju.software.search.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author wxy
 * @Date 2024/3/15
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("case_cause_node")
public class CauseNode {
    /**
     * 案由代码
     */
    @TableId
    private Integer causeCode;

    /**
     * 案由类别
     */
    private String causeCategory;

    /**
     * 案由名称
     */
    private String cause;

    /**
     * 上级案由代码
     */
    private Integer parentCauseCode;

    /**
     * 层级
     */
    private Integer layer;


    @Override
    public String toString() {
        return "CauseNode{" +
                "causeCode=" + causeCode +
                ", causeCategory='" + causeCategory + '\'' +
                ", cause='" + cause + '\'' +
                ", parentCauseCode=" + parentCauseCode + '\'' +
                ", layer=" + layer +
                '}';
    }
}
