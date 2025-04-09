package com.demo.integrationTest.user;

import com.demo.entity.Message;
import com.demo.entity.User;
import com.demo.entity.vo.MessageVo;
import com.demo.service.MessageService;
import com.demo.service.MessageVoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @MockBean
    private MessageVoService messageVoService;

    @Test
    public void testMessageListPage() throws Exception {
        MockHttpSession session = new MockHttpSession();
        User user = new User();
        user.setUserID("test");
        session.setAttribute("user", user);

        Page<Message> page = new PageImpl<>(Collections.singletonList(new Message()));
        when(messageService.findPassState(any())).thenReturn(page);
        when(messageService.findByUser(any(), any())).thenReturn(page);

        mockMvc.perform(get("/message_list").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("message_list"));
    }

    @Test
    public void testGetMessageList() throws Exception {
        // 准备测试数据
        Message message1 = new Message();
        message1.setMessageID(1);
        Message message2 = new Message();
        message2.setMessageID(2);
        List<Message> messages = Arrays.asList(message1, message2);

        // 准备分页数据
        Pageable pageable = PageRequest.of(0, 5, Sort.by("time").descending());
        Page<Message> messagePage = new PageImpl<>(messages, pageable, messages.size());

        // 准备VO数据
        MessageVo vo1 = new MessageVo();
        vo1.setMessageID(1);
        MessageVo vo2 = new MessageVo();
        vo2.setMessageID(2);
        List<MessageVo> messageVos = Arrays.asList(vo1, vo2);

        // 设置Mock行为
        when(messageService.findPassState(pageable)).thenReturn(messagePage);
        when(messageVoService.returnVo(messages)).thenReturn(messageVos);

        // 执行测试
        mockMvc.perform(get("/message/getMessageList")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].messageID").value(1))
                .andExpect(jsonPath("$[1].messageID").value(2));
    }

    @Test
    public void testFindUserList() throws Exception {
        // 准备测试数据
        String testUserId = "user123";
        Message message1 = new Message();
        message1.setMessageID(1);
        message1.setUserID(testUserId);
        List<Message> userMessages = Collections.singletonList(message1);

        // 准备分页数据
        Pageable pageable = PageRequest.of(0, 5, Sort.by("time").descending());
        Page<Message> messagePage = new PageImpl<>(userMessages, pageable, userMessages.size());

        // 准备VO数据
        MessageVo vo = new MessageVo();
        vo.setMessageID(1);
        vo.setUserID(testUserId);
        List<MessageVo> messageVos = Collections.singletonList(vo);

        // 模拟用户会话
        MockHttpSession session = new MockHttpSession();
        User user = new User();
        user.setUserID(testUserId);
        session.setAttribute("user", user);

        // 设置Mock行为
        when(messageService.findByUser(testUserId, pageable)).thenReturn(messagePage);
        when(messageVoService.returnVo(userMessages)).thenReturn(messageVos);

        // 执行测试
        mockMvc.perform(get("/message/findUserList")
                        .param("page", "1")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userID").value(testUserId));
    }

    @Test
    public void testSendMessage() throws Exception {
        mockMvc.perform(post("/sendMessage")
                        .param("userID", "test")
                        .param("content", "test content"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testModifyMessage() throws Exception {
        // 准备测试数据
        int messageId = 1;
        String newContent = "Updated content";
        Message existingMessage = new Message();
        existingMessage.setMessageID(messageId);
        existingMessage.setContent("Old content");

        // 设置Mock行为
        when(messageService.findById(messageId)).thenReturn(existingMessage);

        // 执行测试
        mockMvc.perform(post("/modifyMessage.do")
                        .param("messageID", String.valueOf(messageId))
                        .param("content", newContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        // 验证
        verify(messageService).update(argThat(message ->
                message.getMessageID() == messageId &&
                        newContent.equals(message.getContent())
        ));
    }

    @Test
    public void testDelMessage() throws Exception {
        mockMvc.perform(post("/delMessage.do")
                        .param("messageID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }
}
