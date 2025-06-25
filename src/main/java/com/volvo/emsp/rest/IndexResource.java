package com.volvo.emsp.rest;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Hidden // Hide from Swagger, it is required when use @RestController, here is not needed, but I set it
public class IndexResource {

    @GetMapping("/")
    public String rootToUserDocs() {
        return "redirect:/index.html";
    }

    @GetMapping("/index")
    public String adminDocs() {
        return "redirect:/index.html";
    }

}
