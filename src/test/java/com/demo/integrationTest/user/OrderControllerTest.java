package com.demo.integrationTest.user;

import com.demo.entity.Order;
import com.demo.entity.User;
import com.demo.entity.Venue;
import com.demo.entity.vo.OrderVo;
import com.demo.service.OrderService;
import com.demo.service.OrderVoService;
import com.demo.service.VenueService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private OrderVoService orderVoService;

    @MockBean
    private VenueService venueService;

    @Test
    public void testOrderManage() throws Exception {
        MockHttpSession session = new MockHttpSession();
        User user = new User();
        user.setUserID("test");
        session.setAttribute("user", user);

        Page<Order> page = new PageImpl<>(Collections.singletonList(new Order()));
        when(orderService.findUserOrder(any(), any())).thenReturn(page);

        mockMvc.perform(get("/order_manage").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("order_manage"));
    }

    @Test
    public void testOrderPlace() throws Exception {
        Venue venue = new Venue();
        when(venueService.findByVenueID(1)).thenReturn(venue);

        mockMvc.perform(get("/order_place.do")
                        .param("venueID", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("order_place"));
    }

    @Test
    public void testGetOrderList() throws Exception {
        // 准备模拟数据
        String testUserId = "testUser123";
        Pageable pageable = PageRequest.of(0, 5, Sort.by("orderTime").descending());

        // 创建测试订单数据
        Order order1 = new Order();
        order1.setOrderID(1);
        Order order2 = new Order();
        order2.setOrderID(2);
        List<Order> orders = Arrays.asList(order1, order2);
        Page<Order> mockPage = new PageImpl<>(orders, pageable, orders.size());

        // 创建测试OrderVo数据
        OrderVo orderVo1 = new OrderVo();
        orderVo1.setOrderID(1);
        OrderVo orderVo2 = new OrderVo();
        orderVo2.setOrderID(2);
        List<OrderVo> orderVos = Arrays.asList(orderVo1, orderVo2);

        // 模拟用户登录会话
        MockHttpSession session = new MockHttpSession();
        User user = new User();
        user.setUserID(testUserId);
        session.setAttribute("user", user);

        // 设置Mock行为
        when(orderService.findUserOrder(testUserId, pageable)).thenReturn(mockPage);
        when(orderVoService.returnVo(orders)).thenReturn(orderVos);

        // 执行测试并验证
        mockMvc.perform(get("/getOrderList.do")
                        .param("page", "1")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].orderID").value(1))
                .andExpect(jsonPath("$[1].orderID").value(2));

        // 验证方法调用
        verify(orderService, times(1)).findUserOrder(testUserId, pageable);
        verify(orderVoService, times(1)).returnVo(orders);
    }

    @Test
    public void testFinishOrder() throws Exception {
        mockMvc.perform(post("/finishOrder.do")
                        .param("orderID", "1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testEditOrder() throws Exception {
        // 准备测试数据
        int testOrderId = 1;
        int testVenueId = 100;
        String testVenueName = "Test Venue";
        String testUserId = "testUser123";

        // 创建模拟订单和场馆
        Order mockOrder = new Order();
        mockOrder.setOrderID(testOrderId);
        mockOrder.setVenueID(testVenueId);

        Venue mockVenue = new Venue();
        mockVenue.setVenueID(testVenueId);
        mockVenue.setVenueName(testVenueName);

        // 模拟用户会话
        MockHttpSession session = new MockHttpSession();
        User user = new User();
        user.setUserID(testUserId);
        session.setAttribute("user", user);

        // 设置Mock行为
        when(orderService.findById(testOrderId)).thenReturn(mockOrder);
        when(venueService.findByVenueID(testVenueId)).thenReturn(mockVenue);

        // 执行测试请求
        mockMvc.perform(get("/modifyOrder.do")
                        .param("orderID", String.valueOf(testOrderId))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("order_edit"))
                .andExpect(model().attributeExists("venue"))
                .andExpect(model().attributeExists("order"));

        // 验证方法调用
        verify(orderService, times(1)).findById(testOrderId);
        verify(venueService, times(1)).findByVenueID(testVenueId);
    }

    @Test
    public void testDelOrder() throws Exception {
        mockMvc.perform(post("/delOrder.do")
                        .param("orderID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }
}
