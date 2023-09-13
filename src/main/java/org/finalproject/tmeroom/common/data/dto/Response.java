package org.finalproject.tmeroom.common.data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 작성자: 김태민
 * 작성일자: 2023-09-12
 * 컨트롤러에서 응답 데이터를 담아 보낼 때 사용하는 클래스
 * @param <T> 응답 데이터가 담길 ResponseDto
 */
@Getter
@AllArgsConstructor
public class Response<T> {

    private String resultCode;
    private T result;

    public static Response<Void> error(String errorCode) {
        return new Response<>(errorCode, null);
    }

    public static <T> Response<T> error(String errorCode, T result) {
        return new Response<>(errorCode, result);
    }

    public static Response<Void> success() {
        return new Response<Void>("SUCCESS", null);
    }

    public static <T> Response<T> success(T result) {
        return new Response<>("SUCCESS", result);
    }

    public String toStream() {
        if (result == null) {
            return "{" +
                    "\"resultCode\":" + "\"" + resultCode + "\"," +
                    "\"result\":" + null + "}";
        }

        return "{" +
                "\"resultCode\":" + "\"" + resultCode + "\"," +
                "\"result\":" + "\"" + result + "\"" + "}";
    }
}
