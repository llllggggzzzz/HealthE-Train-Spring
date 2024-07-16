package com.conv.HealthETrain.controller;

import com.conv.HealthETrain.domain.DTO.UserDTO;
import com.conv.HealthETrain.domain.User;
import com.conv.HealthETrain.response.ApiResponse;
import com.conv.HealthETrain.service.UserService;
import lombok.AllArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/userList")
@AllArgsConstructor
public class UserListController {
    private final UserService userService;

    @GetMapping("")
    public List<User> getAllUsers(){
        List<User> userList = userService.getAllUsers();
        return userList;
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable Long userId){
        return userService.getUser(userId);
    }

    @GetMapping("/search/{username}")
    public ApiResponse<List<UserDTO>> getSearchUser(@PathVariable String username){
        List<User> userList = userService.getSearchUserList(username);
        List<UserDTO> userDTOList = userList
                .stream()
                .map(user -> new UserDTO(
                        user.getUserId(),
                        user.getAccount(),
                        user.getUsername(),
                        user.getCover()
                        ))
                .toList();
        return ApiResponse.success(userDTOList);
    }
}
