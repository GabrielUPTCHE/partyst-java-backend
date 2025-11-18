package com.partyst.app.partystapp.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.partyst.app.partystapp.entities.User;
import com.partyst.app.partystapp.records.requests.EditUserRequest;
import com.partyst.app.partystapp.records.responses.CreateProjectResponse;
import com.partyst.app.partystapp.records.responses.EditUserResponse;
import com.partyst.app.partystapp.records.responses.UserByIdResponse;
import com.partyst.app.partystapp.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public List<User> getAll(){
        return userRepository.findAll();
    }

    public UserByIdResponse getUserById(Long userId){
        User findedUser = userRepository.getReferenceById(userId);
        if (findedUser != null) {
            return new  UserByIdResponse(
                findedUser.getUserId(), 
                findedUser.getNickname(), 
                findedUser.getCellphone(),
                findedUser.getEmail(),
                findedUser.getLastname(),
                findedUser.getName() 
            );
        }
        return null;
    }

    public CreateProjectResponse updateUser(EditUserRequest request) {
        User findedUser = userRepository.findByEmail(request.email()).orElseThrow(null);
        findedUser.setName(request.name());
        findedUser.setLastname(request.lastname());
        findedUser.setCellphone(request.cellphone());
        findedUser.setNickname(request.nickname());
        User user = null;
        if (findedUser != null) {
            user = userRepository.save(findedUser);
            return new CreateProjectResponse(true, "Se edito el usuario");
        }
      return new CreateProjectResponse(false, "Error al editar el usuario");
    }

    public CreateProjectResponse deleteUser(Long userId) {
        User findedUser = userRepository.findById(userId).orElseThrow(null);
        if (findedUser != null) {
            userRepository.delete(findedUser);
            return new CreateProjectResponse(true, "Se elimino el usuario");
        }
      return new CreateProjectResponse(false, "Error al eliminar el usuario");
    }

}
