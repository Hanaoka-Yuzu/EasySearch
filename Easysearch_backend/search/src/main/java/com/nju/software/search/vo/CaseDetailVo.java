package com.nju.software.search.vo;

import com.nju.software.common.entity.CaseApplicableLaw;
import com.nju.software.common.entity.CaseCause;
import com.nju.software.common.entity.CaseJudiciaryMember;
import com.nju.software.common.entity.CaseLitigant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description 文书详细信息
 * @Author wxy
 * @Date 2024/3/29
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CaseDetailVo {
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
     * 案例全文
     */
    private String textWhole;

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
     * 法官
     */
    private List<String> judge;

    /**
     * 案例当事人
     */
    private List<CaseLitigant> caseLitigantList;

    /**
     * 案例律师
     */
    private List<LawyerVo> caseLawyerList;

    /**
     * 案例律所
     */
    private List<String> caseLawFirmList;
}
