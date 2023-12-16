package ru.effectivemobile.tms.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.effectivemobile.tms.config.security.UserDetailsImpl;
import ru.effectivemobile.tms.persistence.entity.UserEntity;

@Service
public class UserEntityExtractor {

    public UserEntity extract(Authentication authentication) {
        return ((UserDetailsImpl) authentication.getPrincipal()).getUserEntity();
    }

}
