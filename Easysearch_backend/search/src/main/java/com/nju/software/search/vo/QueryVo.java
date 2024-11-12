package com.nju.software.search.vo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.Map;

/**
 * @Author wxy
 * @Date 2024/3/19
 **/
@Data
public class QueryVo {
    /**
     * 页数
     */
    @Min(value = 1, message = "页数不能小于1")
    private int pageNo = 1;

    /**
     * 每页大小
     */
    @Min(value = 5, message = "每页大小不能小于5")
    @Max(value = 100, message = "每页大小不能大于100")
    private int pageSize = 5;

    /**
     * 是否按照判决日期排序
     * null: 默认按照相关度排序
     * desc: 降序
     * asc: 升序
     */
    private String sortByJudgmentDate;

    /**
     * 查询条件
     * key see {@link com.nju.software.search.service.SearchService#getQueryTerms()}
     */
    private Map<String, Object> queryMap;
}
