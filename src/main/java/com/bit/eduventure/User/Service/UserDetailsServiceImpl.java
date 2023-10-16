package com.bit.eduventure.User.Service;


import com.bit.eduventure.User.Entity.CustomUserDetails;
import com.bit.eduventure.User.Entity.User;
import com.bit.eduventure.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> userOptional = userRepository.findByUserId(username);
        if(userOptional.isEmpty()){
            return null;
        }
        return CustomUserDetails.builder().user(userOptional.get()).build();


    }
}
