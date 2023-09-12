package com.capstone.workspace.services.auth;

import com.capstone.workspace.dtos.user.RegisterUserDto;
import com.capstone.workspace.enums.auth.AuthErrorCode;
import com.capstone.workspace.exceptions.AppDefinedException;
import com.capstone.workspace.exceptions.InternalServerErrorException;
import com.capstone.workspace.helpers.shared.AppHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CognitoService {

    @Value("${aws.cognito.appId}")
    private String APP_ID;

    @Value("${aws.cognito.userPoolId}")
    private String POOL_ID;

    @NonNull
    private final CognitoIdentityProviderClient cognitoIdentityProviderClient;

    @NonNull
    private final HttpServletRequest httpServletRequest;

    public void registerUserByPassword(RegisterUserDto dto) throws UsernameExistsException {
        UserContextDataType userContextDataType = UserContextDataType.builder().ipAddress(getClientIp(httpServletRequest)).build();
        String username = dto.getUsername();

        String attributeName = AppHelper.isVietnamNumberPhone(username) ? "phone_number" : "email";
        String attributeValue = AppHelper.isVietnamNumberPhone(username) ? "+" + username : dto.getEmail();
        AttributeType attribute = AttributeType.builder()
                .name(attributeName)
                .value(attributeValue)
                .build();

        SignUpRequest request = SignUpRequest.builder()
                .clientId(APP_ID)
                .username(username)
                .password(dto.getPassword())
                .userAttributes(attribute)
                .userContextData(userContextDataType)
                .build();

        cognitoIdentityProviderClient.signUp(request);
    }

    public Map loginUserByPassword(String username, String password) {
        try {
            Map<String, String> authParameters = new HashMap<String, String>();
            authParameters.put("USERNAME", username);
            authParameters.put("PASSWORD", password);

            AdminInitiateAuthRequest authRequest = AdminInitiateAuthRequest.builder()
                    .authFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
                    .clientId(APP_ID)
                    .userPoolId(POOL_ID)
                    .authParameters(authParameters)
                    .build();

            AdminInitiateAuthResponse authResponse = cognitoIdentityProviderClient.adminInitiateAuth(authRequest);
            AuthenticationResultType authResult = authResponse.authenticationResult();

            if (authResult != null) {
                return Map.of(
                        "accessToken", authResult.tokenType() + " " + authResult.accessToken(),
                        "expiresIn", authResult.expiresIn(),
                        "refreshToken", authResult.refreshToken(),
                        "idToken", authResult.idToken()
                );
            }

            throw new InternalServerErrorException("Authentication failed");
        } catch (NotAuthorizedException ex) {
            throw AppDefinedException.builder().errorCode(AuthErrorCode.INVALID_CREDENTIALS).build();
        } catch (UserNotConfirmedException ex) {
            throw AppDefinedException.builder().errorCode(AuthErrorCode.USER_NOT_CONFIRMED).build();
        } catch (UserNotFoundException ex) {
            throw AppDefinedException.builder().errorCode(AuthErrorCode.USER_NOT_FOUND).build();
        }
    }

    public void verifyUser(String username, String confirmationCode) {
        UserContextDataType userContextDataType = UserContextDataType.builder().ipAddress(getClientIp(httpServletRequest)).build();

        ConfirmSignUpRequest confirmSignUpRequest = ConfirmSignUpRequest.builder()
                .clientId(APP_ID)
                .username(username)
                .confirmationCode(confirmationCode)
                .userContextData(userContextDataType)
                .build();

        try {
            cognitoIdentityProviderClient.confirmSignUp(confirmSignUpRequest);
        } catch (ExpiredCodeException ex) {
            throw AppDefinedException.builder().errorCode(AuthErrorCode.CODE_EXPIRED).build();
        } catch (CodeMismatchException ex) {
            throw AppDefinedException.builder().errorCode(AuthErrorCode.CODE_MISMATCH).build();
        } catch (LimitExceededException ex) {
            throw AppDefinedException.builder().errorCode(AuthErrorCode.VERIFICATION_LIMIT_EXCEED).build();
        }
    }

    public void logout(String username) {
        AdminUserGlobalSignOutRequest signOutRequest = AdminUserGlobalSignOutRequest.builder()
                .username(username)
                .userPoolId(POOL_ID)
                .build();

        cognitoIdentityProviderClient.adminUserGlobalSignOut(signOutRequest);
    }

    public void resendConfirmationCode(String username) {
        UserContextDataType userContextDataType = UserContextDataType.builder().ipAddress(getClientIp(httpServletRequest)).build();

        ResendConfirmationCodeRequest request = ResendConfirmationCodeRequest.builder()
                .clientId(APP_ID)
                .username(username)
                .userContextData(userContextDataType)
                .build();

        cognitoIdentityProviderClient.resendConfirmationCode(request);
    }

    private String getClientIp(HttpServletRequest request) {
        final String[] headerNames = {"X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
        String clientIp = null;

        for (String headerName: headerNames) {
            if (isValidClientIp(clientIp)) {
                break;
            }
            clientIp = request.getHeader(headerName);
        }

        return isValidClientIp(clientIp) ? clientIp : request.getRemoteAddr();
    }

    private boolean isValidClientIp(String clientIp) {
        return !(clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp));
    }
}