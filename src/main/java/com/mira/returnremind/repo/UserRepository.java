package com.mira.returnremind.repo;

import com.mira.returnremind.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
