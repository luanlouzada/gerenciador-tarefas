package br.com.gerenciador.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.gerenciador.infrastructure.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);
}
