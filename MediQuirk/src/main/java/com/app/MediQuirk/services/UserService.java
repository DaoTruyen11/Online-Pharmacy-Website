package com.app.MediQuirk.services;

import com.app.MediQuirk.model.Role;
import com.app.MediQuirk.model.Users;
import com.app.MediQuirk.repository.IRoleRepository;
import com.app.MediQuirk.repository.IUserRepository;
import com.app.MediQuirk.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final UserRepository usersRepository;

    public void save(@NotNull Users user) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
    }
    public Page<Users> getAllUsersPaginated(Pageable pageable) {
        return usersRepository.findAll(pageable);
    }
    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Users user = userRepository.findByUsernameOrEmailOrPhone(login, login, login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getAuthorities())
                .accountExpired(!user.isAccountNonExpired())
                .accountLocked(!user.isAccountNonLocked())
                .credentialsExpired(!user.isCredentialsNonExpired())
                .disabled(!user.isEnabled())
                .build();
    }

    public Optional<Users> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<Users> findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    public void setDefaultRole(String username, boolean isAdminCreated) {
        Optional<Users> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            String roleName = isAdminCreated ? "ADMIN" : "USER";
            Optional<Role> optionalRole = roleRepository.findByName(roleName);
            if (optionalRole.isPresent()) {
                Role defaultRole = optionalRole.get();
                user.getRoles().add(defaultRole);
                userRepository.save(user);
                log.info("Assigned default role {} to user {}", defaultRole.getName(), username);
            } else {
                log.error("Role {} not found", roleName);
            }
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }

    public Users createUserFromOAuth2(String email, String name) {
        Users user = new Users();
        user.setEmail(email);
        user.setUsername(email);
        user.setUsername(name);
        user.setPassword(new BCryptPasswordEncoder().encode("tempPassword"));

        save(user);
        setDefaultRole(user.getUsername(), false);

        return user;
    }

    public Users processOAuth2User(String email, String name) {
        Optional<Users> userOptional = findByEmail(email);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            return createUserFromOAuth2(email, name);
        }
    }

    public Users getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Users addUser(Users user) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        return usersRepository.save(user);
    }

    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    public Optional<Users> getUserById(Long id) {
        return usersRepository.findById(id);
    }

    public Users updateUser(Long id, Users userDetails) {
        Users user = usersRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id " + id));
        user.setUsername(userDetails.getUsername());
        user.setPassword(new BCryptPasswordEncoder().encode(userDetails.getPassword()));
        user.setEmail(userDetails.getEmail());
        user.setPhone(userDetails.getPhone());
        user.setIsActive(userDetails.getIsActive());
        user.setRoles(userDetails.getRoles());
        return usersRepository.save(user);
    }

    public void deleteUser(Long id) {
        Users user = usersRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id " + id));
        usersRepository.delete(user);
    }
}