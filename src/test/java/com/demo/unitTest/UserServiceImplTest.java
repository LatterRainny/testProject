package com.demo.unitTest;

import com.demo.dao.UserDao;
import com.demo.entity.User;
import com.demo.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    // 模拟 UserDao 依赖，避免直接访问数据库
    @Mock
    private UserDao userDao;

    // 将模拟的 UserDao 注入到 UserServiceImpl 实例中
    @InjectMocks
    private UserServiceImpl userService;

    // 测试用的 User 对象
    private User user;

    @BeforeEach
    public void setUp() {
        // 初始化测试 User 对象
        user = new User();
        user.setId(1);
        user.setUserID("user1");
        user.setUserName("Test User");
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setPhone("123456789");
        user.setIsadmin(0);  // 普通用户
        user.setPicture("default.png");
    }

    /**
     * 测试 findByUserID(String userID) 方法
     * 验证：传入存在的 userID 后返回正确的 User 对象
     */
    @Test
    public void testFindByUserID_String() {
        // 模拟 UserDao 返回测试 User 对象
        when(userDao.findByUserID("user1")).thenReturn(user);

        User result = userService.findByUserID("user1");
        assertNotNull(result);
        assertEquals("user1", result.getUserID());
        assertEquals("Test User", result.getUserName());

        verify(userDao, times(1)).findByUserID("user1");
    }

    /**
     * 测试 findById(int id) 方法
     * 验证：根据 id 返回正确的 User 对象
     */
    @Test
    public void testFindById() {
        when(userDao.findById(1)).thenReturn(user);

        User result = userService.findById(1);
        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(userDao, times(1)).findById(1);
    }

    /**
     * 测试 findByUserID(Pageable pageable) 方法
     * 验证：分页查询返回普通用户（isadmin 为 0）的 User 列表
     */
    @Test
    public void testFindByUserID_Pageable() {
        // 构造分页参数，例如第一页，每页 10 条记录
        Pageable pageable = PageRequest.of(0, 10);
        List<User> userList = Collections.singletonList(user);
        Page<User> page = new PageImpl<>(userList, pageable, userList.size());
        when(userDao.findAllByIsadmin(0, pageable)).thenReturn(page);

        Page<User> result = userService.findByUserID(pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        result.getContent().forEach(u -> assertEquals(0, u.getIsadmin()));
        verify(userDao, times(1)).findAllByIsadmin(0, pageable);
    }

    /**
     * 测试 checkLogin(String userID, String password) 方法
     * 验证：传入正确的 userID 和 password 后返回对应的 User 对象
     */
    @Test
    public void testCheckLogin() {
        when(userDao.findByUserIDAndPassword("user1", "password")).thenReturn(user);

        User result = userService.checkLogin("user1", "password");
        assertNotNull(result);
        assertEquals("user1", result.getUserID());
        verify(userDao, times(1)).findByUserIDAndPassword("user1", "password");
    }

    /**
     * 测试 create(User user) 方法
     * 验证：调用创建方法后返回用户总数
     */
    @Test
    public void testCreate() {
        // 模拟 save 返回 user
        when(userDao.save(user)).thenReturn(user);

        // 模拟 findAll 返回列表
        when(userDao.findAll()).thenReturn(Arrays.asList(user));

        int count = userService.create(user);
        assertEquals(1, count);

        verify(userDao, times(1)).save(user);
        verify(userDao, times(1)).findAll();
    }

    /**
     * 测试 delByID(int id) 方法
     * 验证：调用删除方法后，UserDao.deleteById 被正确调用
     */
    @Test
    public void testDelByID() {
        // 直接调用删除方法
        userService.delByID(1);
        verify(userDao, times(1)).deleteById(1);
    }

    /**
     * 测试 updateUser(User user) 方法
     * 验证：更新用户信息后调用 UserDao.save 方法
     */
    @Test
    public void testUpdateUser() {
        // 修改用户的某些属性
        user.setUserName("Updated Name");
        userService.updateUser(user);
        verify(userDao, times(1)).save(user);
    }

    /**
     * 测试 countUserID(String userID) 方法
     * 验证：根据 userID 返回对应的用户数量
     */
    @Test
    public void testCountUserID() {
        when(userDao.countByUserID("user1")).thenReturn(1);
        int count = userService.countUserID("user1");
        assertEquals(1, count);
        verify(userDao, times(1)).countByUserID("user1");
    }
}
