package com.partyst.app.partystapp.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.partyst.app.partystapp.auth.repository.Token;
import com.partyst.app.partystapp.auth.repository.TokenRepository;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;


    public Token saveToken (Token token) {
       return tokenRepository.save(token);
    }

}
