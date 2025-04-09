package com.demo.integrationTest.admin;

import com.demo.entity.Message;
import com.demo.entity.vo.MessageVo;
import com.demo.service.MessageService;
import com.demo.service.MessageVoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminMessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @MockBean
    private MessageVoService messageVoService;

    @Test
    public void testMessageManageWithEmptyMessage() throws Exception {
        // 模拟返回一个包含空数据的Page对象
        Page<Message> mockPage = new PageImpl<>(Collections.emptyList());
        when(messageService.findWaitState(any(Pageable.class))).thenReturn(mockPage);

        // 模拟MessageVoService返回空列表
        when(messageVoService.returnVo(any())).thenReturn(Collections.emptyList());
        mockMvc.perform(MockMvcRequestBuilders.get("/message_manage"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/message_manage"));

        verify(messageService, times(1)).findWaitState(any(Pageable.class));
    }

    @Test
    public void testMessageManageWithMessage() throws Exception {
         // 模拟返回一个包含数据的Page对象
        Message message1 = new Message();
        message1.setMessageID(1);
        List<Message> messages = new ArrayList<>();
        Pageable message_pageable = PageRequest.of(0, 10, Sort.by("time").descending());
        Page<Message> messagePage = new PageImpl<>(messages, message_pageable, 3);

        //given
        when(messageService.findWaitState(message_pageable)).thenReturn(messagePage);
        //when&then
        mockMvc.perform(MockMvcRequestBuilders.get("/message_manage"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/message_manage"))
                .andExpect(model().attribute("total", messagePage.getTotalPages()));
        verify(messageService, times(1)).findWaitState(message_pageable);
    }

    @Test
    public void testMessageListWithEmptyMessage() throws Exception {
        Page<Message> mockPage = new PageImpl<>(Collections.emptyList());
        when(messageService.findWaitState(any(Pageable.class))).thenReturn(mockPage);

        when(messageVoService.returnVo(any())).thenReturn(Collections.singletonList(new MessageVo()));

        mockMvc.perform(MockMvcRequestBuilders.get("/messageList.do")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
        verify(messageService, times(1)).findWaitState(any(Pageable.class));
    }

    @Test
    public void testMessageListWithMessage() throws Exception {
        Message message1 = new Message();
        message1.setMessageID(1);
        Message message2 = new Message();
        message2.setMessageID(2);
        List<Message> messages = new ArrayList<>();
        messages.add(message1);
        messages.add(message2);
        Pageable message_pageable = PageRequest.of(0, 10, Sort.by("time").descending());
        Page<Message> messagePage = new PageImpl<>(messages, message_pageable, 3);

        when(messageService.findWaitState(message_pageable)).thenReturn(messagePage);
        when(messageVoService.returnVo(any())).thenReturn(Arrays.asList(new MessageVo(), new MessageVo()));

        mockMvc.perform(MockMvcRequestBuilders.get("/messageList.do")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.size()").value(2));
        verify(messageService, times(1)).findWaitState(message_pageable);
    }

    @Test
    public void testPassMessage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/passMessage.do")
                        .param("messageID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    public void testRejectMessage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/rejectMessage.do")
                        .param("messageID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    public void testDelMessage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/delMessage.do")
                        .param("messageID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }
}