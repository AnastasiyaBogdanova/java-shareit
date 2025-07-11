package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    void convertUserToDto() {
        User user = new User();
        user.setId(1L);
        user.setName("Name");
        user.setEmail("name@yandex.ru");

        UserDto dto = userMapper.toDto(user);

        assertEquals(1L, dto.getId());
        assertEquals("Name", dto.getName());
        assertEquals("name@yandex.ru", dto.getEmail());
    }

    @Test
    void convertCollectionOfUsers() {
        User user = new User();
        user.setId(1L);
        user.setName("Name");
        user.setEmail("name@yandex.ru");

        List<UserDto> dtos = new ArrayList<>(userMapper.toDto(Collections.singletonList(user)));

        assertEquals(1, dtos.size());
        assertEquals(1L, dtos.get(0).getId());
        assertEquals("Name", dtos.get(0).getName());
        assertEquals("name@yandex.ru", dtos.get(0).getEmail());
    }

    @Test
    void convertDtoToUser() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("Name");
        dto.setEmail("name@yandex.ru");

        User user = userMapper.toEntity(dto);

        assertEquals(1L, user.getId());
        assertEquals("Name", user.getName());
        assertEquals("name@yandex.ru", user.getEmail());
    }
}
