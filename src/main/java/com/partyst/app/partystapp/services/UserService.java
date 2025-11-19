package com.partyst.app.partystapp.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.partyst.app.partystapp.entities.User;
import com.partyst.app.partystapp.records.GenericResponse;
import com.partyst.app.partystapp.records.requests.EditUserRequest;
import com.partyst.app.partystapp.records.responses.CreateProjectResponse;
import com.partyst.app.partystapp.records.responses.DeleteUserResponse;
import com.partyst.app.partystapp.records.responses.EditUserResponse;
import com.partyst.app.partystapp.records.responses.SkillResponse;
import com.partyst.app.partystapp.records.responses.UserByIdResponse;
import com.partyst.app.partystapp.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public UserByIdResponse getUserById(Long userId) {
        try {
            User userWithSkills = userRepository.findUserWithSkills(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            return new UserByIdResponse(
                    userWithSkills.getName(),
                    userWithSkills.getLastname(),
                    userWithSkills.getNickname(),
                    userWithSkills.getCellphone(),
                    userWithSkills.getEmail(),
                    "", // password vacío por seguridad
                    userWithSkills.getSkills() != null ? userWithSkills.getSkills().stream()
                            .map(skill -> new SkillResponse(skill.getName()))
                            .collect(Collectors.toList()) : new ArrayList<>(),
                    userWithSkills.getBiography() // biography
            );

        } catch (Exception e) {
            throw new RuntimeException("Usuario no encontrado");
        }
    }

    public CreateProjectResponse updateUser(EditUserRequest request) {
        try {
            System.out.println("🔄 [USER UPDATE] Buscando usuario con email: " + request.email());

            User findedUser = userRepository.findByEmail(request.email())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            System.out.println("✅ [USER UPDATE] Usuario encontrado: " + findedUser.getName());

            // Actualizar campos
            findedUser.setName(request.name());
            findedUser.setLastname(request.lastname());
            findedUser.setCellphone(request.cellphone());
            findedUser.setNickname(request.nickname());
            findedUser.setBiography(request.biography());

            System.out.println("💾 [USER UPDATE] Guardando usuario...");
            User savedUser = userRepository.save(findedUser);

            System.out.println("✅ [USER UPDATE] Usuario actualizado: " + savedUser.getName());
            return new CreateProjectResponse(true, "Se editó el usuario");

        } catch (Exception e) {
            System.err.println("❌ [USER UPDATE ERROR] " + e.getMessage());
            e.printStackTrace();
            return new CreateProjectResponse(false, "Error al editar el usuario: " + e.getMessage());
        }
    }

    public DeleteUserResponse deleteUser(Long userId) {
    try {
        System.out.println("🗑️ [USER DELETE] Eliminando usuario ID: " + userId);
        
        User findedUser = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        System.out.println("✅ Usuario encontrado: " + findedUser.getName());
        
        // 1. ELIMINAR COLABORACIONES (esta es la relación que causa el error)
        System.out.println("🔄 Eliminando colaboraciones...");
        int colaborationsDeleted = jdbcTemplate.update(
            "DELETE FROM partyst_plastic.colaborations WHERE user_id = ?", 
            userId
        );
        System.out.println("✅ Colaboraciones eliminadas: " + colaborationsDeleted);
        
        // 2. ELIMINAR SKILLS_USER
        System.out.println("🔄 Eliminando skills del usuario...");
        int skillsDeleted = jdbcTemplate.update(
            "DELETE FROM partyst_plastic.skills_user WHERE user_id = ?", 
            userId
        );
        System.out.println("✅ Skills eliminadas: " + skillsDeleted);
        
        // 3. ELIMINAR USER_ROLES
        System.out.println("🔄 Eliminando roles del usuario...");
        int rolesDeleted = jdbcTemplate.update(
            "DELETE FROM user_roles WHERE user_id = ?", 
            userId
        );
        System.out.println("✅ Roles eliminados: " + rolesDeleted);
        
        // 4. ELIMINAR TOKENS (si existe la tabla)
        try {
            int tokensDeleted = jdbcTemplate.update(
                "DELETE FROM partyst_plastic.tokens WHERE user_id = ?", 
                userId
            );
            System.out.println("✅ Tokens eliminados: " + tokensDeleted);
        } catch (Exception e) {
            System.out.println("ℹ️ Tabla tokens no existe o no hay tokens que eliminar");
        }
        
        // 5. FINALMENTE ELIMINAR EL USUARIO
        System.out.println("💾 Eliminando usuario...");
        userRepository.delete(findedUser);
        
        System.out.println("✅ Usuario eliminado exitosamente");
        return new DeleteUserResponse(200, "Se eliminó el usuario exitosamente");
        
    } catch (RuntimeException e) {
        System.err.println("❌ [USER DELETE ERROR] Usuario no encontrado: " + e.getMessage());
        return new DeleteUserResponse(404, "Usuario no encontrado");
    } catch (Exception e) {
        System.err.println("❌ [USER DELETE ERROR] " + e.getMessage());
        e.printStackTrace();
        return new DeleteUserResponse(500, "Error al eliminar el usuario: " + e.getMessage());
    }
}

}
