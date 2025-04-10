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

import static javax.management.Query.value;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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
                .andExpect(view().name("news"))
                .andExpect(model().attribute("news", news));

        verify(newsService, times(1)).findById(1);
    }

    @Test
    public void testNewsListPage() throws Exception {
        News news = new News();
        news.setNewsID(1);
        Page<News> page = new PageImpl<>(Collections.singletonList(news));
        when(newsService.findAll(any())).thenReturn(page);

        mockMvc.perform(get("/news_list"))
                .andExpect(status().isOk())
                .andExpect(view().name("news_list"))
                .andExpect(jsonPath("$").exists())
                .andExpect(model().attribute("news_list", hasSize(1)))
                .andExpect(model().attribute("total", 1));

        verify(newsService, times(2)).findAll(any());
    }

    @Test
    public void testGetNewsList() throws Exception {
        News news = new News();
        news.setNewsID(1);
        Page<News> page = new PageImpl<>(Collections.singletonList(news));
        when(newsService.findAll(any())).thenReturn(page);

        mockMvc.perform(get("/news/getNewsList")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());

        verify(newsService, times(1)).findAll(any());
    }
}
