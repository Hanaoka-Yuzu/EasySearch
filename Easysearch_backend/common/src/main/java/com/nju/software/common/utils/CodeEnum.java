package com.nju.software.common.utils;

public enum CodeEnum {
    //通用错误码
    SUCCESS(200, "success"),
    NOT_LOGIN(202, "未登录"),
    FAIL(500, "fail"),
    INVALID_PARAMETER(400, "请求参数有误"),
    NOT_FOUND(404, "查找的资源不存在"),

    // 登录注册模块的错误码为10xxx 错误码
    UMS_CODE_TOO_FREQUENT_EXCEPTION(10002, "验证码获取频率太高，请稍后再试"),
    UMS_CODE_SEND_EXCEPTION(10000, "验证码发送时出错"),
    REGISTER_EXCEPTION(10001, "用户注册表单填写错误"),
    REGISTER_PHONE_EXIST_EXCEPTION(10003, "该手机号已存在"),
    REGISTER_PHONE_FORMAT_EXCEPTION(10006, "手机号格式不正确"),
    REGISTER_USERNAME_EXCEPTION(10004, "该用户名已存在"),
    LOGIN_MEMBER_NOT_FOUND_EXCEPTION(10005, "该用户不存在，可能输错了用户名或密码"),
    TOO_MANY_REQUEST(50000, "访问流量过大，请稍后尝试"),
    LOGIN_PASSWORD_ERROR(10007, "密码错误"),
    REGISTER_MESSAGE_CHECK_ERROR(10008, "短信验证码"),

    // 导入模块的错误码为20xxx 错误码
    TOO_MANY_UPLOAD_REQUEST(20001, "当前存在数据集较多，请稍后再上传！"),
    UPLOAD_FILE_NOT_FOUND(20002, "未找到本地数据集"),
    UPLOAD_FILE_EMPTY(20003, "上传文件为空"),
    UPLOAD_ZIP_FILE_EMPTY(20004, "上传压缩包内文件为空"),
    UPLOAD_FILE_NAME_EMPTY(20005, "上传文件名为空"),
    UPLOAD_ZIP_FILE_FORMAT_ERROR(20006, "上传文件格式错误（只接受.zip格式）"),
    UPLOAD_XML_FILE_FORMAT_ERROR(20007, "压缩包内文件格式错误，请修改为.xml格式后再上传"),
    UPLOAD_FILE_SIZE_EXCEED(20008, "上传文件大小超过1GB限制"),
    SQL_EXCEPTION(20009, "数据库操作失败"),
    DUPLICATE_KEY_EXCEPTION(20010, "数据库中存在相同数据"),
    DATASET_ID_EXIST(20011, "当前数据集已经成功上传!"),
    DATASET_ID_NOT_FOUND(20012, "当前数据集不存在!"),
    DATASET_ID_PROCESSING(20013, "当前数据集正在处理中,请勿重复上传!"),
    MESSAGE_EXCEPTION(20014, "消息队列出现异常"),

    // 下载模块的错误码为30xxx 错误码
    TOO_MANY_IMPORT_REQUEST(30001, "当前向文件系统请求较多，请稍后重试!"),

    // 搜索模块的错误码为40xxx 错误码
    EXCEL_FILE_EMPTY(40001, "上传的Excel文件为空"),
    EXCEL_FILE_FORMAT_ERROR(40002, "上传的Excel文件格式错误, 请上传xlsx格式文件"),
    DUPLICATE_DATA(40003, "当前数据集已经成功上传!"),
    CAUSE_NODE_NOT_FOUND(40004, "该案由不存在!"),
    CAUSE_NODE_DATA_ERROR(40005, "案由层级数据有误!"),
    KEYWORD_EMPTY(40006, "搜索关键词为空"),
    CASE_NOT_FOUND(40007, "案号不存在"),
    ;

    private final Integer code;
    private final String message;

    CodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return message;
    }
}
