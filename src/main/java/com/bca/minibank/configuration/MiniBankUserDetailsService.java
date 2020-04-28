package com.bca.minibank.configuration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bca.minibank.entity.TbUsers;
import com.bca.minibank.repository.RepositoryTbUsers;
import com.bca.minibank.dao.DaoTbUsers;

@Service
public class MiniBankUserDetailsService implements UserDetailsService {

    @Autowired
    private RepositoryTbUsers userRepository;
 
    @Override
    public UserDetails loadUserByUsername(String username) {
        TbUsers user = userRepository.findByUsername(username);
        if (user == null) 
        {
            throw new UsernameNotFoundException(username);
        }
        return new MyUserPrincipal(user);
    }
 
//    @Override
//    public User registerNewUserAccount(UserDto accountDto) throws EmailExistsException {
//      
//        if (emailExist(accountDto.getEmail())) {
//            throw new EmailExistsException
//              ("There is an account with that email address: " + accountDto.getEmail());
//        }
//        User user = new User();
//     
//        user.setFirstName(accountDto.getFirstName());
//        user.setLastName(accountDto.getLastName());
//        user.setPassword(passwordEncoder.encode(accountDto.getPassword()));
//        user.setEmail(accountDto.getEmail());
//     
//        user.setRoles(Arrays.asList(roleRepository.findByName("ROLE_USER")));
//        return repository.save(user);
//    }
}
