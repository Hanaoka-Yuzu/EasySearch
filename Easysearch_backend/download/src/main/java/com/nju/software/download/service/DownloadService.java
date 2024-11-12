package com.nju.software.download.service;

import com.nju.software.common.entity.CaseText;
import com.nju.software.common.utils.Result;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DownloadService {

    Integer storeDoc(List<CaseText> caseTextList);

    void downloadDoc(HttpServletResponse response,String fileName, String userID);

    void batchDownloadDoc(HttpServletResponse response,List<String> caseOrders,String userID);
}
