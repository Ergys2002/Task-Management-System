package com.app.taskmanagementsystemapi.config;

import com.app.taskmanagementsystemapi.entity.User;
import com.app.taskmanagementsystemapi.enums.Role;
import com.app.taskmanagementsystemapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitialDataPersistance implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
//        saveAdmin();
    }

    private void saveAdmin(){
        User admin = User.builder()
                .firtsname("Ergys")
                .lastname("Xhaollari")
                .email("ergysxhaollari02@gmail.com")
                .role(Role.ADMIN)
                .password(passwordEncoder.encode("1234"))
                .build();
        userRepository.save(admin);
    }
}
