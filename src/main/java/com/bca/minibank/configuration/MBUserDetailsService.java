package com.bca.minibank.configuration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bca.minibank.entity.TbUsers;
import com.bca.minibank.repository.RepositoryTbUsers;


@Service
public class MBUserDetailsService implements UserDetailsService {

    @Autowired
    private RepositoryTbUsers userRepository;
 
    @Override
    public UserDetails loadUserByUsername(String username) {
        TbUsers user = userRepository.findByUsername(username);
        if (user == null) 
        {
            throw new UsernameNotFoundException(username);
        }
        return new MBUserPrincipal(user);
    }
}
