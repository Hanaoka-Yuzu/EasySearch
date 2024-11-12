package com.nju.software.common.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @Description 案例审判人员
 * @Author wxy
 * @Date 2024/2/2
 **/
@Data
public class CaseJudiciaryMember {

    /**
     * 案号
     */
    @JSONField(name = "case_order")
    @JsonProperty("case_order")
    private String caseOrder;

    /**
     * 审判人员姓名
     */
    @JSONField(name = "name")
    @JsonProperty("name")
    private String judgeName;

    /**
     * 审判人员角色
     */
    @JSONField(name = "role")
    @JsonProperty("role")
    private String judgeRole;
}
