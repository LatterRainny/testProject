package com.demo.integrationTest;

import com.demo.entity.Message;
import com.demo.entity.News;
import com.demo.entity.Venue;
import com.demo.entity.vo.MessageVo;
import com.demo.service.MessageService;
import com.demo.service.MessageVoService;
import com.demo.service.NewsService;
import com.demo.service.VenueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class IndexControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NewsService newsService;

    @MockBean
    private VenueService venueService;

    @MockBean
    private MessageService messageService;

    @MockBean
    private MessageVoService messageVoService;

    @Test
    public void testIndexPageWithData() throws Exception {
        // 准备测试数据
        News news1 = new News();
        news1.setTitle("News 1");
        News news2 = new News();
        news2.setTitle("News 2");
        List<News> newsList = Arrays.asList(news1, news2);

        Venue venue1 = new Venue();
        venue1.setVenueName("Venue 1");
        Venue venue2 = new Venue();
        venue2.setVenueName("Venue 2");
        List<Venue> venueList = Arrays.asList(venue1, venue2);

        Message message1 = new Message();
        message1.setMessageID(1);
        Message message2 = new Message();
        message2.setMessageID(2);
        Page<Message> messagePage = new PageImpl<>(Arrays.asList(message1, message2));

        MessageVo messageVo1 = new MessageVo();
        messageVo1.setMessageID(1);
        MessageVo messageVo2 = new MessageVo();
        messageVo2.setMessageID(2);
        List<MessageVo> messageVoList = Arrays.asList(messageVo1, messageVo2);

        // 设置Mock行为
        when(newsService.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(newsList));
        when(venueService.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(venueList));
        when(messageService.findPassState(any(Pageable.class))).thenReturn(messagePage);
        when(messageVoService.returnVo(messagePage.getContent())).thenReturn(messageVoList);

        // 执行测试并验证 - 使用正确的Hamcrest匹配器语法
        mockMvc.perform(get("/index"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("news_list"))
                .andExpect(model().attributeExists("venue_list"))
                .andExpect(model().attributeExists("message_list"))
                .andExpect(model().attribute("news_list", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(model().attribute("venue_list", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(model().attribute("message_list", org.hamcrest.Matchers.hasSize(2)));
    }

    @Test
    public void testIndexPageWithEmptyData() throws Exception {
        // 设置Mock返回空数据
        when(newsService.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));
        when(venueService.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));
        when(messageService.findPassState(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));
        when(messageVoService.returnVo(any())).thenReturn(Collections.emptyList());

        // 执行测试并验证 - 使用正确的Hamcrest匹配器语法
        mockMvc.perform(get("/index"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("news_list"))
                .andExpect(model().attributeExists("venue_list"))
                .andExpect(model().attributeExists("message_list"))
                .andExpect(model().attribute("news_list", org.hamcrest.Matchers.empty()))
                .andExpect(model().attribute("venue_list", org.hamcrest.Matchers.empty()))
                .andExpect(model().attribute("message_list", org.hamcrest.Matchers.empty()));
    }

    @Test
    public void testAdminIndexPage() throws Exception {
        // 执行测试并验证
        mockMvc.perform(get("/admin_index"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin_index"));
    }
}
