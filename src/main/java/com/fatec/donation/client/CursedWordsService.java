package com.fatec.donation.client;

import com.fatec.donation.domain.entity.CursedWord;
import com.fatec.donation.domain.entity.ResponseData;
import feign.Headers;
import feign.RequestLine;

public interface CursedWordsService {
    @RequestLine("POST /api/analyze-text")
    @Headers("Content-Type: application/json")
    ResponseData postEndpointData(CursedWord requestData);
    ResponseData getEndpointData();
}
