package com.example.demo.src.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findByEmail(String Email);

    public Long countByEmail(String Email);
}