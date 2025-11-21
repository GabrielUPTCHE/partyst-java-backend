package com.partyst.app.partystapp.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.partyst.app.partystapp.entities.Project;
import com.partyst.app.partystapp.entities.Skill;
import com.partyst.app.partystapp.entities.User;
import com.partyst.app.partystapp.records.requests.ArtistFilterRequest;
import com.partyst.app.partystapp.records.requests.EditUserRequest;
import com.partyst.app.partystapp.records.responses.ArtistData;
import com.partyst.app.partystapp.records.responses.ArtistFilterData;
import com.partyst.app.partystapp.records.responses.ArtistFilterResponse;
import com.partyst.app.partystapp.records.responses.ArtistProfileData;
import com.partyst.app.partystapp.records.responses.ArtistProfileResponse;
import com.partyst.app.partystapp.records.responses.CreateProjectResponse;
import com.partyst.app.partystapp.records.responses.ProjectFilterResponse;
import com.partyst.app.partystapp.records.responses.SkillResponse;
import com.partyst.app.partystapp.records.responses.UserByIdResponse;
import com.partyst.app.partystapp.repositories.SkillRepository;
import com.partyst.app.partystapp.repositories.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SkillRepository skillRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public UserByIdResponse getUserById(Long userId) {
        User findedUser = userRepository.getReferenceById(userId);
        if (findedUser != null) {
            return new UserByIdResponse(
                    findedUser.getUserId(),
                    findedUser.getNickname(),
                    findedUser.getCellphone(),
                    findedUser.getEmail(),
                    findedUser.getLastname(),
                    findedUser.getName(),
                    findedUser.getSkills(),
                    findedUser.getBiography());
        }
        return null;
    }

    public ArtistProfileResponse getArtistProfile(Long userId) {
        try {
            User user = userRepository.getReferenceById(userId);
            if (user == null) {
                return new ArtistProfileResponse(false, "Usuario no encontrado", null);
            }
            List<ProjectFilterResponse> projects = user.getProjects().stream()
                    .map(this::mapToProjectResponse)
                    .collect(Collectors.toList());

            List<SkillResponse> skills = user.getSkills().stream()
                    .map(skill -> new SkillResponse(skill.getName()))
                    .collect(Collectors.toList());

            ArtistProfileData profileData = new ArtistProfileData(
                    user.getUserId(),
                    user.getName(),
                    user.getLastname(),
                    user.getNickname(),
                    user.getEmail(),
                    user.getBiography(),
                    skills,
                    projects);

            return new ArtistProfileResponse(true, "Perfil del artista obtenido exitosamente", profileData);
        } catch (Exception e) {
            return new ArtistProfileResponse(false, "Error al obtener el perfil del artista: " + e.getMessage(), null);
        }
    }

    public ArtistFilterResponse filterArtists(ArtistFilterRequest request) {
        try {
            List<User> allUsers = userRepository.findAll();

            List<User> filteredUsers = allUsers.stream()
                    .filter(user -> request.nickname() == null || request.nickname().isEmpty() ||
                            user.getNickname().toLowerCase().contains(request.nickname().toLowerCase()))
                    .filter(user -> {
                        if (request.skills() == null || request.skills().isEmpty()) {
                            return true;
                        }

                        List<String> userSkillNames = user.getSkills().stream()
                                .map(Skill::getName)
                                .map(String::toLowerCase)
                                .collect(Collectors.toList());

                        return request.skills().stream()
                                .anyMatch(requestSkill -> userSkillNames.contains(requestSkill.name().toLowerCase()));
                    })
                    .collect(Collectors.toList());

            List<ArtistData> artists = filteredUsers.stream()
                    .map(this::mapToArtistData)
                    .collect(Collectors.toList());

            ArtistFilterData filterData = new ArtistFilterData(artists);
            return new ArtistFilterResponse(true, "Artistas filtrados exitosamente", filterData);
        } catch (Exception e) {
            return new ArtistFilterResponse(false, "Error al filtrar artistas: " + e.getMessage(), null);
        }
    }

    private ArtistData mapToArtistData(User user) {
        List<SkillResponse> skills = user.getSkills().stream()
                .map(skill -> new SkillResponse(skill.getName()))
                .collect(Collectors.toList());

        String biography = user.getNickname() + " es un artista con experiencia en " +
                skills.stream().map(SkillResponse::name).collect(Collectors.joining(", "));

        return new ArtistData(
                user.getUserId(),
                user.getNickname(),
                biography,
                skills);
    }

    public CreateProjectResponse updateUser(EditUserRequest request) {
        User findedUser = userRepository.findByEmail(request.email()).orElseThrow(null);
        findedUser.setName(request.name());
        findedUser.setLastname(request.lastname());
        findedUser.setCellphone(request.cellphone());
        findedUser.setNickname(request.nickname());
        findedUser.setBiography(request.biography());
        if (request.skillIds() != null && !request.skillIds().isEmpty()) {

            List<Skill> skills = skillRepository.findBySkillIdIn(request.skillIds());

            Set<Skill> skillSet = new HashSet<>(skills);
            findedUser.setSkills(skillSet);
            userRepository.flush();
        } else {
            System.out.println("No se proporcionaron skills para actualizar");
        }

        if (findedUser != null) {
            userRepository.save(findedUser);
            userRepository.flush();
            return new CreateProjectResponse(true, "Se edito el usuario");
        }
        return new CreateProjectResponse(false, "Error al editar el usuario");
    }

    private ProjectFilterResponse mapToProjectResponse(Project project) {
        
        List<SkillResponse> projectSkills = project.getSkills().stream()
                .map(skill -> new SkillResponse(skill.getName()))
                .collect(Collectors.toList());

        return new ProjectFilterResponse(
                project.getProjectId().longValue(),
                project.getName(),
                project.getDescription(),
                project.getCategory() != null ? project.getCategory().getName() : "Sin categoría",
                projectSkills);
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
