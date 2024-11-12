package com.nju.software.search.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.nju.software.common.entity.CaseApplicableLaw;
import com.nju.software.common.entity.CaseCause;
import com.nju.software.common.entity.CaseJudiciaryMember;
import com.nju.software.common.entity.CaseLitigant;
import com.nju.software.common.utils.CaseDO;
import com.nju.software.search.vo.CaseBriefVo;
import com.nju.software.search.vo.CaseDetailVo;
import com.nju.software.search.vo.LawyerVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.nju.software.common.utils.Constant.*;

/**
 * @Author wxy
 * @Date 2024/3/22
 **/
@Component
public class Converter {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public CaseBriefVo convertCaseBrief(CaseDO caseDO) {
        CaseBriefVo caseBriefVo = new CaseBriefVo();
        BeanUtils.copyProperties(caseDO, caseBriefVo);
        caseBriefVo.setJudge(convertJudge(caseDO.getCaseJudiciaryMemberList()));
        caseBriefVo.setCompleteCause(convertCompleteCause(caseDO.getCaseCauseList()));
        caseBriefVo.setCaseLawFirmList(convertLawFirm(caseDO.getCaseLitigantList()));
        caseBriefVo.setCaseLawyerList(convertLawyer(caseDO.getCaseLitigantList()));
        caseBriefVo.setCaseLitigantList(convertLitigantName(caseDO.getCaseLitigantList()));
        return caseBriefVo;
    }

    public CaseBriefVo convertCaseBrief(CaseDetailVo caseDetailVo) {
        CaseBriefVo caseBriefVo = new CaseBriefVo();
        caseBriefVo.setCompleteCause(convertCompleteCause(caseDetailVo.getCaseCauseList()));
        caseBriefVo.setCourtLevel(caseDetailVo.getCourtLevel());
        caseBriefVo.setCourtProvince(caseDetailVo.getCourtProvince());
        caseBriefVo.setJudicalProcess(caseDetailVo.getJudicalProcess());
        caseBriefVo.setCaseType(caseDetailVo.getCaseType());
        caseBriefVo.setCaseCategory(caseDetailVo.getCaseCategory());
        caseBriefVo.setDocType(caseDetailVo.getDocType());
        return caseBriefVo;
    }

    public CaseDetailVo convertCaseDetail(CaseDO caseDO) {
        CaseDetailVo caseDetailVo = new CaseDetailVo();
        BeanUtils.copyProperties(caseDO, caseDetailVo);

        caseDetailVo.setJudge(convertJudge(caseDO.getCaseJudiciaryMemberList()));
        caseDetailVo.setCaseLawFirmList(convertLawFirm(caseDO.getCaseLitigantList()));
        caseDetailVo.setCaseLawyerList(convertLawyer(caseDO.getCaseLitigantList()));
        caseDetailVo.setCaseLitigantList(filterLitigant(caseDO.getCaseLitigantList()));
        return caseDetailVo;

    }

    public CaseDO convertCaseDetail(Map<String, Object> map) {
        if (Objects.isNull(map)) {
            return null;
        }

        CaseDO caseDO = CaseDO.builder()
                .caseOrder(Optional.ofNullable(map.get(CASE_ORDER)).orElse("").toString())
                .caseCategory(Optional.ofNullable(map.get(CASE_CATEGORY)).orElse("").toString())
                .caseType(Optional.ofNullable(map.get(CASE_TYPE)).orElse("").toString())
                .court(Optional.ofNullable(map.get(COURT)).orElse("").toString())
                .courtLevel(Optional.ofNullable(map.get(COURT_LEVEL)).orElse("").toString())
                .courtProvince(Optional.ofNullable(map.get(COURT_RPOVINCE)).orElse("").toString())
                .docType(Optional.ofNullable(map.get(DOC_TYPE)).orElse("").toString())
                .judicalProcess(Optional.ofNullable(map.get(JUDICAL_PROCESS)).orElse("").toString())
                .judgmentDate(Optional.ofNullable(map.get(JUDGMENT_DATE)).orElse("").toString())
                .filingYear((Integer) map.getOrDefault(FILING_YEAR, null))
                .closingYear((Integer) map.getOrDefault(CLOSING_YEAR, null))
                .textWhole(Optional.ofNullable(map.get(TEXT_WHOLE)).orElse("").toString())
                .appellateCourt(Optional.ofNullable(map.get(APPELLATE_COURT)).orElse("").toString())
                .caseCauseList(convertCause(map))
                .caseApplicableLawList(convertApplicableLaw(map))
                .caseJudiciaryMemberList(convertJudiciaryMember(map))
                .caseLitigantList(convertLitigant(map))
                .build();
        if (!caseDO.getCaseApplicableLawList().isEmpty()) {
            caseDO.setCaseOrder(caseDO.getCaseApplicableLawList().get(0).getCaseOrder());
        } else if (!caseDO.getCaseJudiciaryMemberList().isEmpty()) {
            caseDO.setCaseOrder(caseDO.getCaseCauseList().get(0).getCaseOrder());
        }
        return caseDO;
    }

