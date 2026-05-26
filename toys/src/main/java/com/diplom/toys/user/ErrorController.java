package com.diplom.toys.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/wrong-role")
    public String wrongRole() {
        return "wrong_role";
    }
}