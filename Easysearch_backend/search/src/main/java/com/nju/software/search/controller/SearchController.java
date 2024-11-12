package com.nju.software.search.controller;

import com.nju.software.common.utils.Result;
import com.nju.software.search.service.SearchService;
import com.nju.software.search.vo.QueryVo;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.nju.software.common.utils.Constant.*;

/**
 * @Author wxy
 * @Date 2024/2/2
 **/
@RestController
@RequestMapping("/search")
@Validated
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/suggestion")
    public Result suggest(@NotNull String keyword) {
        return Result.ok().data(searchService.suggest(keyword));
    }

    @GetMapping("/recommend")
    public Result recommend(@NotNull String caseOrder) {
        return Result.ok().data(RECOMMEND, searchService.caseRecommend(caseOrder));
    }

    @GetMapping("/terms")
    public Result getQueryTerms() {
        return Result.ok().data(QUERY_TERMS, searchService.getQueryTerms());
    }

    @GetMapping("/detail")
    public Result getCaseDetail(@NotNull String caseOrder) {
        return Result.ok().data(CASE_DETAIL, searchService.getCaseDetail(caseOrder));
    }

    @PostMapping("/complex")
    public Result complexSearch(@RequestBody QueryVo queryVo) {
        return searchService.complexSearch(queryVo);
    }

    @GetMapping("/count")
    public Result countByCategory() {
        return Result.ok().data(searchService.count());
    }

    @GetMapping("/count/cause")
    public Result countByCause(String parentCause) {
        return Result.ok().data(CAUSE, searchService.countByCause(parentCause));
    }

    @GetMapping("/count/court")
    public Result countByCourt(@NotNull String province) {
        return Result.ok().data(COURT, searchService.countByCourt(province));
    }

}
