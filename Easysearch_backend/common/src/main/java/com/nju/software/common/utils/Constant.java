package com.nju.software.common.utils;

/**
 * @Description 常量
 * @Author wxy
 * @Date 2024/3/9
 **/
public class Constant {
    // Result data key
    public static final String STORE_DOC_COUNT = "storeDocCount";
    public static final String DATASET_ID = "datasetId";
    public static final String DATASET_VO_LIST = "datasetVoList";
    public static final String DATASET_VO = "datasetVo";
    public static final String COMPLEX = "complex";
    public static final String TOTAL_HIT = "totalHit";
    public static final String TOTAL_PAGE = "totalPage";
    public static final String QUERY_TERMS = "queryTerms";
    public static final String CASE_DETAIL = "caseDetail";
    public static final String RECOMMEND = "recommend";

    // elasticsearch
    public static final String INDEX = "es";

    // redis hash key
    public static final String IMPORT_KEY = "import";
    public static final String CAUSE_COUNT_KEY = "causeCount";
    public static final String CAUSE_MAX_DEPTH = "causeMaxDepth";

    // dataset status
    public static final String WAITING = "waiting";
    public static final String TRAVERSE = "traverse";
    public static final String TRAVERSE_FAILED = "traverse_failed";
    public static final String PARSE = "parse";
    public static final String PARSE_FAILED = "parse_failed";
    public static final String UPLOAD = "upload";
    public static final String UPLOAD_FAILED = "upload_failed";
    public static final String SAVE = "save";
    public static final String SAVE_FAILED = "save_failed";
    public static final String SYNC_FAILED = "sync_failed";
    public static final String FINISHED = "finished";

    // dataset type
    public static final String PATH = "path";
    public static final String ZIP = "zip";

    // import
    public static final String UNKNOWN_POSITION = "未知";
    public static final String FILE_FILTER = "__MACOSX";

    // cause
    public static final String SYNC = "sync";
    public static final String LAYER = "layer";

    // search
    public static final String KEYWORD = ".keyword";
    public static final String SUGGEST = ".suggest";
    public static final String PRESIDING_JUDGE = "审判长";
    public static final String ASSOCIATE_JUDGE = "审判员";
    public static final String LAWYER_ROLE = "律师";
    public static final String LAWYER = "lawyer";
    public static final String LAW_FIRM = "law_firm";
    public static final String JUDGE = "judge";
    public static final String LITIGANT = "litigant";
    public static final String LITIGANT_CONTEXT = "litigant_position";
    public static final String LITIGANT_NAME = "litigant.name";
    public static final String LITIGANT_EMPLOYER = "litigant.employer_name";
    public static final String COURT_LEVEL = "court_level";
    public static final String COURT_RPOVINCE = "court_province";
    public static final String COURT_CITY = "court_city";
    public static final String APPELLATE_COURT = "appellate_court";
    public static final String APPLICABLE_LAW = "applicable_law";
    public static final String CASE_ORDER = "case_order";
    public static final String CASE_TYPE = "case_type";
    public static final String CASE_CATEGORY = "case_category";
    public static final String DOC_TYPE = "doc_type";
    public static final String JUDICAL_PROCESS = "judical_process";
    public static final String JUDICIARY_MEMBER = "judiciary_member";
    public static final String JUDICIARY_MEMBER_CONTEXT = "judiciary_member_role";
    public static final String JUDICIARY_MEMBER_NAME = "judiciary_member.name";
    public static final String FILING_YEAR = "filing_year";
    public static final String CLOSING_YEAR = "closing_year";
    public static final String JUDGMENT_DATE = "judgment_date";
    public static final String TEXT_WHOLE = "text_whole";
    public static final String CASE_CAUSE = "case_cause";
    public static final String COMPLETE_CAUSE = "case_cause.complete_cause";
    public static final String COURT = "court";
    public static final String CAUSE = "cause";
    public static final String PARENT_CAUSE_CODE = "parent_cause_code";
    public static final String DESC = "desc";
    public static final String ASC = "asc";

}
