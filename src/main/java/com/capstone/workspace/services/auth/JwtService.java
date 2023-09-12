package com.capstone.workspace.services.auth;

import com.capstone.workspace.enums.auth.AuthErrorCode;
import com.capstone.workspace.exceptions.AppDefinedException;
import com.capstone.workspace.exceptions.InternalServerErrorException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.Jwts;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {
    @NonNull
    private ObjectMapper objectMapper;

    @Value("${aws.cognito.userPoolId}")
    private String POOL_ID;

    @Value("${aws.cognito.region}")
    private String REGION;

    private Map<String, PublicKey> publicKeys;

    private void fetchPublicCognitoSignature() {
        try {
            String cognitoJwkUrl = String.format("https://cognito-idp.%s.amazonaws.com/%s/.well-known/jwks.json", REGION, POOL_ID);
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(cognitoJwkUrl)).build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return;
            }

            JsonNode rootNode = objectMapper.readTree(response.body());
            JsonNode keys = rootNode.get("keys");

            if (keys == null || !keys.isArray()) {
                return;
            }

            publicKeys = new HashMap<>();
            for (JsonNode keyNode : keys) {
                String key = keyNode.get("kid").textValue();
                String keyType = keyNode.get("kty").textValue();
                String modulus = keyNode.get("n").textValue();
                String exponent = keyNode.get("e").textValue();

                byte[] modulusBytes = Base64.getUrlDecoder().decode(modulus);
                byte[] exponentBytes = Base64.getUrlDecoder().decode(exponent);

                BigInteger modulusBigInt = new BigInteger(1, modulusBytes);
                BigInteger exponentBigInt = new BigInteger(1, exponentBytes);

                PublicKey publicKey = KeyFactory.getInstance(keyType).generatePublic(new RSAPublicKeySpec(modulusBigInt, exponentBigInt));
                publicKeys.put(key, publicKey);
            }
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex);
        }
    }

    public Claims decode(String token) {
        try {
            JsonNode header = objectMapper.readTree(Base64.getDecoder().decode(token.substring(0, token.indexOf("."))));

            String keyId = header.get("kid").asText();
            if (keyId == null){
                return null;
            }

            if (publicKeys == null || publicKeys.isEmpty() || !publicKeys.containsKey(keyId)) {
                fetchPublicCognitoSignature();
            }

            PublicKey publicKey = publicKeys.get(keyId);
            return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody();
        } catch (InvalidClaimException | ExpiredJwtException ex) {
            throw AppDefinedException.builder().errorCode(AuthErrorCode.INVALID_ACCESS_TOKEN).build();
        } catch (Exception ex) {
            throw new InternalServerErrorException(ex);
        }
    }
}