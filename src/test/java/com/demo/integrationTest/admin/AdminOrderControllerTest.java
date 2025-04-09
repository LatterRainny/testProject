package com.demo.integrationTest.admin;

import com.demo.entity.Order;
import com.demo.entity.vo.OrderVo;
import com.demo.service.OrderService;
import com.demo.service.OrderVoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
public class AdminOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private OrderVoService orderVoService;

    @Test
    public void testReservationManage() throws Exception {
        List<Order> orders = Collections.emptyList();
        Page<Order> orderPage = new PageImpl<>(orders);

        when(orderService.findAuditOrder()).thenReturn(orders);
        when(orderService.findNoAuditOrder(any(Pageable.class))).thenReturn(orderPage);
        when(orderVoService.returnVo(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/reservation_manage"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/reservation_manage"))
                .andExpect(model().attributeExists("total"))
                .andExpect(model().attributeExists("order_list"));

        verify(orderService, times(1)).findAuditOrder();
        verify(orderService, times(1)).findNoAuditOrder(any(Pageable.class));
        verify(orderVoService, times(1)).returnVo(any());
    }

    @Test
    public void testGetNoAuditOrder() throws Exception {
        List<Order> orders = Arrays.asList(new Order(), new Order());
        Page<Order> orderPage = new PageImpl<>(orders);
        List<OrderVo> orderVos = Arrays.asList(new OrderVo(), new OrderVo());

        when(orderService.findNoAuditOrder(any(Pageable.class))).thenReturn(orderPage);
        when(orderVoService.returnVo(any())).thenReturn(orderVos);

        mockMvc.perform(get("/admin/getOrderList.do")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
        verify(orderService, times(1)).findNoAuditOrder(any(Pageable.class));
        verify(orderVoService, times(1)).returnVo(any());
    }

    @Test
    public void testConfirmOrder() throws Exception {
        mockMvc.perform(post("/passOrder.do")
                        .param("orderID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    public void testRejectOrder() throws Exception {
        mockMvc.perform(post("/rejectOrder.do")
                        .param("orderID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }
}
