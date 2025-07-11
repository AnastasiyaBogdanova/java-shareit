package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService requestService;

    @Test
    void createRequest_shouldReturnCreatedRequest() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("утюг");

        ItemRequestDto responseDto = new ItemRequestDto();
        responseDto.setId(1L);
        responseDto.setDescription("утюг");
        responseDto.setCreated(LocalDateTime.now());

        when(requestService.createRequest(anyLong(), any(ItemRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("утюг"))
                .andExpect(jsonPath("$.created").exists());
    }

    @Test
    void getUserRequests_shouldReturnListOfRequests() throws Exception {
        ItemRequestWithItemsDto request1 = new ItemRequestWithItemsDto();
        request1.setId(1L);
        request1.setDescription("утюг");
        request1.setCreated(LocalDateTime.now());
        request1.setItems(List.of(new ItemDto()));

        when(requestService.getUserRequests(anyLong())).thenReturn(List.of(request1));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("утюг"))
                .andExpect(jsonPath("$[0].items.length()").value(1));
    }

    @Test
    void getAllRequests_shouldReturnPaginatedRequests() throws Exception {
        ItemRequestWithItemsDto request1 = new ItemRequestWithItemsDto();
        request1.setId(1L);
        request1.setDescription("утюг");
        request1.setCreated(LocalDateTime.now());

        when(requestService.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(request1));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("утюг"));
    }

    @Test
    void getRequestById_shouldReturnRequestWithItems() throws Exception {
        ItemRequestWithItemsDto request = new ItemRequestWithItemsDto();
        request.setId(1L);
        request.setDescription("утюг");
        request.setCreated(LocalDateTime.now());
        request.setItems(List.of(new ItemDto()));

        when(requestService.getRequestById(anyLong(), anyLong())).thenReturn(request);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("утюг"))
                .andExpect(jsonPath("$.items.length()").value(1));
    }
}