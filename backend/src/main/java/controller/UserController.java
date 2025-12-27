package controller;

import dto.user.UserProfileDTO;
import org.springframework.web.bind.annotation.*;
import service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    //2.3 – User profile

    @GetMapping("/{id}")
    public UserProfileDTO getUserProfile(@PathVariable String id) {
        return userService.getUserProfile(id);
    }
}

