package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoForCreate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;
    private UserDtoForCreate userDtoForCreate;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@ya.ru");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Test User");
        userDto.setEmail("test@ya.ru");

        userDtoForCreate = new UserDtoForCreate();
        userDtoForCreate.setName("Test User");
        userDtoForCreate.setEmail("test@ya.ru");
    }

    @Test
    void createUser_shouldCreateAndReturnUserDto() {
        when(userMapper.toEntity(userDtoForCreate)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.createUser(userDtoForCreate);

        assertNotNull(result);
        assertEquals(userDto, result);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_shouldUpdateName() {
        UserDto updateDto = new UserDto();
        updateDto.setName("Updated Name");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.updateUser(1L, updateDto);

        assertEquals("Updated Name", user.getName());
        assertEquals(userDto, result);
    }

    @Test
    void updateUser_shouldUpdateEmailWhenNotExists() {
        UserDto updateDto = new UserDto();
        updateDto.setEmail("new@ya.ru");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("new@ya.ru")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.updateUser(1L, updateDto);

        assertEquals("new@ya.ru", user.getEmail());
        assertEquals(userDto, result);
    }

    @Test
    void updateUser_shouldThrowConflictExceptionWhenEmailExists() {
        UserDto updateDto = new UserDto();
        updateDto.setEmail("existing@ya.ru");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("existing@ya.ru")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.updateUser(1L, updateDto));
    }

    @Test
    void updateUser_shouldNotUpdateEmailWhenSame() {
        UserDto updateDto = new UserDto();
        updateDto.setEmail("test@ya.ru");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.updateUser(1L, updateDto);

        assertEquals("test@ya.ru", user.getEmail());
        assertEquals(userDto, result);
        verify(userRepository, never()).existsByEmail(any());
    }

    @Test
    void getUser_shouldReturnUserDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.getUser(1L);

        assertEquals(userDto, result);
    }

    @Test
    void getUser_shouldThrowNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUser(1L));
    }

    @Test
    void getAllUsers_shouldReturnListOfUserDto() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDto(List.of(user))).thenReturn(List.of(userDto));

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));
    }

    @Test
    void deleteUser_shouldCallRepositoryDelete() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void findUserById_shouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findUserById(1L);

        assertEquals(user, result);
    }

    @Test
    void findUserById_shouldThrowNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findUserById(1L));
    }
}