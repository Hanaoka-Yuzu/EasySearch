package com.nju.software.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @Description 案例基本信息
 * @Author wxy
 * @Date 2024/2/2
 **/
@Data
@TableName("case_basic_info")
public class CaseBasicInfo {
    /**
     * 案例文书数据库id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 案号
     */
    private String caseOrder;

    /**
     * 案件类型（民事一审案件）
     */
    private String caseCategory;

    /**
     * 案件类别（民事案件）
     */
    private String caseType;

    /**
     * 审判程序（一审案件）
     */
    private String judicalProcess;

    /**
     * 文书类型（判决书）
     */
    private String docType;

    /**
     * 立案年度
     */
    private Integer filingYear;

    /**
     * 立案法院
     */
    private String court;

    /**
     * 法院层级
     */
    private String courtLevel;

    /**
     * 法院行政区划（省）
     */
    private String courtProvince;

    /**
     * 法院行政区划（市）
     */
    private String courtCity;

    /**
     * 结案时间
     */
    private Date judgmentDate;

    /**
     * 结案年度
     */
    private Integer closingYear;

    /**
     * 可上诉法院
     */
    private String appellateCourt;

    /**
     * 上诉期限
     */
    private String appellateDeadline;

}
