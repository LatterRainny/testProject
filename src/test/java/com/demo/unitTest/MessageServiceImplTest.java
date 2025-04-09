package com.demo.unitTest;

import com.demo.dao.MessageDao;
import com.demo.entity.Message;
import com.demo.service.MessageService;
import com.demo.service.impl.MessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceImplTest {

    // 模拟 MessageDao 依赖
    @Mock
    private MessageDao messageDao;

    // 注入 MessageDao 的模拟对象到 MessageServiceImpl 实例中
    @InjectMocks
    private MessageServiceImpl messageService;

    private Message message;

    @BeforeEach
    public void setUp() {
        // 初始化一个测试用的 Message 对象
        message = new Message();
        message.setMessageID(1);
        message.setUserID("user1");
        message.setContent("Test message");
        message.setTime(LocalDateTime.now());
        message.setState(MessageService.STATE_NO_AUDIT);
    }

    /**
     * 测试 findById 方法
     * 验证：当传入正确的 messageID 时，能返回对应的 Message 对象
     */
    @Test
    public void testFindById() {
        // 模拟 messageDao.getOne 方法返回测试用的 message 对象
        when(messageDao.getOne(1)).thenReturn(message);

        // 调用 service 方法
        Message found = messageService.findById(1);
        // 验证返回结果不为空且 userID 正确
        assertNotNull(found);
        assertEquals("user1", found.getUserID());

        // 验证 messageDao.getOne 方法被调用一次
        verify(messageDao, times(1)).getOne(1);
    }

    /**
     * 测试 findByUser 方法
     * 验证：当传入正确的 userID 和分页参数时，返回该用户对应的消息分页数据
     */
    @Test
    public void testFindByUser() {
        PageRequest pageable = PageRequest.of(0, 10);
        // 创建另一个 Message 对象，并放入集合中模拟返回结果
        Message msg2 = new Message(2, "user1", "Second message", LocalDateTime.now(), MessageService.STATE_NO_AUDIT);
        Page<Message> page = new PageImpl<>(Arrays.asList(message, msg2));
        when(messageDao.findAllByUserID("user1", pageable)).thenReturn(page);

        // 调用 service 方法
        Page<Message> result = messageService.findByUser("user1", pageable);
        // 验证返回分页中数据总数正确
        assertEquals(2, result.getTotalElements());
        // 遍历验证每个消息的 userID 都是 "user1"
        result.getContent().forEach(m -> assertEquals("user1", m.getUserID()));

        // 验证 messageDao.findAllByUserID 方法被调用一次
        verify(messageDao, times(1)).findAllByUserID("user1", pageable);
    }

    /**
     * 测试 create 方法
     * 验证：创建新的 Message 对象后返回正确的 messageID
     */
    @Test
    public void testCreate() {
        // 创建一个待创建的 Message 对象，ID 设为 0（未设置状态）
        Message toCreate = new Message(0, "user2", "New message", LocalDateTime.now(), MessageService.STATE_NO_AUDIT);
        // 模拟 save 方法，返回设置好 messageID 的 Message 对象
        Message saved = new Message(100, "user2", "New message", LocalDateTime.now(), MessageService.STATE_NO_AUDIT);
        when(messageDao.save(toCreate)).thenReturn(saved);

        // 调用 service 方法进行创建
        int id = messageService.create(toCreate);
        // 验证返回的 messageID 为 100
        assertEquals(100, id);

        // 验证 messageDao.save 方法被调用一次
        verify(messageDao, times(1)).save(toCreate);
    }

    /**
     * 测试 delById 方法
     * 验证：调用删除方法时 MessageDao.deleteById 被正确调用
     */
    @Test
    public void testDelById() {
        // 直接调用 service 删除方法
        messageService.delById(1);
        // 验证 messageDao.deleteById 方法被调用一次，参数为 1
        verify(messageDao, times(1)).deleteById(1);
    }

    /**
     * 测试 update 方法
     * 验证：更新 Message 对象后调用 save 方法
     */
    @Test
    public void testUpdate() {
        // 修改测试 Message 的内容
        message.setContent("Updated content");
        // 调用 service 更新方法
        messageService.update(message);
        // 验证 messageDao.save 方法被调用一次
        verify(messageDao, times(1)).save(message);
    }

    /**
     * 测试 confirmMessage 方法
     * 验证：当 Message 存在且状态为待审核时，调用确认操作后状态变为审核通过
     */
    @Test
    public void testConfirmMessage() {
        // 模拟通过 messageDao.findByMessageID 查找到测试 Message 对象
        when(messageDao.findByMessageID(1)).thenReturn(message);
        // 调用 service 的确认留言方法
        messageService.confirmMessage(1);
        // 验证 messageDao.updateState 方法被调用一次，并传入 STATE_PASS 和正确的 messageID
        verify(messageDao, times(1)).updateState(MessageService.STATE_PASS, 1);
    }

    /**
     * 测试 confirmMessage 方法：当留言不存在时，应该抛出异常
     */
    @Test
    public void testConfirmMessage_NotFound() {
        // 模拟留言不存在，返回 null
        when(messageDao.findByMessageID(999)).thenReturn(null);
        // 验证调用 confirmMessage 方法时抛出 RuntimeException 异常，并验证异常消息
        Exception exception = assertThrows(RuntimeException.class, () -> {
            messageService.confirmMessage(999);
        });
        assertEquals("留言不存在", exception.getMessage());
    }

    /**
     * 测试 rejectMessage 方法
     * 验证：当 Message 存在且状态为待审核时，调用拒绝操作后状态变为拒绝
     */
    @Test
    public void testRejectMessage() {
        // 模拟通过 messageDao.findByMessageID 查找到测试 Message 对象
        when(messageDao.findByMessageID(1)).thenReturn(message);
        // 调用 service 的拒绝留言方法
        messageService.rejectMessage(1);
        // 验证 messageDao.updateState 方法被调用一次，并传入 STATE_REJECT 和正确的 messageID
        verify(messageDao, times(1)).updateState(MessageService.STATE_REJECT, 1);
    }

    /**
     * 测试 rejectMessage 方法：当留言不存在时，应该抛出异常
     */
    @Test
    public void testRejectMessage_NotFound() {
        // 模拟留言不存在，返回 null
        when(messageDao.findByMessageID(999)).thenReturn(null);
        // 验证调用 rejectMessage 方法时抛出 RuntimeException 异常，并验证异常消息
        Exception exception = assertThrows(RuntimeException.class, () -> {
            messageService.rejectMessage(999);
        });
        assertEquals("留言不存在", exception.getMessage());
    }

    /**
     * 测试 findWaitState 方法
     * 验证：分页查询返回的消息全部为待审核状态
     */
    @Test
    public void testFindWaitState() {
        PageRequest pageable = PageRequest.of(0, 10);
        // 模拟返回一个待审核的消息分页结果
        Page<Message> page = new PageImpl<>(Collections.singletonList(message));
        when(messageDao.findAllByState(MessageService.STATE_NO_AUDIT, pageable)).thenReturn(page);

        // 调用 service 方法
        Page<Message> result = messageService.findWaitState(pageable);
        // 验证返回分页数据中消息总数为 1
        assertEquals(1, result.getTotalElements());
        // 验证返回的每个消息状态均为 STATE_NO_AUDIT
        result.getContent().forEach(m -> assertEquals(MessageService.STATE_NO_AUDIT, m.getState()));

        // 验证 messageDao.findAllByState 被调用一次，参数为 STATE_NO_AUDIT 和正确的分页参数
        verify(messageDao, times(1)).findAllByState(MessageService.STATE_NO_AUDIT, pageable);
    }

    /**
     * 测试 findPassState 方法
     * 验证：分页查询返回的消息全部为审核通过状态
     */
    @Test
    public void testFindPassState() {
        PageRequest pageable = PageRequest.of(0, 10);
        // 创建一个审核通过状态的 Message 对象
        Message passMessage = new Message(2, "user1", "Passed message", LocalDateTime.now(), MessageService.STATE_PASS);
        // 模拟返回一个审核通过的消息分页结果
        Page<Message> page = new PageImpl<>(Collections.singletonList(passMessage));
        when(messageDao.findAllByState(MessageService.STATE_PASS, pageable)).thenReturn(page);

        // 调用 service 方法
        Page<Message> result = messageService.findPassState(pageable);
        // 验证返回分页数据中消息总数为 1
        assertEquals(1, result.getTotalElements());
        // 验证返回的每个消息状态均为 STATE_PASS
        result.getContent().forEach(m -> assertEquals(MessageService.STATE_PASS, m.getState()));

        // 验证 messageDao.findAllByState 被调用一次，参数为 STATE_PASS 和正确的分页参数
        verify(messageDao, times(1)).findAllByState(MessageService.STATE_PASS, pageable);
    }
}
