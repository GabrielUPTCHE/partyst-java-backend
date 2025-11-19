package com.partyst.app.partystapp.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.partyst.app.partystapp.entities.User;
import com.partyst.app.partystapp.records.GenericResponse;
import com.partyst.app.partystapp.records.requests.EditUserRequest;
import com.partyst.app.partystapp.records.responses.CreateProjectResponse;
import com.partyst.app.partystapp.records.responses.DeleteUserResponse;
import com.partyst.app.partystapp.records.responses.EditUserResponse;
import com.partyst.app.partystapp.records.responses.UserByIdResponse;
import com.partyst.app.partystapp.services.UserService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;





@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/getAll")
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<GenericResponse> getUserById(@PathVariable Long userId) {
        UserByIdResponse findedUser = userService.getUserById(userId);
        return ResponseEntity.ok(new GenericResponse<UserByIdResponse>(201, "Usuario encontrado", findedUser));
    }
    
    
    @PutMapping("/update")
    public ResponseEntity<GenericResponse> updateUser(@RequestBody EditUserRequest entity) {
        CreateProjectResponse updatedUser = userService.updateUser(entity);
        return ResponseEntity.ok(new GenericResponse<CreateProjectResponse>(201, "Usuario editado", updatedUser));
    }

    @DeleteMapping("/{userId}/delete")
    public ResponseEntity<DeleteUserResponse> deleteUser(@PathVariable Long userId){
        DeleteUserResponse deletedUser = userService.deleteUser(userId);
        return ResponseEntity.ok(deletedUser);
    }
    
}
