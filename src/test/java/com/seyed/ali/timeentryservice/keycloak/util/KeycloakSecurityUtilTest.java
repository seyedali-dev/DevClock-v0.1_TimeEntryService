package com.seyed.ali.timeentryservice.keycloak.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KeycloakSecurityUtilTest {

    /**
     * A sample JSON of Keycloak JWT token.
     *
     * <pre>
     * {
     *   "exp": 171***256,
     *   "iat": 171***956,
     *   "jti": "92d0ee53-***-0e9574c33cbd",
     *   "iss": "http://***",
     *   "aud": "account",
     *   "sub": "c3140cf9-***-3f9e99f65ea3",
     *   "typ": "Bearer",
     *   "azp": "DevVault-v2.0",
     *   "session_state": "981a0d13-***-fc50ff9d10dc",
     *   "acr": "1",
     *   "allowed-origins": [
     *     "*"
     *   ],
     *   "realm_access": {                   <-- This part is of importance for this method!
     *     "roles": [
     *       "offline_access",
     *       "uma_authorization",
     *       "default-roles-devvault-v2.0"
     *     ]
     *   },
     *   "resource_access": {
     *     "account": {
     *       "roles": [
     *         "manage-account",
     *         "manage-account-links",
     *         "view-profile"
     *       ]
     *     }
     *   },
     *   "scope": "openid profile email",
     *   "sid": "981a0d13-***-fc50ff9d10dc",
     *   "email_verified": true,
     *   "name": "default user",
     *   "preferred_username": "default",
     *   "given_name": "default",
     *   "family_name": "user",
     *   "email": "default@example.com"
     * }
     * </pre>
     */

    @Test
    void extractAuthoritiesTest() {
        // given
        Jwt jwt = mock(Jwt.class);
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", List.of("board_manager", "board_member"));

        when(jwt.getClaim("realm_access"))
                .thenReturn(realmAccess);

        // when
        KeycloakSecurityUtil keycloakSecurityUtil = new KeycloakSecurityUtil(new ObjectMapper());
        Collection<GrantedAuthority> authorities = keycloakSecurityUtil.extractAuthorities(jwt);

        // then
        assertThat(authorities)
                .as("Must extract authorities from jwt token")
                .isNotNull();
        assertThat(authorities)
                .as("Must have only two roles extracted")
                .hasSize(2);
    }

}