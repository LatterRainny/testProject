package com.demo.integrationTest.admin;


import com.demo.entity.User;
import com.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void testUserManage() throws Exception {
        // 准备模拟数据
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<User> mockPage = new PageImpl<>(Collections.emptyList());

        // 明确指定使用 findByUserID(Pageable) 方法
        when(userService.findByUserID(pageable)).thenReturn(mockPage);

        mockMvc.perform(get("/user_manage"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user_manage"))
                .andExpect(model().attributeExists("total"));

        verify(userService, times(1)).findByUserID(any(Pageable.class));
    }

    @Test
    public void testUserAdd() throws Exception {
        mockMvc.perform(get("/user_add"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user_add"));
    }

    @Test
    public void testUserEdit() throws Exception {
        User user = new User();
        when(userService.findById(1)).thenReturn(user);

        mockMvc.perform(get("/user_edit")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user_edit"));

        verify(userService, times(1)).findById(any(Integer.class));
    }

    @Test
    public void testUserList() throws Exception {
        // 准备模拟数据
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        List<User> userList = Arrays.asList(new User(), new User());
        Page<User> mockPage = new PageImpl<>(userList, pageable, userList.size());

        // 明确指定使用 findByUserID(Pageable) 方法
        when(userService.findByUserID(pageable)).thenReturn(mockPage);

        mockMvc.perform(get("/userList.do")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(userService, times(1)).findByUserID(any(Pageable.class));
    }

    @Test
    public void testCheckUserID() throws Exception {
        when(userService.countUserID("test")).thenReturn(0);

        mockMvc.perform(post("/checkUserID.do")
                        .param("userID", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(userService, times(1)).countUserID(any(String.class));
    }

    @Test
    public void testDelUser() throws Exception {
        mockMvc.perform(post("/delUser.do")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }
}
