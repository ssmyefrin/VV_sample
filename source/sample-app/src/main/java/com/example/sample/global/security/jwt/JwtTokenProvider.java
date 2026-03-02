package com.example.sample.global.security.jwt;

import com.example.sample.global.error.CommonException;
import com.example.sample.global.error.SampleErrorCode;
import com.example.sample.global.security.dto.Token;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private SecretKey key;
    private JwtParser jwtParser;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.jwtParser = Jwts.parser().verifyWith(key).build();
    }

    public Token generateToken(Authentication authentication) {
        // 시간기준점을 하나로 가져감
        Instant now = Instant.now();
        Instant accessExpiry = now.plusMillis(jwtProperties.getAccessTokenExpiration());
        Instant refreshExpiry = now.plusMillis(jwtProperties.getRefreshTokenExpiration());

        String accessToken = createAccessToken(authentication, Date.from(now), Date.from(accessExpiry));
        String refreshToken = createRefreshToken(Date.from(now), Date.from(refreshExpiry));

        return Token.builder()
                .grantType(JwtConstants.GRANT_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(accessExpiry.toEpochMilli())
                .build();
    }

    public String createAccessToken(Authentication authentication, Date now, Date validity) {
        String authorities = getAuthorities(authentication);

        return Jwts.builder()
                .subject(authentication.getName())
                .claim(JwtConstants.AUTHORITIES_KEY, authorities)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(Date now, Date validity) {
        return Jwts.builder()
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    /**
     * 2. 인증 정보 조회 - 님의 "DB 조회 안 하는 방식" 채용 (속도 굿!)
     * (만약 실시간 차단이 중요하다면 아까처럼 userDetailsService 쓰면 됩니다)
     */
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get(JwtConstants.AUTHORITIES_KEY) == null) {
            throw new CommonException(SampleErrorCode.TOKEN_NO_AUTHORITY);
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = Arrays
                .stream(claims.get(JwtConstants.AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        // 비밀번호는 모르니까 빈 문자열
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * 권한 GET
     * 
     * @param authentication
     * @return
     */
    private String getAuthorities(Authentication authentication) {
        if (authentication != null && ObjectUtils.isNotEmpty(authentication.getAuthorities())) {
            return authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));
        }
        return "";
    }

    /**
     * 3. 토큰 검증 - 로깅은 제 방식대로 상세하게
     */
    public void validateToken(String token) {
        try {
            jwtParser.parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
            // 만료됨 -> 4001 에러
            throw new CommonException(SampleErrorCode.TOKEN_EXPIRED);
        } catch (MalformedJwtException e) {
            // 구조가 깨짐 -> 4003 에러
            throw new CommonException(SampleErrorCode.TOKEN_MALFORMED);
        } catch (SecurityException e) { // JJWT 0.12+ 에서는 SignatureException 대신 SecurityException 사용
            // 서명 불일치 (위조) -> 4005 에러
            throw new CommonException(SampleErrorCode.TOKEN_SIGNATURE_ERROR);
        } catch (UnsupportedJwtException e) {
            throw new CommonException(SampleErrorCode.TOKEN_UNSUPPORTED);
        } catch (IllegalArgumentException e) {
            throw new CommonException(SampleErrorCode.TOKEN_INVALID);
        }
    }

    private Claims parseClaims(String accessToken) {
        try {
            return jwtParser.parseSignedClaims(accessToken).getPayload();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰이어도 클레임은 꺼내고 싶을 때 (예: 토큰 재발급 시)
            return e.getClaims();
        }
    }
}
