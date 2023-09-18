package org.finalproject.tmeroom.auth.config.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseCookie;

@Getter
@AllArgsConstructor
public enum TokenType {

    ACCESS("accessToken", 1000L * 60 * 60),
    REFRESH("refreshToken", 1000L * 60 * 60 * 24 * 7),
    ;

    private final String name;
    private final long validTimeMilli;

    private long getValidTimeSec() {
        return validTimeMilli / 1000;
    }

    public ResponseCookie createHttpOnlyCookieFrom(String token) {
        return ResponseCookie.from(getName(), token)
                .httpOnly(true)
                .secure(true)
                .maxAge(getValidTimeSec())
                .build();
    }

}
