package com.nju.software.common.entity;

import lombok.Data;

/**
 * @Description 案例文本内容
 * @Author wxy
 * @Date 2024/2/2
 **/
@Data
public class CaseText {

    /**
     * 案号
     */
    private String caseOrder;

    /**
     * 全文
     */
    private String textWhole;

    /**
     * 案例当事人
     */
    private String caseLitigant;

    /**
     * 诉讼记录
     */
    private String caseLitigationRecord;

    /**
     * 案例基本信息
     */
    private String caseBasic;

    /**
     * 裁判分析过程
     */
    private String caseAnalysis;

    /**
     * 判决结果
     */
    private String caseResult;

    /**
     * 文尾
     */
    private String textEnd;

    @Override
    public String toString(){
        return caseOrder+textWhole+caseLitigant +
                caseLitigationRecord+caseBasic+ caseAnalysis+
                caseResult+textEnd;
    }

}
