package com.declutter.dclutter.util;

import com.declutter.dclutter.model.User;
import com.declutter.dclutter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    @Autowired
    private UserRepository userRepository;

    // ✅ Get logged-in username (String)
    public String loggedInUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UsernameNotFoundException("No authenticated user found");
        }

        return authentication.getName();
    }

    // ✅ Get logged-in user's email (String)
    public String loggedInEmail() {
        User user = loggedInUser();
        return user.getEmail();
    }

    // ✅ Get logged-in user's ID (Long)
    public Long loggedInUserId() {
        User user = loggedInUser();
        return user.getUserId();
    }

    // ✅ Get logged-in user object (User)
    public User loggedInUser() {
        String username = loggedInUsername();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username: " + username));
    }
}