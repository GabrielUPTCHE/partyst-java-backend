package com.partyst.app.partystapp.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.partyst.app.partystapp.entities.User;
import com.partyst.app.partystapp.records.requests.EditUserRequest;
import com.partyst.app.partystapp.records.responses.EditUserResponse;
import com.partyst.app.partystapp.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public List<User> getAll(){
        return userRepository.findAll();
    }

    public EditUserResponse updateUser(EditUserRequest request) {
        User findedUser = userRepository.findByEmail(request.email()).orElseThrow(null);
        findedUser.setName(request.name());
        findedUser.setLastname(request.lastname());
        findedUser.setCellphone(request.cellphone());
        User user = null;
        System.out.println("el finded " + findedUser);
        if (findedUser != null) {
            user = userRepository.save(findedUser);
        }
        return new EditUserResponse(user.getName(), user.getLastname(), user.getName(), user.getCellphone(), user.getEmail());
    }

}
