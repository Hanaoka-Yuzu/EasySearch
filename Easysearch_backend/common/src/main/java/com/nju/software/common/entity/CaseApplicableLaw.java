package com.nju.software.common.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @Description 案例适用法律
 * @Author wxy
 * @Date 2024/2/2
 **/
@Data
public class CaseApplicableLaw {

    /**
     * 案号
     */
    @JSONField(name = "case_order")
    @JsonProperty("case_order")
    private String caseOrder;

    /**
     * 法律名称（xx法）
     */
    @JSONField(name = "name")
    @JsonProperty("name")
    private String lawName;

    /**
     * 法律条文(第xx条第xx款第xx项）
     */
    @JSONField(name = "article")
    @JsonProperty("article")
    private String lawArticle;
}
