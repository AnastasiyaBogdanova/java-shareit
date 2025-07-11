package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoForCreate;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        UserDtoForCreate userDto = new UserDtoForCreate();
        userDto.setName("user");
        userDto.setEmail("john@ya.ru");

        UserDto responseDto = new UserDto();
        responseDto.setId(1L);
        responseDto.setName("user");
        responseDto.setEmail("user@yandex.ru");

        Mockito.when(userService.createUser(any(UserDtoForCreate.class))).thenReturn(responseDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("user"))
                .andExpect(jsonPath("$.email").value("user@yandex.ru"));
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Updated user");
        userDto.setEmail("update@yandex.ru");

        UserDto responseDto = new UserDto();
        responseDto.setId(1L);
        responseDto.setName("Updated user");
        responseDto.setEmail("update@yandex.ru");

        Mockito.when(userService.updateUser(anyLong(), any(UserDto.class))).thenReturn(responseDto);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated user"))
                .andExpect(jsonPath("$.email").value("update@yandex.ru"));
    }

    @Test
    void getUser_shouldReturnUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("user");
        userDto.setEmail("user@yandex.ru");

        Mockito.when(userService.getUser(1L)).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("user"))
                .andExpect(jsonPath("$.email").value("user@yandex.ru"));
    }

    @Test
    void getUser_shouldReturnNotFoundForInvalidId() throws Exception {
        Mockito.when(userService.getUser(999L)).thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() throws Exception {
        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setName("User 1");
        user1.setEmail("user1@yandex.ru");

        UserDto user2 = new UserDto();
        user2.setId(2L);
        user2.setName("User 2");
        user2.setEmail("user2@yandex.ru");

        Mockito.when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        Mockito.verify(userService).deleteUser(1L);
    }

    @Test
    void updateUser_shouldReturnConflictForDuplicateEmail() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("duplicate@yandex.ru");

        Mockito.when(userService.updateUser(anyLong(), any(UserDto.class)))
                .thenThrow(new ConflictException("Почта существует"));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isConflict());
    }
}