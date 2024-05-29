package com.fatec.donation.client;

import com.fatec.donation.domain.entity.CursedWord;
import com.fatec.donation.domain.entity.ResponseData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "cursedWordsService", url = "http://10.67.56.204:5000")
public interface CursedWordsService {
    @RequestMapping(method = RequestMethod.POST, value = "/api/analyze-text")
    ResponseData isWordInappropriate(@RequestBody CursedWord requestData);
}
