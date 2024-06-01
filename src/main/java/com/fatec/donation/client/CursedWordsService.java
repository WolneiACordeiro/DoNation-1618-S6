package com.fatec.donation.client;

import com.fatec.donation.domain.entity.CursedWord;
import com.fatec.donation.domain.entity.ResponseCursedWord;
import com.fatec.donation.utils.CursedWordsServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "cursedWordsService", url = "http://127.0.0.1:8888",  fallback = CursedWordsServiceFallback.class)
public interface CursedWordsService {
    @RequestMapping(method = RequestMethod.POST, value = "/api/analyze-text")
    ResponseCursedWord isWordInappropriate(@RequestBody CursedWord requestData);
}
