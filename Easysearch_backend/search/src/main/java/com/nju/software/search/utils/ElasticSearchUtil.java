package com.nju.software.search.utils;

import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description ElasticSearch工具类
 * @Author wxy
 * @Date 2024/4/6
 **/
@Component
public class ElasticSearchUtil {
    public String getNestedPath(String nestedField) {
        String nestedPath = nestedField.substring(0, nestedField.lastIndexOf('.'));
        System.out.println(nestedPath);
        return nestedPath;
    }

    public Map<String, Long> countMap(Terms terms) {
        Map<String, Long> map = new HashMap<>();
        List<? extends Terms.Bucket> buckets = terms.getBuckets();
        buckets.forEach(bucket -> map.put(bucket.getKeyAsString(), bucket.getDocCount()));
        return map;
    }
}
