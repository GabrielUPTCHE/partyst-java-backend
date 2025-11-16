package com.partyst.app.partystapp.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.partyst.app.partystapp.entities.User;
import com.partyst.app.partystapp.records.GenericResponse;
import com.partyst.app.partystapp.records.requests.EditUserRequest;
import com.partyst.app.partystapp.records.responses.EditUserResponse;
import com.partyst.app.partystapp.records.responses.ForgetPasswordResponse;
import com.partyst.app.partystapp.services.UserService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/getAll")
    public List<User> getAll() {
        return userService.getAll();
    }
    
    @PostMapping("/editUser")
    public ResponseEntity<GenericResponse> postMethodName(@RequestBody EditUserRequest entity) {
        EditUserResponse updatedUser = userService.updateUser(entity);
        return ResponseEntity.ok(new GenericResponse<EditUserResponse>(201, "Usuario editado", updatedUser));
    }
    
}
