package com.nju.software.xmltodb.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nju.software.common.entity.CaseText;
import lombok.Data;

/**
 * @Author wxy
 * @Date 2024/3/19
 **/
@Data
@TableName("case_text")
public class CaseTextContent {
    /**
     * 案号
     */
    private String caseOrder;

    /**
     * 全文
     */
    private String textWhole;

    public CaseTextContent(CaseText caseText) {
        this.caseOrder = caseText.getCaseOrder();
        this.textWhole = caseText.getTextWhole();
    }
}
