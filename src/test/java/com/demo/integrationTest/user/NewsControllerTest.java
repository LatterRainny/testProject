package com.demo.integrationTest.user;

import com.demo.entity.News;
import com.demo.service.NewsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class NewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NewsService newsService;

    @Test
    public void testNewsPage() throws Exception {
        News news = new News();
        when(newsService.findById(1)).thenReturn(news);

        mockMvc.perform(get("/news")
                        .param("newsID", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("news"));
    }

    @Test
    public void testNewsListPage() throws Exception {
        Page<News> page = new PageImpl<>(Collections.singletonList(new News()));
        when(newsService.findAll(any())).thenReturn(page);

        mockMvc.perform(get("/news_list"))
                .andExpect(status().isOk())
                .andExpect(view().name("news_list"));
    }

    @Test
    public void testGetNewsList() throws Exception {
        Page<News> page = new PageImpl<>(Collections.singletonList(new News()));
        when(newsService.findAll(any())).thenReturn(page);

        mockMvc.perform(get("/news/getNewsList")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }
}
