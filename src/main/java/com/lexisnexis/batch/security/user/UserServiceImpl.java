package com.lexisnexis.batch.security.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static List<User> users = new ArrayList<>();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("Username not found: " + username);
        }
        return user;
    }

    private User getUserByUsername(String username) {
        if (users.isEmpty()) {
            populateUserList();
        }

        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }

        return null;
    }

    private void populateUserList() {
        users.add(new User("rbbuser1", passwordEncoder.encode("P@ssw0rd1"), "CLIENT1"));
        users.add(new User("rbbuser2", passwordEncoder.encode("P@ssw0rd1"), "CLIENT2"));
    }
}