    private List<CaseCause> convertCause(Map<String, Object> map) {
        if (Objects.isNull(map) || !map.containsKey(CASE_CAUSE)) {
            return Lists.newArrayList();
        }
        return objectMapper.convertValue(map.get(CASE_CAUSE), new TypeReference<>() {
        });
    }

    private List<CaseApplicableLaw> convertApplicableLaw(Map<String, Object> map) {
        if (Objects.isNull(map) || !map.containsKey(APPLICABLE_LAW)) {
            return Lists.newArrayList();
        }
        return objectMapper.convertValue(map.get(APPLICABLE_LAW), new TypeReference<>() {
        });
    }

    private List<String> convertCompleteCause(List<CaseCause> caseCauseList) {
        if (Objects.isNull(caseCauseList) || caseCauseList.isEmpty()) {
            return Lists.newArrayList();
        }
        return caseCauseList.stream()
                .map(CaseCause::getCompleteCause)
                .collect(Collectors.toList());
    }

    private List<CaseJudiciaryMember> convertJudiciaryMember(Map<String, Object> map) {
        if (Objects.isNull(map) || !map.containsKey(JUDICIARY_MEMBER)) {
            return Lists.newArrayList();
        }
        return objectMapper.convertValue(map.get(JUDICIARY_MEMBER), new TypeReference<>() {
        });
    }

    private List<CaseLitigant> convertLitigant(Map<String, Object> map) {
        if (Objects.isNull(map) || !map.containsKey(LITIGANT)) {
            return Lists.newArrayList();
        }
        return objectMapper.convertValue(map.get(LITIGANT), new TypeReference<>() {
        });

    }

    private List<String> convertJudge(List<CaseJudiciaryMember> caseJudiciaryMemberList) {
        if (Objects.isNull(caseJudiciaryMemberList) || caseJudiciaryMemberList.isEmpty()) {
            return Lists.newArrayList();
        }
        for (CaseJudiciaryMember caseJudiciaryMember : caseJudiciaryMemberList) {
            if (PRESIDING_JUDGE.equals(caseJudiciaryMember.getJudgeRole())) {
                return List.of(caseJudiciaryMember.getJudgeName());
            }
        }
        return caseJudiciaryMemberList.stream()
                .filter(caseJudiciaryMember -> ASSOCIATE_JUDGE.equals(caseJudiciaryMember.getJudgeRole()))
                .map(CaseJudiciaryMember::getJudgeName)
                .toList();
    }

    private List<String> convertLawFirm(List<CaseLitigant> caseLitigantList) {
        if (Objects.isNull(caseLitigantList) || caseLitigantList.isEmpty()) {
            return Lists.newArrayList();
        }
        List<String> lawFirmList = Lists.newArrayList();
        for (CaseLitigant caseLitigant : caseLitigantList) {
            if (LAWYER_ROLE.equals(caseLitigant.getPosition()) && caseLitigant.getEmployerName() != null) {
                lawFirmList.add(caseLitigant.getEmployerName());
            }
        }
        return lawFirmList.stream().distinct().toList();
    }

    private List<LawyerVo> convertLawyer(List<CaseLitigant> caseLitigantList) {
        List<LawyerVo> lawyerVoList = Lists.newArrayList();
        for (CaseLitigant caseLitigant : caseLitigantList) {
            if (LAWYER_ROLE.equals(caseLitigant.getPosition())) {
                LawyerVo lawyerVo = LawyerVo.builder()
                        .name(caseLitigant.getName())
                        .lawFirm(caseLitigant.getEmployerName())
                        .build();
                lawyerVoList.add(lawyerVo);
            }
        }
        return lawyerVoList;
    }

    private List<String> convertLitigantName(List<CaseLitigant> caseLitigantList) {
        if (Objects.isNull(caseLitigantList) || caseLitigantList.isEmpty()) {
            return Lists.newArrayList();
        }
        return caseLitigantList.stream()
                .filter(caseLitigant -> !LAWYER_ROLE.equals(caseLitigant.getPosition()))
                .map(CaseLitigant::getName)
                .collect(Collectors.toList());
    }

    private List<CaseLitigant> filterLitigant(List<CaseLitigant> caseLitigantList) {
        if (Objects.isNull(caseLitigantList) || caseLitigantList.isEmpty()) {
            return Lists.newArrayList();
        }
        return caseLitigantList.stream()
                .filter(caseLitigant -> LAWYER_ROLE.equals(caseLitigant.getPosition()))
                .collect(Collectors.toList());
    }


}
