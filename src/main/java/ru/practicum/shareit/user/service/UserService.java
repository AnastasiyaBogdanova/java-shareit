package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto createUser(User user) {
        return userMapper.toUserDto(userRepository.createUser(user));
    }

    public UserDto updateUser(Long userId, UserDto user) {
        userRepository.findById(userId);
        return userMapper.toUserDto(userRepository.updateUser(userId, userMapper.toUser(user)));
    }

    public UserDto getUser(Long userId) {
        return userMapper.toUserDto(findUserById(userId));
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id);
    }
}