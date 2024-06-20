package org.example.shortlinkgenerator.services;

import lombok.RequiredArgsConstructor;
import org.example.shortlinkgenerator.entity.User;
import org.example.shortlinkgenerator.exceptions.RegistrationException;
import org.example.shortlinkgenerator.exceptions.UserNotFoundException;
import org.example.shortlinkgenerator.reposotories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsersService {

    private final UserRepository userRepository;


    public Optional<User> findByEmail(String username) {
        return userRepository.findByEmail(username);
    }

    @Transactional
    public void save(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent())
            throw new RegistrationException("User with email " + user.getEmail() + " already exist");

        userRepository.save(user);
    }

    @Transactional
    public void delete(String email) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty())
            throw new UserNotFoundException();

        userRepository.delete(user.get());
    }
}
