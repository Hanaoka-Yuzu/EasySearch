package com.nju.software.search.service.impl;

import com.nju.software.common.utils.CaseDO;
import com.nju.software.common.utils.CodeEnum;
import com.nju.software.common.utils.Result;
import com.nju.software.search.entity.CauseNode;
import com.nju.software.search.exception.SearchException;
import com.nju.software.search.mapper.CauseNodeMapper;
import com.nju.software.search.service.SearchService;
import com.nju.software.search.utils.Converter;
import com.nju.software.search.utils.ElasticSearchUtil;
import com.nju.software.search.vo.CaseBriefVo;
import com.nju.software.search.vo.CaseDetailVo;
import com.nju.software.search.vo.QueryVo;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.elasticsearch.search.suggest.completion.context.CategoryQueryContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.nju.software.common.utils.Constant.*;
import static org.apache.lucene.search.join.ScoreMode.Max;
import static org.apache.lucene.search.join.ScoreMode.None;

/**
 * @Author wxy
 * @Date 2024/2/2
 **/
@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

    private final RestHighLevelClient client;
    private final StringRedisTemplate stringRedisTemplate;
    private BoundHashOperations<String, Object, Object> causeCountHashOps;
    private final CauseNodeMapper causeNodeMapper;
    private final Converter converter;
    private final ElasticSearchUtil elasticSearchUtil;

    private static final List<String> suggestList = List.of(CASE_CATEGORY, COURT, CAUSE, JUDGE, LAWYER, LAW_FIRM);
    private static final Map<String, String> suggestMap = Map.of(
            CASE_CATEGORY, CASE_CATEGORY + SUGGEST,
            COURT, COURT + SUGGEST,
            CAUSE, COMPLETE_CAUSE + SUGGEST
    );
    private static final Map<String, String> aggregationMap = Map.of(
            COURT_LEVEL, COURT_LEVEL + KEYWORD,
            COURT_RPOVINCE, COURT_RPOVINCE + KEYWORD,
            CASE_TYPE, CASE_TYPE + KEYWORD,
            DOC_TYPE, DOC_TYPE + KEYWORD,
            JUDICAL_PROCESS, JUDICAL_PROCESS + KEYWORD,
            FILING_YEAR, FILING_YEAR,
            CLOSING_YEAR, CLOSING_YEAR
    );
    private static final List<String> queryTermList = Arrays.asList(
            CASE_ORDER, CAUSE, TEXT_WHOLE,
            LITIGANT, LAWYER, LAW_FIRM, JUDGE,
            COURT, COURT_LEVEL, COURT_RPOVINCE, COURT_CITY,
            CASE_TYPE, DOC_TYPE, CASE_CATEGORY, JUDICAL_PROCESS,
            FILING_YEAR, CLOSING_YEAR
    );
    private static final Map<String, String> queryTermMap = Map.of(
            CAUSE, COMPLETE_CAUSE,
            LITIGANT, LITIGANT_NAME,
            LAWYER, LITIGANT_NAME,
            LAW_FIRM, LITIGANT_EMPLOYER,
            JUDGE, JUDICIARY_MEMBER_NAME
    );
    private static final List<String> queryYearList = List.of(FILING_YEAR, CLOSING_YEAR);


    @Autowired
    public SearchServiceImpl(RestHighLevelClient client, StringRedisTemplate stringRedisTemplate, CauseNodeMapper causeNodeMapper, Converter converter, ElasticSearchUtil elasticSearchUtil) {
        this.client = client;
        this.stringRedisTemplate = stringRedisTemplate;
        this.causeNodeMapper = causeNodeMapper;
        this.converter = converter;
        this.elasticSearchUtil = elasticSearchUtil;
    }

    @PostConstruct
    public void initCauseCountHashOps() {
        this.causeCountHashOps = stringRedisTemplate.boundHashOps(CAUSE_COUNT_KEY);
    }


    @Override
    public Map<String, Object> suggest(String keyword) {
        // 搜索关键词不能为空
        if (StringUtils.isBlank(keyword)) {
            throw new SearchException(CodeEnum.KEYWORD_EMPTY.getMsg(), CodeEnum.KEYWORD_EMPTY.getCode());
        }

        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 添加suggest
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestMap.forEach((name, fieldName) -> addSuggestBuilder(suggestBuilder, name, fieldName, keyword));
        addContextSuggestBuilder(suggestBuilder, JUDGE, JUDICIARY_MEMBER_NAME + SUGGEST, keyword, JUDICIARY_MEMBER_CONTEXT, PRESIDING_JUDGE);
        addContextSuggestBuilder(suggestBuilder, LAWYER, LITIGANT_NAME + SUGGEST, keyword, LITIGANT_CONTEXT, LAWYER_ROLE);
        addContextSuggestBuilder(suggestBuilder, LAW_FIRM, LITIGANT_EMPLOYER + SUGGEST, keyword, LITIGANT_CONTEXT, LAWYER_ROLE);
        searchSourceBuilder.suggest(suggestBuilder);

        searchRequest.source(searchSourceBuilder);

        // 获取结果
        Map<String, Object> resultMap = new HashMap<>();
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            Suggest suggest = searchResponse.getSuggest();
            suggestList.forEach(name -> handleSuggestions(suggest, name, resultMap));
        } catch (IOException e) {
            log.error("suggest方法 查询es错误", e);
            throw new RuntimeException(e);
        }

        return resultMap;
    }

    @Override
    public List<String> getQueryTerms() {
        return queryTermList;
    }

    @Override
    public CaseDetailVo getCaseDetail(String caseOrder) {
        CaseDetailVo caseDetailVo;
        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery(CASE_ORDER, caseOrder));
        searchRequest.source(sourceBuilder);

        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            List<CaseDO> caseDOList = getCaseVoList(searchResponse);
            if (caseDOList.isEmpty()) {
                throw new SearchException(CodeEnum.CASE_NOT_FOUND.getMsg(), CodeEnum.CASE_NOT_FOUND.getCode());
            }
            caseDetailVo = converter.convertCaseDetail(caseDOList.get(0));
        } catch (IOException e) {
            log.error("getCaseDetail方法 查询es错误", e);
            throw new RuntimeException(e);
        }

        return caseDetailVo;
    }

    @Override
    public Result complexSearch(QueryVo queryVo) {
        Result result = Result.ok();
        List<CaseBriefVo> caseBriefVoList;
        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // 设置查询条件
        if (Objects.nonNull(queryVo.getQueryMap())) {
            // 全文模糊搜索
            if (queryVo.getQueryMap().containsKey(TEXT_WHOLE)) {
                boolQuery.must(QueryBuilders.matchQuery(TEXT_WHOLE, queryVo.getQueryMap().get(TEXT_WHOLE)).minimumShouldMatch("70%"));
                queryVo.getQueryMap().remove(TEXT_WHOLE);
            }
            // 其余字段精确搜索
            queryVo.getQueryMap().forEach((key, value) -> {
                if (queryTermMap.containsKey(key)) {
                    String nestedField = queryTermMap.get(key);
                    String nestedPath = elasticSearchUtil.getNestedPath(nestedField);
                    boolQuery.must(QueryBuilders.nestedQuery(
                            nestedPath, // nested 路径
                            QueryBuilders.matchQuery(nestedField + KEYWORD, value),
                            None));
                } else if (queryYearList.contains(key)) {
                    boolQuery.must(QueryBuilders.termQuery(key, value));
                } else if (CASE_ORDER.equals(key)) {
                    boolQuery.must(QueryBuilders.matchQuery(key, value));
                } else {
                    boolQuery.must(QueryBuilders.termQuery(queryTermMap.getOrDefault(key, key) + KEYWORD, value));
                }
            });
        }
        sourceBuilder.query(boolQuery);

        // 设置排序条件
        if (Objects.nonNull(queryVo.getSortByJudgmentDate())) {
            switch (queryVo.getSortByJudgmentDate()) {
                case DESC:
                    sourceBuilder.sort(SortBuilders.fieldSort(JUDGMENT_DATE).order(SortOrder.DESC));
                    break;
                case ASC:
                    sourceBuilder.sort(SortBuilders.fieldSort(JUDGMENT_DATE).order(SortOrder.ASC));
                    break;
                default:
                    break;
            }
        }

        // 设置分页
        int from = Math.max((queryVo.getPageNo() - 1) * queryVo.getPageSize(), 0);
        sourceBuilder.from(from);
        sourceBuilder.size(queryVo.getPageSize());

        searchRequest.source(sourceBuilder);

        // 执行查询
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            result.data(TOTAL_HIT, searchResponse.getHits().getTotalHits().value);
            result.data(TOTAL_PAGE, searchResponse.getHits().getTotalHits().value / queryVo.getPageSize() + 1);
            List<CaseDO> caseDOList = getCaseVoList(searchResponse);
            caseBriefVoList = caseDOList.stream().map(converter::convertCaseBrief).collect(Collectors.toList());
        } catch (IOException e) {
            log.error("complexSearch方法 查询es错误", e);
            throw new RuntimeException(e);
        }
        result.data(COMPLEX, caseBriefVoList);
        return result;
    }

    /**
     * 根据案号推荐相似案号，并且过滤掉不同的案件类型
     *
     * @param caseOrder 案号
     */
    @Override
    public List<String> caseRecommend(String caseOrder) {
        CaseDetailVo caseDetailVo = getCaseDetail(caseOrder);
        Map<String, String> coreInfo = converter.convertCaseBrief(caseDetailVo).toCoreInfo();
        List<String> caseOrderList = new ArrayList<>();
        System.out.println(coreInfo);
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (!coreInfo.isEmpty()) {
            SearchRequest searchRequest = new SearchRequest(INDEX);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            coreInfo.forEach((key, value) -> {
                if (queryTermMap.containsKey(key)) {
                    String nestedField = queryTermMap.get(key);
                    String nestedPath = elasticSearchUtil.getNestedPath(nestedField);
                    boolQuery.must(QueryBuilders.nestedQuery(
                            nestedPath, // nested 路径
                            QueryBuilders.matchQuery(nestedField + KEYWORD, value),
                            None));
                }
            });
            boolQuery.must(QueryBuilders.termQuery(CASE_TYPE, caseDetailVo.getCaseType()));
            boolQuery.must(QueryBuilders.nestedQuery(
                    CASE_CAUSE, // nested 路径
                    QueryBuilders.matchQuery(COMPLETE_CAUSE, caseDetailVo.getCaseCauseList().get(0).getCompleteCause()),
                    Max)).minimumShouldMatch("80%");
            searchSourceBuilder.query(boolQuery);
            searchSourceBuilder.size(10);
            searchSourceBuilder.sort(SortBuilders.scoreSort().order(SortOrder.DESC));
            searchRequest.source(searchSourceBuilder);

            try {
                SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
                List<CaseDO> caseDOList = getCaseVoList(searchResponse);
                caseDOList.forEach(caseDO -> {
                    if (!caseDO.getCaseOrder().equals(caseOrder)) {
                        caseOrderList.add(caseDO.getCaseOrder() + StringUtils.SPACE);
                    }
                });
            } catch (IOException e) {
                log.error("caseRecommend方法 查询es错误", e);
                throw new RuntimeException(e);
            }
        }
        return caseOrderList;
    }

    @Override
    public Map<String, Object> count() {
        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        aggregationMap.forEach((name, fieldName) -> addAggregation(searchSourceBuilder, name, fieldName));
        searchRequest.source(searchSourceBuilder);

        Map<String, Object> countMap = new HashMap<>();
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            Aggregations aggregations = searchResponse.getAggregations();
            aggregationMap.forEach((name, fieldName) -> handleAggregation(aggregations, name, countMap));
        } catch (IOException e) {
            log.error("countByCategory方法 查询es错误");
            throw new RuntimeException(e);
        }
        return countMap;
    }

    @Override
    public Map<String, Long> countByCause(String parentCause) {
        Map<String, Long> resultMap = new HashMap<>();
        List<CauseNode> causeNodeList;
        if (StringUtils.isBlank(parentCause)) {
            Map<String, Object> queryMap = new HashMap<>();
            queryMap.put(LAYER, 0);
            causeNodeList = causeNodeMapper.selectByMap(queryMap);
        } else {
            causeNodeList = getChildrenCause(parentCause);
        }
        causeNodeList.forEach(causeNode -> {
            String causeName = causeNode.getCause().strip();
            Long count = Long.parseLong((String) Objects.requireNonNull(causeCountHashOps.get(causeName)));
            resultMap.put(causeName, count);
        });
        return resultMap;
    }

    @Override
    public Map<String, Long> countByCourt(String province) {
        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(0);
        sourceBuilder.query(QueryBuilders.termQuery(COURT_RPOVINCE + KEYWORD, province));
        sourceBuilder.aggregation(AggregationBuilders.terms(COURT).field(COURT + KEYWORD));
        searchRequest.source(sourceBuilder);

        Map<String, Long> resultMap;
        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            Aggregations aggregations = response.getAggregations();
            resultMap = elasticSearchUtil.countMap(aggregations.get(COURT));
        } catch (IOException e) {
            log.error("countByCourt方法 查询es错误");
            throw new RuntimeException(e);
        }

        return resultMap;
    }


    private List<CauseNode> getChildrenCause(String parentCause) {
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put(CAUSE, parentCause);
        List<CauseNode> list = causeNodeMapper.selectByMap(queryMap);
        if (Objects.isNull(list) || list.isEmpty()) {
            throw new SearchException(CodeEnum.CAUSE_NODE_NOT_FOUND.getMsg(), CodeEnum.CAUSE_NODE_NOT_FOUND.getCode());
        }
        list = list.stream().sorted(Comparator.comparing(CauseNode::getCauseCode)).toList();
        CauseNode parentCauseNode = list.get(list.size() - 1);

        Map<String, Object> queryChildrenMap = new HashMap<>();
        queryChildrenMap.put(PARENT_CAUSE_CODE, parentCauseNode.getCauseCode());
        return causeNodeMapper.selectByMap(queryChildrenMap);
    }

    private void addSuggestBuilder(SuggestBuilder suggestBuilder, String name, String fieldName, String keyword) {
        suggestBuilder.addSuggestion(name, SuggestBuilders.completionSuggestion(fieldName).text(keyword).size(5).skipDuplicates(true));
    }

    private void addContextSuggestBuilder(SuggestBuilder suggestBuilder, String name, String fieldName, String keyword, String contextName, String contextValue) {
        List<? extends ToXContent> contextList = Collections.singletonList(CategoryQueryContext.builder().setCategory(contextValue).build());
        Map<String, List<? extends ToXContent>> contextMap = Collections.singletonMap(contextName, contextList);
        CompletionSuggestionBuilder suggestionBuilder = SuggestBuilders.completionSuggestion(fieldName)
                .prefix(keyword)
                .contexts(contextMap)
                .skipDuplicates(true);
        suggestBuilder.addSuggestion(name, suggestionBuilder);
    }

    private void handleSuggestions(Suggest suggest, String name, Map<String, Object> resultMap) {
        CompletionSuggestion suggestion = suggest.getSuggestion(name);
        List<String> suggestions = suggestion.getEntries().stream()
                .map(CompletionSuggestion.Entry::getOptions)
                .flatMap(List::stream)
                .map(CompletionSuggestion.Entry.Option::getText)
                .map(Text::string)
                .collect(Collectors.toList());
        resultMap.put(name, suggestions);
    }

    private void addAggregation(SearchSourceBuilder searchSourceBuilder, String name, String fieldName) {
        searchSourceBuilder.aggregation(AggregationBuilders.terms(name).field(fieldName));
    }

    private void handleAggregation(Aggregations aggregations, String name, Map<String, Object> resultMap) {
        Terms terms = aggregations.get(name);
        Map<String, Long> countMap = elasticSearchUtil.countMap(terms);
        resultMap.put(name, countMap);
    }

    private List<CaseDO> getCaseVoList(SearchResponse searchResponse) {
        if (Objects.isNull(searchResponse.getHits())) {
            return Collections.emptyList();
        }
        List<CaseDO> caseDOList = new ArrayList<>();
        SearchHit[] hits = searchResponse.getHits().getHits();
        Arrays.stream(hits)
                .filter(Objects::nonNull)
                .map(hit -> converter.convertCaseDetail(hit.getSourceAsMap()))
                .filter(Objects::nonNull)
                .forEach(caseDOList::add);
        return caseDOList;
    }

    private Map<String, Long> handleUnknownResult(Map<String, Long> countMap) {
        Map<String, Long> resultMap = new HashMap<>();
        AtomicLong count = new AtomicLong(0L);
        countMap.forEach((key, value) -> {
            if (key.contains("*")) {
                count.updateAndGet(v -> v + value);
            } else {
                resultMap.put(key, value);
            }
        });
        if (count.get() > 0) {
            resultMap.put("未知", count.get());
        }

        return resultMap;
    }

}
