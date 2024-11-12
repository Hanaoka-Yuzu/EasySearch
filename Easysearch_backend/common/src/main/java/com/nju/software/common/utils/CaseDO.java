package com.nju.software.common.utils;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nju.software.common.entity.CaseApplicableLaw;
import com.nju.software.common.entity.CaseCause;
import com.nju.software.common.entity.CaseJudiciaryMember;
import com.nju.software.common.entity.CaseLitigant;
import lombok.*;

import java.util.List;

/**
 * @Author wxy
 * @Date 2024/3/19
 **/
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseDO {
    /**
     * 案号
     */
    @JSONField(name = "case_order")
    @JsonProperty("case_order")
    private String caseOrder;

    /**
     * 案件类型（民事一审案件）
     */
    @JSONField(name = "case_category")
    @JsonProperty("case_category")
    private String caseCategory;

    /**
     * 案件类别（民事案件）
     */
    @JSONField(name = "case_type")
    @JsonProperty("case_type")
    private String caseType;

    /**
     * 审判程序（一审案件）
     */
    @JSONField(name = "judical_process")
    @JsonProperty("judical_process")
    private String judicalProcess;

    /**
     * 文书类型（判决书）
     */
    @JSONField(name = "doc_type")
    @JsonProperty("doc_type")
    private String docType;

    /**
     * 立案年度
     */
    @JSONField(name = "filing_year")
    @JsonProperty("filing_year")
    private Integer filingYear;

    /**
     * 立案法院
     */
    @JSONField(name = "court")
    @JsonProperty("court")
    private String court;

    /**
     * 法院层级
     */
    @JSONField(name = "court_level")
    @JsonProperty("court_level")
    private String courtLevel;

    /**
     * 法院行政区划（省）
     */
    @JSONField(name = "court_province")
    @JsonProperty("court_province")
    private String courtProvince;

    /**
     * 结案时间
     */
    @JSONField(name = "judgment_date")
    @JsonProperty("judgment_date")
    private String judgmentDate;

    /**
     * 结案年度
     */
    @JSONField(name = "closing_year")
    @JsonProperty("closing_year")
    private Integer closingYear;

    /**
     * 可上诉法院
     */
    @JSONField(name = "appellate_court")
    @JsonProperty("appellate_court")
    private String appellateCourt;

    /**
     * 案例全文
     */
    @JSONField(name = "text_whole")
    @JsonProperty("text_whole")
    private String textWhole;

    /**
     * 案例适用法律
     */
    @JSONField(name = "applicable_law")
    @JsonProperty("applicable_law")
    private List<CaseApplicableLaw> caseApplicableLawList;

    /**
     * 案例案由
     */
    @JSONField(name = "case_cause")
    @JsonProperty("case_cause")
    private List<CaseCause> caseCauseList;

    /**
     * 案例审判人员
     */
    @JSONField(name = "judiciary_member")
    @JsonProperty("judiciary_member")
    private List<CaseJudiciaryMember> caseJudiciaryMemberList;

    /**
     * 案例当事人
     */
    @JSONField(name = "litigant")
    @JsonProperty("litigant")
    private List<CaseLitigant> caseLitigantList;
}
