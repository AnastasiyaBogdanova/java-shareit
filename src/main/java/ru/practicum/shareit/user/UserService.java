package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoForCreate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto createUser(UserDtoForCreate userDto) {
        User user = userMapper.toEntity(userDto);
        return userMapper.toDto(userRepository.createUser(user));
    }

    public UserDto updateUser(Long userId, UserDto user) {
        userRepository.findById(userId);
        return userMapper.toDto(userRepository.updateUser(userId, userMapper.toEntity(user)));
    }

    public UserDto getUser(Long userId) {
        return userMapper.toDto(findUserById(userId));
    }

    public List<UserDto> getAllUsers() {
        return new ArrayList<>(userMapper.toDto(userRepository.findAll()));
    }

    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден"));
    }
}