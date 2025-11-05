package com.example.WebKtx.service;

import com.example.WebKtx.dto.authDto.*;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthenticationService {
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);
    public IntrospectResponse introspect();
    public void logout();
    public AuthenticationResponse refreshToken() throws ParseException, JOSEException;

}
