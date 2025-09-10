package com.sentinella.Sentinella.repository;

import com.sentinella.Sentinella.entity.UserApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserApplication, Long> {
    Optional<UserApplication> findById(Long id);
    Optional<UserApplication> findByUsername(String username);

    boolean existsByUsername(String attr0);

    boolean existsByEmail(String attr0);
}
