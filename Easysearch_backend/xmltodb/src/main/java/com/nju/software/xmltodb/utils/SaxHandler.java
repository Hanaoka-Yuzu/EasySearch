package com.nju.software.xmltodb.utils;

import com.nju.software.common.entity.*;
import com.nju.software.xmltodb.constant.TagEnum;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.util.CollectionUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.ParseException;
import java.util.*;
import java.util.regex.Pattern;

import static com.nju.software.common.utils.Constant.UNKNOWN_POSITION;

/**
 * @Description Sax解析器
 * @Author wxy
 * @Date 2024/3/1
 **/
@Slf4j
@RefreshScope
public class SaxHandler extends DefaultHandler {

    private static final Integer BEGIN_YEAR = 1949;
    private static final Integer END_YEAR = 2050;
    private static final Integer CASE_ORDER_LENGTH = 100;
    private String currentTag;
    private final String serialNo;

    @Getter
    private transient CaseInfo caseInfo;

    public SaxHandler(String serialNo) {
        this.serialNo = serialNo;
    }

    @Override
    public void startDocument() {
        this.caseInfo = new CaseInfo();
        this.caseInfo.setCaseBasicInfo(new CaseBasicInfo());
    }

    @Override
    public void endDocument() {
        filterCaseCause();
        handleCaseLitigant();
        String caseOrder = getCaseOrder();
        this.caseInfo.getCaseBasicInfo().setCaseOrder(caseOrder);
        this.caseInfo.getCaseText().setCaseOrder(caseOrder);
        this.caseInfo.getCaseCauseList().forEach(e -> e.setCaseOrder(caseOrder));
        this.caseInfo.getCaseLitigantList().forEach(e -> e.setCaseOrder(caseOrder));
        this.caseInfo.getCaseApplicableLawList().forEach(e -> e.setCaseOrder(caseOrder));
        this.caseInfo.getCaseJudiciaryMemberList().forEach(e -> e.setCaseOrder(caseOrder));
        log.debug("解析文件完成: {}", this.caseInfo.getCaseOrder());
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        TagEnum tagEnum = TagEnum.getTagEnum(qName);
        if (Objects.isNull(tagEnum)) {
            return;
        }
        int index; // 数组最后一位的index
        switch (tagEnum) {
            case TEXT_WHOLE:
                CaseText caseText = new CaseText();
                String textWhole = getValue(attributes);
                caseText.setTextWhole(textWhole);
                this.caseInfo.setCaseText(caseText);
                this.caseInfo.setCaseBasicInfo(new CaseBasicInfo());
                this.caseInfo.setCaseCauseList(new ArrayList<>());
                this.caseInfo.setCaseLitigantList(new ArrayList<>());
                this.caseInfo.setCaseApplicableLawList(new ArrayList<>());
                this.caseInfo.setCaseJudiciaryMemberList(new ArrayList<>());
                break;
            case TEXT_HEADER:
                String textHeader = getValue(attributes);
                textHeader = textHeader.strip();
                this.caseInfo.setCaseOrder(textHeader);
                break;
            case TEXT_END:
                String textEnd = getValue(attributes);
                this.caseInfo.getCaseText().setTextEnd(textEnd);
                break;
            case CASE_LITIGANT:
                String textLitigant = getValue(attributes);
                this.caseInfo.getCaseText().setCaseLitigant(textLitigant);
                break;
            case CASE_LITIGATION_RECORD:
                String textLitigationRecord = getValue(attributes);
                this.caseInfo.getCaseText().setCaseLitigationRecord(textLitigationRecord);
                break;
            case CASE_BASIC:
                String caseBasic = getValue(attributes);
                this.caseInfo.getCaseText().setCaseBasic(caseBasic);
                break;
            case CASE_ANALYSIS:
                String textAnalysis = getValue(attributes);
                this.caseInfo.getCaseText().setCaseAnalysis(textAnalysis);
                break;
            case CASE_JUDGEMENT:
                String textJudgement = getValue(attributes);
                this.caseInfo.getCaseText().setCaseResult(textJudgement);
                break;
            case FILING_YEAR:
                String filingYearStr = getValue(attributes);
                try {
                    Integer filingYear = Integer.parseInt(filingYearStr);
                    if (filingYear.compareTo(BEGIN_YEAR) > 0 && filingYear.compareTo(END_YEAR) < 0) {
                        this.caseInfo.getCaseBasicInfo().setFilingYear(filingYear);
                    }
                } catch (NumberFormatException e) {
                    break;
                }
                break;
            case CASE_CATEGORY:
                String caseCategory = getValue(attributes);
                this.caseInfo.getCaseBasicInfo().setCaseCategory(caseCategory);
                break;
            case CASE_TYPE:
                String caseType = getValue(attributes);
                this.caseInfo.getCaseBasicInfo().setCaseType(caseType);
                break;
            case DOC_TYPE:
                String docType = getValue(attributes);
                this.caseInfo.getCaseBasicInfo().setDocType(docType);
                break;
            case JUDICAL_PROCESS:
                String judicalProcess = getValue(attributes);
                this.caseInfo.getCaseBasicInfo().setJudicalProcess(judicalProcess);
                break;
            case COURT:
                String court = getValue(attributes);
                this.caseInfo.getCaseBasicInfo().setCourt(court);
                break;
            case COUR_LEVEL:
                String courtLevel = getValue(attributes);
                this.caseInfo.getCaseBasicInfo().setCourtLevel(courtLevel);
                break;
            case COUR_PROVINCE:
                String courtProvince = getValue(attributes);
                this.caseInfo.getCaseBasicInfo().setCourtProvince(courtProvince);
                break;
            case COURT_CITY:
                String courtCity = getValue(attributes);
                this.caseInfo.getCaseBasicInfo().setCourtCity(courtCity);
                break;
            case LITIGANT_NAME:
                String litigantName = getValue(attributes);
                CaseLitigant caseLitigant = new CaseLitigant();
                caseLitigant.setName(litigantName);
                this.caseInfo.getCaseLitigantList().add(caseLitigant);
                break;
            case LITIGANT_IDENTITY:
                String litigantIdentity = getValue(attributes);
                index = this.caseInfo.getCaseLitigantList().size() - 1;
                this.caseInfo.getCaseLitigantList().get(index).setIdentity(litigantIdentity);
                break;
            case LITIGANT_CATEGORY:
                String litigantCategory = getValue(attributes);
                index = this.caseInfo.getCaseLitigantList().size() - 1;
                this.caseInfo.getCaseLitigantList().get(index).setCategory(litigantCategory);
                break;
            case LITIGANT_GENDER:
                String litigantGender = getValue(attributes);
                index = this.caseInfo.getCaseLitigantList().size() - 1;
                this.caseInfo.getCaseLitigantList().get(index).setGender(litigantGender);
                break;
            case LITIGANT_NATIONALITY:
                String litigantNationality = getValue(attributes);
                index = this.caseInfo.getCaseLitigantList().size() - 1;
                this.caseInfo.getCaseLitigantList().get(index).setNationality(litigantNationality);
                break;
            case LITIGANT_POSITION:
                String litigantPosition = getValue(attributes);
                index = this.caseInfo.getCaseLitigantList().size() - 1;
                this.caseInfo.getCaseLitigantList().get(index).setPosition(litigantPosition);
                break;
            case LITIGANT_ETHNICITY:
                String litigantEthnicity = getValue(attributes);
                index = this.caseInfo.getCaseLitigantList().size() - 1;
                this.caseInfo.getCaseLitigantList().get(index).setEthnicity(litigantEthnicity);
                break;
            case LITIGANT_BIRTHDAY:
                String birthdayStr = getValue(attributes);
                index = this.caseInfo.getCaseLitigantList().size() - 1;
                birthdayStr = birthdayStr
                        .replace("年", "-")
                        .replace("月", "-")
                        .replace("日", "");
                try {
                    Date birthday = DateUtils.parseDate(birthdayStr, "yyyy-MM-dd");
                    this.caseInfo.getCaseLitigantList().get(index).setBirthday(birthday);
                } catch (ParseException e) {
                    break;
                }
                break;
            case LITIGANT_ADDRESS:
                String address = getValue(attributes);
                index = this.caseInfo.getCaseLitigantList().size() - 1;
                this.caseInfo.getCaseLitigantList().get(index).setAddress(address);
                break;
            case LITIGANT_EMPLOYER_NAME:
                String employerName = getValue(attributes);
                index = this.caseInfo.getCaseLitigantList().size() - 1;
                this.caseInfo.getCaseLitigantList().get(index).setEmployerName(employerName);
                break;
            case LITIGANT_EMPLOYER_GENDER:
                String employerGender = getValue(attributes);
                index = this.caseInfo.getCaseLitigantList().size() - 1;
                this.caseInfo.getCaseLitigantList().get(index).setEmployerGender(employerGender);
                break;
            case LITIGANT_VICTIM:
                String victim = getValue(attributes);
                index = this.caseInfo.getCaseLitigantList().size() - 1;
                this.caseInfo.getCaseLitigantList().get(index).setIsVictim(Boolean.parseBoolean(victim));
                break;
            case LITIGANT_CRIMINAL_RESPONSIBILITY:
                String criminalResponsibility = getValue(attributes);
                index = this.caseInfo.getCaseLitigantList().size() - 1;
                this.caseInfo.getCaseLitigantList().get(index).setCriminalResponsibility(criminalResponsibility);
                break;
            case LITIGANT_DEFENSE_TYPE:
                String defenseType = getValue(attributes);
                index = this.caseInfo.getCaseLitigantList().size() - 1;
                this.caseInfo.getCaseLitigantList().get(index).setDefenseType(defenseType);
                break;
            case LITIGANT_DEFENDANT:
                String defendant = getValue(attributes);
                index = this.caseInfo.getCaseLitigantList().size() - 1;
                this.caseInfo.getCaseLitigantList().get(index).setDefendant(defendant);
                break;
            case CAUSE:
            case CRIMINAL_CHARGE:
                currentTag = TagEnum.CAUSE.getCode();
                String cause = getValue(attributes).strip();
                CaseCause caseCause = new CaseCause();
                caseCause.setCause(cause);
                this.caseInfo.getCaseCauseList().add(caseCause);
                break;
            case COMPLETE_CAUSE:
            case COMPLETE_CRIMINAL_CHARGE:
                if (!TagEnum.CAUSE.getCode().equals(currentTag)) {
                    break;
                }
                String completeCause = getValue(attributes).strip();
                index = this.caseInfo.getCaseCauseList().size() - 1;
                this.caseInfo.getCaseCauseList().get(index).setCompleteCause(completeCause);
                break;
            case CAUSE_CODE:
            case CRIMINAL_CHARGDE_CODE:
                if (!TagEnum.CAUSE.getCode().equals(currentTag)) {
                    break;
                }
                String causeCodeStr = getValue(attributes);
                index = this.caseInfo.getCaseCauseList().size() - 1;
                try {
                    Integer causeCode = Integer.parseInt(causeCodeStr);
                    this.caseInfo.getCaseCauseList().get(index).setCauseCode(causeCode);
                    break;
                } catch (NumberFormatException e) {
                    break;
                }
            case LAW:
                if (attributes.getLength() == 1) {
                    break;
                }
                String law = getValue(attributes);
                if (law.split("》").length != 2) {
                    break;
                }
                String lawName = law.split("》")[0] + "》";
                String lawArticle = law.split("》")[1];
                CaseApplicableLaw applicableLaw = new CaseApplicableLaw();
                applicableLaw.setLawName(lawName);
                applicableLaw.setLawArticle(lawArticle);
                this.caseInfo.getCaseApplicableLawList().add(applicableLaw);
                break;
            case APPELLATE_COURT:
                String appellateCourt = getValue(attributes);
                this.caseInfo.getCaseBasicInfo().setAppellateCourt(appellateCourt);
                break;
            case APPELLATE_DEADLINE:
                String appellateDeadline = getValue(attributes);
                this.caseInfo.getCaseBasicInfo().setAppellateDeadline(appellateDeadline);
                break;
            case JUDEGEMENT_DATE:
                String judgmentDateStr = getValue(attributes)
                        .replace("年", "-")
                        .replace("月", "-")
                        .replace("日", "");
                try {
                    Date judgmentDate = DateUtils.parseDate(judgmentDateStr, "yyyy-MM-dd");
                    this.caseInfo.getCaseBasicInfo().setJudgmentDate(judgmentDate);
                } catch (ParseException e) {
                    break;
                }
                break;
            case CLOSING_YEAR:
                String closingYearStr = getValue(attributes);
                try {
                    Integer closingYear = Integer.parseInt(closingYearStr);
                    this.caseInfo.getCaseBasicInfo().setClosingYear(closingYear);
                } catch (NumberFormatException e) {
                    break;
                }
                break;

            case JUDICIARY_MEMBER_NAME:
                String judiciaryMemberName = getValue(attributes);
                CaseJudiciaryMember judiciaryMember = new CaseJudiciaryMember();
                judiciaryMember.setJudgeName(judiciaryMemberName);
                this.caseInfo.getCaseJudiciaryMemberList().add(judiciaryMember);
                break;
            case JUDICIARY_MEMBER_ROLE:
                String judiciaryMemberRole = getValue(attributes);
                index = this.caseInfo.getCaseJudiciaryMemberList().size() - 1;
                this.caseInfo.getCaseJudiciaryMemberList().get(index).setJudgeRole(judiciaryMemberRole);
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        TagEnum tagEnum = TagEnum.getTagEnum(qName);
        if (TagEnum.CAUSE.equals(tagEnum)) {
            currentTag = "";
        }
    }

    @Override
    public void error(SAXParseException e) {
        log.error("SAXParseException:", e);
    }

    private String getValue(Attributes attributes) {
        return attributes.getValue(1) == null ? "" : attributes.getValue(1);
    }

    private void filterCaseCause() {
        Set<String> caseCauseSet = new HashSet<>();
        List<CaseCause> caseCauseList = new ArrayList<>();
        this.caseInfo.getCaseCauseList().forEach(e -> {
            if (!caseCauseSet.contains(e.getCause()) && Objects.nonNull(e.getCauseCode())) {
                caseCauseSet.add(e.getCause());
                caseCauseList.add(e);
            }
        });
        this.caseInfo.setCaseCauseList(caseCauseList);
    }

    private void handleCaseLitigant() {
        if (CollectionUtils.isEmpty(this.caseInfo.getCaseLitigantList())) {
            return;
        }
        this.caseInfo.getCaseLitigantList().forEach(caseLitigant -> {
            if (StringUtils.isBlank(caseLitigant.getPosition())) {
                caseLitigant.setPosition(UNKNOWN_POSITION);
            }
        });
    }

    private String getCaseOrder() {
        String caseOrder = this.caseInfo.getCaseOrder();
        if (caseOrder.contains("*")) {
            String caseCategory = this.caseInfo.getCaseBasicInfo().getCaseCategory();
            caseOrder += caseCategory + serialNo;
        }
        caseOrder = caseOrder.strip();
        if (caseOrder.length() > CASE_ORDER_LENGTH) {
            caseOrder = caseOrder.substring(0, CASE_ORDER_LENGTH);
        }
        if (StringUtils.isBlank(caseOrder) || containsEnglish(caseOrder)) {
            return null;
        }
        return caseOrder;
    }


    private static boolean containsEnglish(String str) {
        Pattern pattern = Pattern.compile("[a-zA-Z]");
        return pattern.matcher(str).find();
    }


}

