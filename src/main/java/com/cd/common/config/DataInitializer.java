package com.cd.common.config;

import com.cd.model.Tudor;
import com.cd.repository.TudorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private TudorRepository tudorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (tudorRepository.findByUsername("admin").isEmpty()) {
            Tudor user = new Tudor();
            user.setUsername("admin");
            //NOTE: Don't do this for prod applications!!
            user.setPassword(passwordEncoder.encode("admin"));
            tudorRepository.save(user);
        }
    }
}