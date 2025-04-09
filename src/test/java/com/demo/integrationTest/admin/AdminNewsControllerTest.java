package com.demo.integrationTest.admin;

import com.demo.entity.Message;
import com.demo.entity.News;
import com.demo.service.NewsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminNewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NewsService newsService;

    @Test
    public void testNewsManage() throws Exception {
        Page<News> mockPage = new PageImpl<>(Collections.emptyList());
        when(newsService.findAll(any(Pageable.class))).thenReturn(mockPage);
        mockMvc.perform(get("/news_manage"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/news_manage"));

        verify(newsService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void testNewsAdd() throws Exception {
        mockMvc.perform(get("/news_add"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/news_add"));
    }

    @Test
    public void testNewsEdit() throws Exception {
        News news = new News();
        when(newsService.findById(1)).thenReturn(news);

        mockMvc.perform(get("/news_edit")
                        .param("newsID", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/news_edit"));
        verify(newsService, times(1)).findById(1);
    }

    @Test
    public void testNewsListWithNoData() throws Exception {
        Page<News> mockPage = new PageImpl<>(Collections.emptyList());
        when(newsService.findAll(any(Pageable.class))).thenReturn(mockPage);

        mockMvc.perform(get("/newsList.do")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        verify(newsService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void testNewsList() throws Exception {
        Page<News> page = new PageImpl<>(Collections.singletonList(new News()));
        when(newsService.findAll(any())).thenReturn(page);

        mockMvc.perform(get("/newsList.do")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
        verify(newsService, times(1)).findAll(any());
    }

    @Test
    public void testDelNews() throws Exception {
        mockMvc.perform(post("/delNews.do")
                        .param("newsID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }
}