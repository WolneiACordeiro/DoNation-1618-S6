package com.fatec.donation.utils;

import com.fatec.donation.client.CursedWordsService;
import com.fatec.donation.domain.entity.CursedWord;
import com.fatec.donation.domain.entity.ResponseCursedWord;
import org.springframework.stereotype.Component;

@Component
public class CursedWordsServiceFallback implements CursedWordsService {
    @Override
    public ResponseCursedWord isWordInappropriate(CursedWord requestData) {
        return new ResponseCursedWord(false);
    }
}