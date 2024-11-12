package com.nju.software.common.entity;

import lombok.Data;

import java.util.List;

/**
 * @Description 文书所有信息
 * @Author wxy
 * @Date 2024/2/29
 **/
@Data
public class CaseInfo {
    /**
     * 案号
     */
    String caseOrder;

    /**
     * 案例基本信息
     */
    private CaseBasicInfo caseBasicInfo;

    /**
     * 案例文本内容
     */
    private CaseText caseText;

    /**
     * 案例适用法律
     */
    private List<CaseApplicableLaw> caseApplicableLawList;

    /**
     * 案例案由
     */
    private List<CaseCause> caseCauseList;

    /**
     * 案例审判人员
     */
    private List<CaseJudiciaryMember> caseJudiciaryMemberList;

    /**
     * 案例当事人
     */
    private List<CaseLitigant> caseLitigantList;
}
