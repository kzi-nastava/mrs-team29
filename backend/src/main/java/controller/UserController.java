package controller;

import dto.user.*;
import org.springframework.web.bind.annotation.*;
import service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserProfileDTO getUserProfile(@PathVariable String id) {
        return userService.getUserProfile(id);
    }

    @PutMapping("/{id}")
    public UserProfileDTO updateProfile(
            @PathVariable String id,
            @RequestBody UpdateUserProfileDTO dto
    ) {
        return userService.updateProfile(id, dto);
    }
}


