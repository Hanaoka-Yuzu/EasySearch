package com.nju.software.search.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author wxy
 * @Date 2024/3/17
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("case_cause_children_node")
public class CauseNodeLink {
    /**
     * 案由代码
     */
    Integer causeCode;

    /**
     * 子案由代码
     */
    Integer childrenCauseCode;

    /**
     * 子级案由名称
     */
    String childrenCause;
}
