package com.nju.software.common.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @Description 案例案由
 * @Author wxy
 * @Date 2024/3/1
 **/
@Data
public class CaseCause {
    /**
     * 案号
     */
    @JSONField(name = "case_order")
    @JsonProperty("case_order")
    private String caseOrder;

    /**
     * 案由
     */
    private String cause;

    /**
     * 完整案由
     */
    @JSONField(name = "complete_cause")
    @JsonProperty("complete_cause")
    private String completeCause;

    /**
     * 案由代码
     */
    @JSONField(name = "cause_code")
    @JsonProperty("cause_code")
    private Integer causeCode;
}
