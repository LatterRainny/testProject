package com.demo.unitTest;

import com.demo.dao.MessageDao;
import com.demo.dao.UserDao;
import com.demo.entity.Message;
import com.demo.entity.User;
import com.demo.entity.vo.MessageVo;
import com.demo.service.impl.MessageVoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageVoServiceImplTest {

    // 模拟 MessageDao 的依赖
    @Mock
    private MessageDao messageDao;

    // 模拟 UserDao 的依赖
    @Mock
    private UserDao userDao;

    // 将上面两个模拟对象注入到 MessageVoServiceImpl 实例中
    @InjectMocks
    private MessageVoServiceImpl messageVoService;

    // 测试用的 Message 对象
    private Message message;
    // 测试用的 User 对象
    private User user;

    @BeforeEach
    public void setUp() {
        // 初始化测试 Message 对象
        message = new Message();
        message.setMessageID(1);
        message.setUserID("user1");
        message.setContent("This is a test message");
        message.setTime(LocalDateTime.now());
        message.setState(1);  // 例如：1 未审核

        // 初始化测试 User 对象
        user = new User();
        user.setUserID("user1");
        user.setUserName("Test User");
        user.setPicture("test_pic.png");
    }

    /**
     * 测试 returnMessageVoByMessageID 方法
     * 模拟通过 MessageDao 与 UserDao 根据 messageID 和 userID 获取数据，
     * 最终返回一个 MessageVo 对象，其字段内容与预期一致。
     */
    @Test
    public void testReturnMessageVoByMessageID() {
        // 模拟 messageDao.findByMessageID 方法返回测试 Message 对象
        when(messageDao.findByMessageID(1)).thenReturn(message);
        // 模拟 userDao.findByUserID 方法返回测试 User 对象
        when(userDao.findByUserID("user1")).thenReturn(user);

        // 调用方法获取 MessageVo 对象
        MessageVo messageVo = messageVoService.returnMessageVoByMessageID(1);

        // 断言返回的 MessageVo 对象不为空，并且各属性值与预期一致
        assertNotNull(messageVo);
        assertEquals(message.getMessageID(), messageVo.getMessageID());
        assertEquals(message.getUserID(), messageVo.getUserID());
        assertEquals(message.getContent(), messageVo.getContent());
        assertEquals(message.getTime(), messageVo.getTime());
        assertEquals(user.getUserName(), messageVo.getUserName());
        assertEquals(user.getPicture(), messageVo.getPicture());
        assertEquals(message.getState(), messageVo.getState());

        // 验证 messageDao.findByMessageID 与 userDao.findByUserID 方法被调用一次
        verify(messageDao, times(1)).findByMessageID(1);
        verify(userDao, times(1)).findByUserID("user1");
    }

    /**
     * 测试 returnVo 方法
     * 传入一个 Message 对象集合，方法内部会调用 returnMessageVoByMessageID 将每个 Message 转换为 MessageVo，
     * 最终返回一个 MessageVo 对象集合
     */
    @Test
    public void testReturnVo() {
        // 模拟 messageDao.findByMessageID 方法返回测试 Message 对象
        when(messageDao.findByMessageID(1)).thenReturn(message);
        // 模拟 userDao.findByUserID 方法返回测试 User 对象
        when(userDao.findByUserID("user1")).thenReturn(user);

        // 构造测试数据：包含一个 Message 对象的列表
        List<Message> messages = Collections.singletonList(message);

        // 调用方法获取 MessageVo 列表
        List<MessageVo> voList = messageVoService.returnVo(messages);

        // 断言返回的列表不为空，且列表大小为 1
        assertNotNull(voList);
        assertEquals(1, voList.size());

        // 获取第一个 MessageVo 并验证各个属性
        MessageVo messageVo = voList.get(0);
        assertEquals(message.getMessageID(), messageVo.getMessageID());
        assertEquals(message.getUserID(), messageVo.getUserID());
        assertEquals(message.getContent(), messageVo.getContent());
        assertEquals(message.getTime(), messageVo.getTime());
        assertEquals(user.getUserName(), messageVo.getUserName());
        assertEquals(user.getPicture(), messageVo.getPicture());
        assertEquals(message.getState(), messageVo.getState());

        // 验证 findByMessageID 与 findByUserID 分别被调用一次
        verify(messageDao, times(1)).findByMessageID(1);
        verify(userDao, times(1)).findByUserID("user1");
    }

    /**
     * 测试 returnVo 方法：传入空列表时，返回空列表
     */
    @Test
    public void testReturnVo_EmptyList() {
        // 调用方法，传入空列表
        List<MessageVo> voList = messageVoService.returnVo(Collections.emptyList());

        // 断言返回的列表为空
        assertNotNull(voList);
        assertTrue(voList.isEmpty());

        // 验证 DAO 方法不被调用
        verify(messageDao, never()).findByMessageID(anyInt());
        verify(userDao, never()).findByUserID(anyString());
    }
}
