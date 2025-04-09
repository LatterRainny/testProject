package com.demo.unitTest;

import com.demo.dao.OrderDao;
import com.demo.dao.VenueDao;
import com.demo.entity.Order;
import com.demo.entity.Venue;
import com.demo.entity.vo.OrderVo;
import com.demo.service.impl.OrderVoServiceImpl;
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
public class OrderVoServiceImplTest {

    // 模拟 OrderDao 依赖
    @Mock
    private OrderDao orderDao;

    // 模拟 VenueDao 依赖
    @Mock
    private VenueDao venueDao;

    // 将上面的模拟对象注入到 OrderVoServiceImpl 实例中
    @InjectMocks
    private OrderVoServiceImpl orderVoService;

    // 测试用的 Order 对象
    private Order order;
    // 测试用的 Venue 对象
    private Venue venue;

    @BeforeEach
    public void setUp() {
        // 初始化测试 Order 对象
        order = new Order();
        order.setOrderID(1);
        order.setUserID("user1");
        order.setVenueID(100);
        order.setState(1); // 例如：1 未审核
        order.setOrderTime(LocalDateTime.now());
        order.setStartTime(LocalDateTime.of(2025, 5, 1, 10, 0));
        order.setHours(2);
        order.setTotal(200);

        // 初始化测试 Venue 对象
        venue = new Venue();
        venue.setVenueID(100);
        venue.setVenueName("Test Venue");
        // 此处如果需要还可以设置其他字段（如价格等），但对 OrderVoServiceImpl 来说只需场馆名称
    }

    /**
     * 测试 returnOrderVoByOrderID 方法
     * 模拟 OrderDao 与 VenueDao 返回数据，验证返回的 OrderVo 对象各字段转换正确
     */
    @Test
    public void testReturnOrderVoByOrderID() {
        // 模拟 OrderDao 根据 orderID 返回 Order 对象
        when(orderDao.findByOrderID(1)).thenReturn(order);
        // 模拟 VenueDao 根据 venueID 返回 Venue 对象
        when(venueDao.findByVenueID(100)).thenReturn(venue);

        // 调用 service 方法转换为 OrderVo
        OrderVo orderVo = orderVoService.returnOrderVoByOrderID(1);

        // 验证转换后的 OrderVo 不为空，各字段值与测试数据一致
        assertNotNull(orderVo);
        assertEquals(order.getOrderID(), orderVo.getOrderID());
        assertEquals(order.getUserID(), orderVo.getUserID());
        assertEquals(order.getVenueID(), orderVo.getVenueID());
        assertEquals(venue.getVenueName(), orderVo.getVenueName());
        assertEquals(order.getState(), orderVo.getState());
        assertEquals(order.getOrderTime(), orderVo.getOrderTime());
        assertEquals(order.getStartTime(), orderVo.getStartTime());
        assertEquals(order.getHours(), orderVo.getHours());
        assertEquals(order.getTotal(), orderVo.getTotal());

        // 验证模拟方法被正确调用
        verify(orderDao, times(1)).findByOrderID(1);
        verify(venueDao, times(1)).findByVenueID(100);
    }

    /**
     * 测试 returnVo 方法
     * 传入 Order 对象集合，验证方法内部调用 returnOrderVoByOrderID 将每个 Order 转换为 OrderVo 后返回
     */
    @Test
    public void testReturnVo() {
        // 模拟 OrderDao 与 VenueDao 返回数据
        when(orderDao.findByOrderID(1)).thenReturn(order);
        when(venueDao.findByVenueID(100)).thenReturn(venue);

        // 构造包含一个 Order 对象的列表
        List<Order> orderList = Collections.singletonList(order);
        // 调用方法转换列表
        List<OrderVo> voList = orderVoService.returnVo(orderList);

        // 验证转换后的集合不为空，且集合大小与传入列表一致
        assertNotNull(voList);
        assertEquals(1, voList.size());

        OrderVo orderVo = voList.get(0);
        // 验证 OrderVo 对象各字段值正确
        assertEquals(order.getOrderID(), orderVo.getOrderID());
        assertEquals(order.getUserID(), orderVo.getUserID());
        assertEquals(order.getVenueID(), orderVo.getVenueID());
        assertEquals(venue.getVenueName(), orderVo.getVenueName());
        assertEquals(order.getState(), orderVo.getState());
        assertEquals(order.getOrderTime(), orderVo.getOrderTime());
        assertEquals(order.getStartTime(), orderVo.getStartTime());
        assertEquals(order.getHours(), orderVo.getHours());
        assertEquals(order.getTotal(), orderVo.getTotal());

        // 验证在转换过程中，findByOrderID 与 findByVenueID 方法分别被调用一次
        verify(orderDao, times(1)).findByOrderID(1);
        verify(venueDao, times(1)).findByVenueID(100);
    }

    /**
     * 测试 returnVo 方法：当传入空列表时，返回空列表
     */
    @Test
    public void testReturnVo_EmptyList() {
        // 调用方法传入空集合
        List<OrderVo> voList = orderVoService.returnVo(Collections.emptyList());
        // 验证返回的集合为空
        assertNotNull(voList);
        assertTrue(voList.isEmpty());

        // 验证 DAO 方法不会被调用
        verify(orderDao, never()).findByOrderID(anyInt());
        verify(venueDao, never()).findByVenueID(anyInt());
    }
}
