package com.businessassistantbcn.usermanagement.service;

import com.businessassistantbcn.usermanagement.dto.UserUuidDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import com.businessassistantbcn.usermanagement.document.User;
import com.businessassistantbcn.usermanagement.dto.UserEmailDto;
import com.businessassistantbcn.usermanagement.dto.UserDto;
import com.businessassistantbcn.usermanagement.helper.DtoHelper;
import com.businessassistantbcn.usermanagement.repository.UserManagementRepository;

@Service
public class UserManagementService implements IUserManagementService {

    @Autowired
    UserManagementRepository userRepository;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12); // Strength set as 12;


    public Mono<UserDto> addUser(UserEmailDto userEmailDto) {

        userEmailDto.setPassword(encoder.encode(userEmailDto.getPassword()));
        return userRepository.save(DtoHelper.convertToUserFromEmailDto(userEmailDto)).map(DtoHelper::convertToDto);
    }

    @Override
    public Mono<UserDto> getUserByUuid(UserUuidDto userUuidDto) {

        //QQQ -TODO
            return userRepository.findByUuid(userUuidDto.getUuid()).map( user -> DtoHelper.convertToDto(user));
    }

    public Mono<UserDto> getUserByEmail(UserEmailDto userEmailDto) {

        //QQQ -TODO
        return userRepository.findByUuid(userEmailDto.getEmail()).map( user -> DtoHelper.convertToDto(user));

    }
}