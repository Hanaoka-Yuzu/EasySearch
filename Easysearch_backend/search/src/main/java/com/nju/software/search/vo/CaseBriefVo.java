package com.nju.software.search.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @Description 文书简要信息
 * @Author wxy
 * @Date 2024/3/29
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CaseBriefVo {
    /**
     * 案号
     */
    String caseOrder;

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
     * 结案时间
     */
    private String judgmentDate;

    /**
     * 结案年度
     */
    private Integer closingYear;

    /**
     * 可上诉法院
     */
    private String appellateCourt;

    /**
     * 案例完整案由
     */
    private List<String> completeCause;

    /**
     * 法官
     */
    private List<String> judge;

    /**
     * 案例当事人
     */
    private List<String> caseLitigantList;

    /**
     * 案例律师
     */
    private List<LawyerVo> caseLawyerList;

    /**
     * 案例律所
     */
    private List<String> caseLawFirmList;

    public Map<String, String> toCoreInfo() {
        return Map.of(
                "caseCategory", caseCategory,
                "caseType", caseType,
                "judicalProcess", judicalProcess,
                "docType", docType,
                "courtLevel", courtLevel,
                "courtProvince", courtProvince,
                "completeCause", completeCause.toString()
        );
    }

}
