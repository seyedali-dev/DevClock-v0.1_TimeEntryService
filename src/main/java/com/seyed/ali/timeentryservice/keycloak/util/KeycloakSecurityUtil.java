package com.seyed.ali.timeentryservice.keycloak.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This is a utility class for managing security-related information from Keycloak JWT tokens.
 * <br><br>
 * <ul>
 *     <li>It provides methods to extract user roles from the JWT token and convert them into granted authorities for Spring Security.</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class KeycloakSecurityUtil {

    /**
     * Object mapper instance for converting JSON data to Java objects.
     */
    private final ObjectMapper objectMapper;

    /**
     * This method extracts a collection of granted authorities from a Keycloak JWT token.
     * <p>
     * It retrieves the roles from the {@code realm_access} field in the JWT token and maps them to {@link SimpleGrantedAuthority} objects.
     * If the 'realm_access' claim is null, it returns an empty list of authorities.
     *
     * @param jwt the Keycloak JWT token
     * @return a collection of granted authorities
     * @see . the test of this method <pre>KeycloakSecurityUtilTest#testExtractAuthorities()</pre>
     */
    @SuppressWarnings("unchecked")
    public Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        // Get the "realm_access" claim from the JWT token
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        // If the claim is null, return an empty list of authorities
        if (realmAccess == null) {
            return new ArrayList<>();
        }

        // Extract the roles from the "realm_access" claim
        Collection<String> roles = this.objectMapper.convertValue(realmAccess.get("roles"), Collection.class);

        // Create a list of granted authorities from the roles
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        return authorities;
    }

}
