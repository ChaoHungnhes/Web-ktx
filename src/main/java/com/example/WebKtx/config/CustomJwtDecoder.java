package com.example.WebKtx.config;

import com.example.WebKtx.common.ErrorCode;
import com.example.WebKtx.dto.authDto.IntrospectRequest;
import com.example.WebKtx.exception.AppException;
import com.example.WebKtx.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

@Component
public class CustomJwtDecoder
        implements JwtDecoder {
    @Value("${application.security.jwt.secret-key}")
    private String signerKey;

    @Autowired
    private AuthenticationService authenticationService;

    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {

        var response = authenticationService.introspect(
//                IntrospectRequest.builder().token(token).build()
                );

        if (!response.isValid()) throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (Objects.isNull(nimbusJwtDecoder)) { // xác thực token và build
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    signerKey.getBytes(),
                    "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(
                            secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        return nimbusJwtDecoder.decode(token);
    }
}
