package org.knowm.xchange.quoine.service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.ws.rs.HeaderParam;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import org.knowm.xchange.service.BaseParamsDigest;

import net.iharder.Base64;
import si.mazi.rescu.Params;
import si.mazi.rescu.ParamsDigest;
import si.mazi.rescu.RestInvocation;
import si.mazi.rescu.SynchronizedValueFactory;

public class QuoineSignatureDigest implements ParamsDigest {

    private final JWTCreator.Builder builder;
    private final String tokenID;
    private final byte[] userSecret;
    private final SynchronizedValueFactory<Long> nonceFactory;

    public QuoineSignatureDigest(String tokenID, String userSecret, SynchronizedValueFactory<Long> nonceFactory) {
        this.tokenID = tokenID;
        this.userSecret = userSecret.getBytes();
        this.nonceFactory = nonceFactory;

        this.builder = JWT.create();
    }

    @Override
    public String digestParams(RestInvocation restInvocation) {

        String path = "/" + restInvocation.getMethodPath();

        final String sign = builder
                .withClaim("path", path)
                .withClaim("nonce", String.valueOf(nonceFactory.createValue()))
                .withClaim("token_id", tokenID)
                .sign(Algorithm.HMAC256(userSecret));

        return sign;
    }
}
