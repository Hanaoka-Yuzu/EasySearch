package com.nju.software.xmltodb.utils;

import cn.hutool.db.ds.simple.SimpleDataSource;
import com.nju.software.common.entity.CaseInfo;
import com.nju.software.common.utils.CaseDO;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * @Author wxy
 * @Date 2024/4/26
 **/
@Component
public class Converter {
    public static CaseDO convert(CaseInfo caseInfo) {
        return CaseDO.builder()
                .caseOrder(caseInfo.getCaseOrder())
                .caseCategory(caseInfo.getCaseBasicInfo().getCaseCategory())
                .caseType(caseInfo.getCaseBasicInfo().getCaseType())
                .judicalProcess(caseInfo.getCaseBasicInfo().getJudicalProcess())
                .docType(caseInfo.getCaseBasicInfo().getDocType())
                .filingYear(caseInfo.getCaseBasicInfo().getFilingYear())
                .court(caseInfo.getCaseBasicInfo().getCourt())
                .courtLevel(caseInfo.getCaseBasicInfo().getCourtLevel())
                .courtProvince(caseInfo.getCaseBasicInfo().getCourtProvince())
                .judgmentDate(convertDate(caseInfo.getCaseBasicInfo().getJudgmentDate()))
                .closingYear(caseInfo.getCaseBasicInfo().getClosingYear())
                .appellateCourt(caseInfo.getCaseBasicInfo().getAppellateCourt())
                .textWhole(caseInfo.getCaseText().getTextWhole())
                .caseLitigantList(caseInfo.getCaseLitigantList())
                .caseApplicableLawList(caseInfo.getCaseApplicableLawList())
                .caseCauseList(caseInfo.getCaseCauseList())
                .caseJudiciaryMemberList(caseInfo.getCaseJudiciaryMemberList())
                .build();
    }

    private static String convertDate(Date date) {
        if(Objects.isNull(date)) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }
}
