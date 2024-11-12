package com.nju.software.search.config;

/**
 * @Description 自定义sql方法枚举类
 * @Author wxy
 * @Date 2024/4/14
 **/
public enum MySqlMethod {
    UPDATE_BATCH_BY_ID("updateBatchById", "通过主键批量更新数据", "<script>UPDATE %s \n%s \nWHERE %s IN %s\n</script>");

    private final String method;
    private final String desc;
    private final String sql;

    MySqlMethod(String method, String desc, String sql) {
        this.method = method;
        this.desc = desc;
        this.sql = sql;
    }

    public String getMethod() {
        return this.method;
    }

    public String getDesc() {
        return this.desc;
    }

    public String getSql() {
        return this.sql;
    }
}
