package org.example.waf;

import org.example.waf.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameOrEmail(String username, String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    // ✅ 添加以下两行 ↓↓↓
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
