package com.nju.software.xmltodb.constant;

/**
 * @Description xml tag 枚举
 * @Author wxy
 * @Date 2024/3/1
 **/
public enum TagEnum {
    // 文本相关
    TEXT_WHOLE("QW", "全文"),
    TEXT_HEADER("WS", "文首"),
    TEXT_END("WW", "文尾"),
    CASE_LITIGANT("DSR", "案例当事人"),
    CASE_LITIGATION_RECORD("SSJL", "诉讼记录"),
    CASE_BASIC("AJJBQK", "案例基本情况"),
    CASE_ANALYSIS("CPFXGC", "裁判分析过程"),
    CASE_JUDGEMENT("PJJG", "判决结果"),

    // 文首相关
    CASE_ORDER("AH", "案号"),
    FILING_YEAR("LAND", "立案年度"),
    CASE_CATEGORY("AJLX", "案件类型（民事一审案件）"),
    CASE_TYPE("AJLB", "案件类别（民事案件）"),
    DOC_TYPE("WSZL", "文书类型（判决书）"),
    JUDICAL_PROCESS("SPCX", "审判程序（一审案件）"),

    // 法院相关
    COURT("JBFY", "立案法院"),
    COUR_LEVEL("FYJB", "法院级别"),
    COUR_PROVINCE("XZQH_P", "法院行政区划（省）"),
    COURT_CITY("XZQH_C", "法院行政区划（市）"),

    // 诉讼参与人相关
    LITIGANT_NAME("SSCYR", "诉讼参与人名称"),
    LITIGANT_IDENTITY("SSSF", "诉讼身份"),
    LITIGANT_CATEGORY("DSRLX", "当事人类型"),
    LITIGANT_GENDER("XB", "当事人性别"),
    LITIGANT_ETHNICITY("MZ", "当事人民族"),
    LITIGANT_NATIONALITY("GJ", "当事人国籍"),
    LITIGANT_BIRTHDAY("CSRQ", "当事人出生日期"),
    LITIGANT_ADDRESS("DSRDZ", "当事人地址"),
    LITIGANT_POSITION("ZW", "当事人职务"),
    LITIGANT_EMPLOYER_NAME("DWMC", "当事人工作单位"),
    LITIGANT_EMPLOYER_GENDER("DWXZ", "当事人工作单位性质"),
    LITIGANT_VICTIM("SFBHR", "是否被害人"),
    LITIGANT_CRIMINAL_RESPONSIBILITY("XSZRNL", "刑事责任能力"),
    LITIGANT_DEFENSE_TYPE("BHZL", "辩护种类"),
    LITIGANT_DEFENDANT("BHDX", "辩护对象"),

    // 判决相关
    CAUSE("AY", "案由"),
    CRIMINAL_CHARGE("ZM","罪名"),
    COMPLETE_CAUSE("WZAY", "完整案由"),
    COMPLETE_CRIMINAL_CHARGE("WZZM", "完整罪名"),
    CAUSE_CODE("AYDM", "案由代码"),
    CRIMINAL_CHARGDE_CODE("ZMDM", "罪名代码"),
    LAW("CUS_FLFT_RY", "法律法条"),
    APPELLATE_COURT("KSSZ", "可上诉法院"),
    APPELLATE_DEADLINE("SSQX", "上诉期限"),

    // 文尾相关
    JUDEGEMENT_DATE("CPSJ", "裁判时间"),
    CLOSING_YEAR("JAND", "结案年度"),
    JUDICIARY_MEMBER_NAME("SPRYXM", "审判人员姓名"),
    JUDICIARY_MEMBER_ROLE("SPRYJS", "审判人员角色"),
    ;


    private final String code;
    private final String message;

    TagEnum(String code, String message) {
        this.code = code;
        this.message = message;

    }

    /**
     * 根据code获取tag
     */
    public static TagEnum getTagEnum(String code) {
        TagEnum[] tagEnums = TagEnum.values();
        for (TagEnum tagEnum : tagEnums) {
            if (tagEnum.getCode().equals(code)) {
                return tagEnum;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
