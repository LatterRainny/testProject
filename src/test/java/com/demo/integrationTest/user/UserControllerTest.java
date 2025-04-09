package com.demo.integrationTest.user;

import com.demo.entity.User;
import com.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void testSignUp() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"));
    }

    @Test
    public void testLogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    public void testLoginCheckSuccess() throws Exception {
        User user = new User();
        user.setIsadmin(0);
        when(userService.checkLogin(any(), any())).thenReturn(user);

        mockMvc.perform(post("/loginCheck.do")
                        .param("userID", "test")
                        .param("password", "123456"))
                .andExpect(status().isOk())
                .andExpect(content().string("/index"));
    }

    @Test
    public void testRegister() throws Exception {
        mockMvc.perform(post("/register.do")
                        .param("userID", "test")
                        .param("userName", "test")
                        .param("password", "123456")
                        .param("email", "test@test.com")
                        .param("phone", "12345678901"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testLogout() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", new User());

        mockMvc.perform(get("/logout.do").session(session))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testCheckPassword() throws Exception {
        User user = new User();
        user.setPassword("123456");
        when(userService.findByUserID("test")).thenReturn(user);

        mockMvc.perform(get("/checkPassword.do")
                        .param("userID", "test")
                        .param("password", "123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(userService, times(1)).findByUserID("test");
    }

    @Test
    public void testUserInfo() throws Exception {
        // 模拟用户登录
        MockHttpSession session = new MockHttpSession();
        User user = new User();
        user.setUserID("testUser");
        session.setAttribute("user", user);

        mockMvc.perform(get("/user_info").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("user_info"));
    }
}
