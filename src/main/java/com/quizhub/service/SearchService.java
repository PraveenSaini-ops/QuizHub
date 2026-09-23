package com.quizhub.service;

import com.quizhub.dto.SearchResultDto;

public interface SearchService {
    SearchResultDto globalSearch(String query);
}
