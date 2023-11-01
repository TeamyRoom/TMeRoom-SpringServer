package org.finalproject.tmeroom.auth.config.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseCookie;

@Getter
@AllArgsConstructor
public enum TokenType {

    ACCESS("accessToken", 1000L * 60 * 60, false),
    REFRESH("refreshToken", 1000L * 60 * 60 * 24 * 7, true),
    ;

    private final String name;
    private final long validTimeMilli;
    private final boolean isHttpOnly;

    private long getValidTimeSec() {
        return validTimeMilli / 1000;
    }

    public ResponseCookie createCookieFrom(String token) {
        return createCookieFrom(token, getValidTimeSec());
    }

    public ResponseCookie removeCookie() {
        return createCookieFrom("", 0);
    }

    public ResponseCookie createCookieFrom(String token, long validTimeSec) {
        return ResponseCookie.from(name, token)
                .httpOnly(isHttpOnly)
                .secure(true)
                .maxAge(validTimeSec)
                .path("/")
                .build();
    }
}
