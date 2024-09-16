package com.app.taskmanagementsystemapi.service.classes;

import com.app.taskmanagementsystemapi.config.JwtService;
import com.app.taskmanagementsystemapi.dto.*;
import com.app.taskmanagementsystemapi.entity.User;
import com.app.taskmanagementsystemapi.enums.Role;
import com.app.taskmanagementsystemapi.repository.UserRepository;
import com.app.taskmanagementsystemapi.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    @Override
    public ResponseEntity<LoginResponse> register(RegisterRequest request) {

        User fromDb = userRepository.findByEmail(request.getEmail());


        if (fromDb == null) {
            var user = User.builder()
                    .firtsname(request.getFirstname())
                    .lastname(request.getLastname())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.valueOf(request.getRole()))
                    .build();

            userRepository.save(user);
            String jwtToken = jwtService.generateToken(user);
            return new ResponseEntity<>(LoginResponse.builder()
                    .token(jwtToken)
                    .message("Registered Succesfully")
                    .validUntil(
                            jwtService.extractAllClaims(jwtToken)
                                    .getExpiration()
                                    .toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate())
                    .build(), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail());
        System.out.println(user);

        String jwtToken = jwtService.generateToken(user);

        LocalDate localDate = jwtService.extractAllClaims(jwtToken)
                .getExpiration()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        System.out.println(localDate);

        return new ResponseEntity<>(LoginResponse.builder()
                .token(jwtToken)
                .message("Logged in Successfully")
                .validUntil(localDate)
                .role(user.getRole().toString())
                .build(), HttpStatus.ACCEPTED);
    }
    @Override
    public ResponseEntity<UserResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User currentUser = userRepository.findByEmail(authentication.getName());

        UserResponse userResponse = mapUserToUserResponse(currentUser);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }
    @Override
    public ResponseEntity<?> addProjectManager(AddUserRequest request) {
        return addUser(request , Role.PROJECT_MANAGER);
    }
    @Override
    public ResponseEntity<?> deleteProjectManager(String id) {
        User projectManagerFromDB = userRepository.findById(UUID.fromString(id)).orElse(null);


        if (projectManagerFromDB == null){
            return new ResponseEntity<>("Project manager doesn't exist", HttpStatus.NOT_FOUND);
        }
        else if (projectManagerFromDB.getProjectToBeManaged() != null) {
            return new ResponseEntity<>("Project manager have project assigned and can't be deleted" , HttpStatus.CONFLICT);
        }

        userRepository.delete(projectManagerFromDB);

        return new ResponseEntity<>("Project manager deleted succesfully", HttpStatus.OK);
    }
    @Override
    public ResponseEntity<?> getAllProjectManagersCreatedByAuthenticatedAdmin() {
        List<User> projectManagersFromDB = userRepository
                .getAllByRoleAndCreatorIdAndProjectToBeManagedEquals(
                        Role.PROJECT_MANAGER ,
                        UUID.fromString(getCurrentUser().getBody().getId()),
                        null
                        );

        if (projectManagersFromDB.isEmpty()){
            return new ResponseEntity<>("No project manager existing", HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(projectManagersFromDB
                .stream()
                .map(this::mapUserToUserResponse).collect(Collectors.toList()), HttpStatus.OK);
    }
    @Override
    public User getManagerById(String projectManagerId) {
        return userRepository.findById(UUID.fromString(projectManagerId)).orElse(null);
    }
    @Override
    public ResponseEntity<?> addDeveloper(AddUserRequest request) {
        return addUser(request, Role.DEVELOPER);
    }
    @Override
    public ResponseEntity<?> deleteDeveloper(String id) {
        User devToBeDeleted = userRepository.findById(UUID.fromString(id)).orElse(null);

        if (devToBeDeleted == null){
            return new ResponseEntity<>("Developer doesn't exist", HttpStatus.NO_CONTENT);
        } else if (!devToBeDeleted.getTasks().isEmpty()) {
            return new ResponseEntity<>("Developer can't be deleted because of tasks assigned", HttpStatus.CONFLICT);
        }

        userRepository.delete(devToBeDeleted);
        return new ResponseEntity<>("Developer delete succesfully", HttpStatus.OK);
    }

    @Override
    public User getDeveloperById(String id) {
        return userRepository.findById(UUID.fromString(id)).orElse(null);
    }

    @Override
    public ResponseEntity<?> getAllDevelopersCreatedByAuthenticatedManager() {
        List<User> developersFromDB = userRepository
                .getAllByRoleAndCreatorId(
                        Role.DEVELOPER ,
                        UUID.fromString(getCurrentUser().getBody().getId())
                );

        if (developersFromDB.isEmpty()){
            return new ResponseEntity<>("No project manager existing", HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(developersFromDB
                .stream()
                .map(this::mapUserToUserResponse).collect(Collectors.toList()), HttpStatus.OK);
    }

    private UserResponse mapUserToUserResponse(User user){
        if (user == null){
            return null;
        }else {
            return new UserResponse() {
                @Override
                public String getId() {
                    return user.getId().toString();
                }

                @Override
                public String getFirstname() {
                    return user.getFirtsname();
                }

                @Override
                public String getLastname() {
                    return user.getLastname();
                }

                @Override
                public String getEmail() {
                    return user.getEmail();
                }

                @Override
                public String getRole() {
                    return user.getRole().toString();
                }
            };
        }
    }
    private ResponseEntity<?> addUser(AddUserRequest request , Role role){
        User fromDb = userRepository.findByEmail(request.getEmail());

        if(fromDb != null){
            return new ResponseEntity<>("User already exists" , HttpStatus.INTERNAL_SERVER_ERROR);
        }else {
            User userToBeSaved = User.builder()
                    .firtsname(request.getFirstname())
                    .lastname(request.getLastname())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(role)
                    .build();

            userToBeSaved.setCreatorId(UUID.fromString(getCurrentUser().getBody().getId()));
            userRepository.save(userToBeSaved);

            return new ResponseEntity<>("User saved succesfully", HttpStatus.CREATED);
        }
    }
}
