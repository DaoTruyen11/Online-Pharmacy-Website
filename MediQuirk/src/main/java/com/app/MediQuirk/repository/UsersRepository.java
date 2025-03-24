package com.app.MediQuirk.repository;


import com.app.MediQuirk.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Page<Users> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
}
