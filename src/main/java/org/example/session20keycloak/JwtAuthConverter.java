package org.example.session20keycloak;


import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthConverter implements Converter<Jwt, java.util.Collection<org.springframework.security.core.GrantedAuthority>> {
    @Override
    public java.util.Collection<org.springframework.security.core.GrantedAuthority> convert(Jwt jwt) {
        var realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null || realmAccess.isEmpty()) {
            return List.of();
        }
        var roles = (List<String>) realmAccess.get("roles");
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toList());
    }
}

