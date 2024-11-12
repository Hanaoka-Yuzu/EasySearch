package com.nju.software.search.service;

import com.nju.software.common.utils.Result;
import com.nju.software.search.vo.CaseDetailVo;
import com.nju.software.search.vo.QueryVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author wxy
 * @Date 2024/2/2
 **/
@Service
public interface SearchService {
    /**
     * 获取搜索建议
     *
     * @param keyword 关键词
     * @return 搜索建议
     */
    Map<String, Object> suggest(String keyword);


    /**
     * 获取查询条件
     *
     * @return 查询条件
     */
    List<String> getQueryTerms();

    /**
     * 获取文书详细信息
     *
     * @param caseOrder 案号
     * @return 文书详细信息
     */
    CaseDetailVo getCaseDetail(String caseOrder);

    /**
     * 复杂条件查询
     *
     * @param queryVo 查询条件
     * @return 查询结果
     */
    Result complexSearch(QueryVo queryVo);

    /**
     * 根据文本相似度推荐文书
     * @param caseOrder 案号
     * @return 相似文书的案号
     */
    List<String> caseRecommend(String caseOrder);

    /**
     * 法院级别文书个数统计
     *
     * @return map:
     * key: category
     * value: map
     */
    Map<String, Object> count();

    /**
     * 案由文书个数统计
     *
     * @param parentCause 父案由
     * @return map:
     * key: cause
     * value: count
     */
    Map<String, Long> countByCause(String parentCause);

    /**
     * 法院名称文书个数统计
     *
     * @param province 省份 (江苏)
     * @return map:
     * key: court
     * value: count
     */
    Map<String, Long> countByCourt(String province);
}
