package com.example.shared.mapper;

import com.example.domain.model.User;
import com.example.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserEntityMapper {

    public UserEntity toEntity(User domain) {
        return new UserEntity(domain.getId(), domain.getUsername(), domain.getPassword(),
                domain.getRole(), domain.getCreatedAt());
    }

    public User toDomain(UserEntity entity) {
        return new User(entity.getId(), entity.getUsername(), entity.getPassword(),
                entity.getRole(), entity.getCreatedAt());
    }
}
