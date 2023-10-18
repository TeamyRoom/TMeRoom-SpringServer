package org.finalproject.tmeroom.admin.controller;

import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.common.data.dto.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class EchoController {

    @GetMapping("/echo")
    public Response<Void> test() {
        return Response.success();
    }
}
