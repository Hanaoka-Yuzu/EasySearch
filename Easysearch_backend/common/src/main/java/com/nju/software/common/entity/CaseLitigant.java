package com.nju.software.common.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Description 案例当事人
 * @Author wxy
 * @Date 2024/2/2
 **/
@Data
public class CaseLitigant {
    /**
     * 案号
     */
    @JSONField(name = "case_order")
    @JsonProperty("case_order")
    private String caseOrder;

    /**
     * 诉讼参与人名称
     */
    private String name;

    /**
     * 诉讼参与人身份
     */
    private String identity;

    /**
     * 当事人类型
     */
    private String category;

    /**
     * 当事人性别
     */
    private String gender;

    /**
     * 当事人民族
     */
    private String ethnicity;

    /**
     * 当事人国籍
     */
    private String nationality;

    /**
     * 当事人出生日期
     */
    private Date birthday;

    /**
     * 当事人住址
     */
    private String address;

    /**
     * 当事人职务
     */
    private String position;

    /**
     * 当事人工作单位
     */
    @JSONField(name = "employer_name")
    @JsonProperty("employer_name")
    private String employerName;

    /**
     * 当事人工作单位性质
     */
    @JSONField(name = "employer_category")
    @JsonProperty("employer_category")
    private String employerGender;

    /**
     * 是否被害人
     */
    private Boolean isVictim;

    /**
     * 刑事责任能力
     */
    @JSONField(name = "criminal_responsibility")
    @JsonProperty("criminal_responsibility")
    private String criminalResponsibility;

    /**
     * 辩护种类
     */
    @JSONField(name = "defense_type")
    @JsonProperty("defense_type")
    private String defenseType;

    /**
     * 辩护对象
     */
    private String defendant;

}
