package com.seyed.ali.timeentryservice.keycloak.util.converter;

import com.seyed.ali.timeentryservice.keycloak.util.KeycloakSecurityUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KeycloakJwtAuthorityConverterTest {

    //<editor-fold desc="fields">
    private @InjectMocks KeycloakJwtAuthorityConverter keycloakJwtAuthorityConverter;
    private @Mock KeycloakSecurityUtil keycloakSecurityUtil;
    //</editor-fold>

    @Test
    void convertTest() {
        // given
        Jwt jwt = mock(Jwt.class);

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("board_manager"),
                new SimpleGrantedAuthority("board_member")
        );
        when(this.keycloakSecurityUtil.extractAuthorities(isA(Jwt.class)))
                .thenReturn(authorities);

        // when
        AbstractAuthenticationToken authenticationToken = this.keycloakJwtAuthorityConverter.convert(jwt);

        // then
        assertThat(authenticationToken)
                .as("Must not be null")
                .isNotNull();
        assertThat(authenticationToken.getAuthorities())
                .as("Must have 2 authorities")
                .hasSize(2);
        assertThat(authenticationToken.getAuthorities().contains(new SimpleGrantedAuthority("board_manager")))
                .as("Must contain this role and return a true")
                .isTrue();
    }

}