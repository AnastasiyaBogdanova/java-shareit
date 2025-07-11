package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForCreate;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void createItem_shouldReturnCreatedItem() throws Exception {
        ItemDtoForCreate itemDto = new ItemDtoForCreate();
        itemDto.setName("утюг");
        itemDto.setDescription("хороший");
        itemDto.setAvailable(true);

        ItemDto responseDto = new ItemDto();
        responseDto.setId(1L);
        responseDto.setName("утюг");
        responseDto.setDescription("хороший");
        responseDto.setAvailable(true);

        when(itemService.createItem(anyLong(), any(ItemDtoForCreate.class))).thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("утюг"))
                .andExpect(jsonPath("$.description").value("хороший"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void updateItem_shouldReturnUpdatedItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("утюг чугунный");
        itemDto.setDescription("отличный");
        itemDto.setAvailable(false);

        ItemDto responseDto = new ItemDto();
        responseDto.setId(1L);
        responseDto.setName("утюг чугунный");
        responseDto.setDescription("отличный");
        responseDto.setAvailable(false);

        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class))).thenReturn(responseDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("утюг чугунный"))
                .andExpect(jsonPath("$.description").value("отличный"))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void getItemById_shouldReturnItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("утюг");
        itemDto.setDescription("хороший");
        itemDto.setAvailable(true);

        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("утюг"))
                .andExpect(jsonPath("$.description").value("хороший"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void getUserItems_shouldReturnListOfItems() throws Exception {
        ItemDto item1 = new ItemDto();
        item1.setId(1L);
        item1.setName("утюг");
        item1.setDescription("хороший");
        item1.setAvailable(true);

        ItemDto item2 = new ItemDto();
        item2.setId(2L);
        item2.setName("стол");
        item2.setDescription("обычный");
        item2.setAvailable(true);

        when(itemService.getUserItems(anyLong())).thenReturn(List.of(item1, item2));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void searchItems_shouldReturnMatchingItems() throws Exception {
        ItemDto item1 = new ItemDto();
        item1.setId(1L);
        item1.setName("утюг");
        item1.setDescription("хороший");
        item1.setAvailable(true);

        when(itemService.searchItems(anyString(), anyLong())).thenReturn(List.of(item1));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "утюг"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("утюг"));
    }

    @Test
    void addComment_shouldReturnCreatedComment() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("супер!");

        CommentDto responseDto = new CommentDto();
        responseDto.setId(1L);
        responseDto.setText("супер!");
        responseDto.setAuthorName("User");

        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("супер!"))
                .andExpect(jsonPath("$.authorName").value("User"));
    }
}