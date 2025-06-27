package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
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
        return userMapper.toDto(userRepository.save(user));
    }

    public UserDto updateUser(Long userId, UserDto user) {
        User existingUser = findUserById(userId);

        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new ConflictException("Email " + user.getEmail() + " уже используется");
            }
            existingUser.setEmail(user.getEmail());
        }
        return userMapper.toDto(userRepository.save(existingUser));
    }

    public UserDto getUser(Long userId) {
        return userMapper.toDto(findUserById(userId));
    }

    public List<UserDto> getAllUsers() {
        return new ArrayList<>(userMapper.toDto(userRepository.findAll()));
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден"));
    }
}