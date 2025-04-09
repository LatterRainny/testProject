package com.demo.unitTest;

import com.demo.dao.OrderDao;
import com.demo.dao.VenueDao;
import com.demo.entity.Order;
import com.demo.entity.Venue;
import com.demo.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.demo.service.OrderService.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    // 模拟 OrderDao 依赖
    @Mock
    private OrderDao orderDao;

    // 模拟 VenueDao 依赖
    @Mock
    private VenueDao venueDao;

    // 注入上面模拟对象到 OrderServiceImpl 实例中
    @InjectMocks
    private OrderServiceImpl orderService;

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
        order.setState(STATE_NO_AUDIT);
        order.setOrderTime(LocalDateTime.now());
        order.setStartTime(LocalDateTime.of(2025, 5, 1, 10, 0));
        order.setHours(2);
        order.setTotal(200);

        // 初始化测试 Venue 对象
        venue = new Venue();
        venue.setVenueID(100);
        venue.setVenueName("Test Venue");
        // 假设场馆价格为 100 元/小时
        venue.setPrice(100);
    }

    /**
     * 测试 findById 方法
     * 验证：当传入存在的 orderID 时，返回对应的 Order 对象
     */
    @Test
    public void testFindById() {
        // 模拟 orderDao.getOne 方法返回测试 Order 对象
        when(orderDao.getOne(1)).thenReturn(order);

        Order result = orderService.findById(1);
        assertNotNull(result);
        assertEquals(1, result.getOrderID());
        verify(orderDao, times(1)).getOne(1);
    }

    /**
     * 测试 findNoAuditOrder 方法
     * 验证：分页查询返回的订单全部为未审核状态（STATE_NO_AUDIT）
     */
    @Test
    public void testFindNoAuditOrder() {
        Pageable pageable = PageRequest.of(0, 10);
        // 构造返回的分页数据，此处只包含一个订单
        Page<Order> page = new PageImpl<>(Collections.singletonList(order), pageable, 1);
        when(orderDao.findAllByState(STATE_NO_AUDIT, pageable)).thenReturn(page);

        Page<Order> result = orderService.findNoAuditOrder(pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        result.getContent().forEach(o -> assertEquals(STATE_NO_AUDIT, o.getState()));
        verify(orderDao, times(1)).findAllByState(STATE_NO_AUDIT, pageable);
    }

    /**
     * 测试 findAuditOrder 方法
     * 验证：返回审核中的订单（STATE_WAIT 或 STATE_FINISH）
     */
    @Test
    public void testFindAuditOrder() {
        // 构造审核中的订单（状态设置为 STATE_WAIT 和 STATE_FINISH）
        Order orderWait = new Order();
        orderWait.setOrderID(2);
        orderWait.setUserID("user2");
        orderWait.setVenueID(101);
        orderWait.setState(STATE_WAIT);
        orderWait.setOrderTime(LocalDateTime.now());
        orderWait.setStartTime(LocalDateTime.of(2025, 5, 2, 14, 0));
        orderWait.setHours(3);
        orderWait.setTotal(300);

        Order orderFinish = new Order();
        orderFinish.setOrderID(3);
        orderFinish.setUserID("user3");
        orderFinish.setVenueID(102);
        orderFinish.setState(STATE_FINISH);
        orderFinish.setOrderTime(LocalDateTime.now());
        orderFinish.setStartTime(LocalDateTime.of(2025, 5, 3, 16, 0));
        orderFinish.setHours(2);
        orderFinish.setTotal(200);

        List<Order> list = Arrays.asList(orderWait, orderFinish);
        when(orderDao.findAudit(STATE_WAIT, STATE_FINISH)).thenReturn(list);

        List<Order> result = orderService.findAuditOrder();
        assertNotNull(result);
        assertEquals(2, result.size());
        // 验证返回的订单状态均为 STATE_WAIT 或 STATE_FINISH
        result.forEach(o -> assertTrue(o.getState() == STATE_WAIT || o.getState() == STATE_FINISH));
        verify(orderDao, times(1)).findAudit(STATE_WAIT, STATE_FINISH);
    }

    /**
     * 测试 findDateOrder 方法
     * 验证：根据场馆 ID 和时间区间返回对应订单列表
     */
    @Test
    public void testFindDateOrder() {
        LocalDateTime start = LocalDateTime.of(2025, 5, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 5, 2, 0, 0);

        List<Order> orders = Collections.singletonList(order);
        when(orderDao.findByVenueIDAndStartTimeIsBetween(100, start, end)).thenReturn(orders);

        List<Order> result = orderService.findDateOrder(100, start, end);
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderDao, times(1)).findByVenueIDAndStartTimeIsBetween(100, start, end);
    }

    /**
     * 测试 findUserOrder 方法
     * 验证：分页查询返回指定用户的订单列表
     */
    @Test
    public void testFindUserOrder() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> page = new PageImpl<>(Collections.singletonList(order), pageable, 1);
        when(orderDao.findAllByUserID("user1", pageable)).thenReturn(page);

        Page<Order> result = orderService.findUserOrder("user1", pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        result.getContent().forEach(o -> assertEquals("user1", o.getUserID()));
        verify(orderDao, times(1)).findAllByUserID("user1", pageable);
    }

    /**
     * 测试 updateOrder 方法
     * 验证：根据传入的参数更新订单信息，并调用 save 保存更新后的订单
     */
    @Test
    public void testUpdateOrder() {
        // 假设更新的订单 ID 为 1，传入新场馆名称、开始时间、预订小时数、用户 ID
        String newVenueName = "Test Venue";
        LocalDateTime newStartTime = LocalDateTime.of(2025, 6, 1, 9, 0);
        int newHours = 4;
        String newUserID = "user1";

        // 模拟 venueDao 根据场馆名称返回 Venue 对象
        when(venueDao.findByVenueName(newVenueName)).thenReturn(venue);
        // 模拟 orderDao 根据订单 ID 返回订单对象
        when(orderDao.findByOrderID(1)).thenReturn(order);

        // 调用 updateOrder 方法
        orderService.updateOrder(1, newVenueName, newStartTime, newHours, newUserID);

        // 验证订单状态重置为 STATE_NO_AUDIT，并更新其他属性
        assertEquals(STATE_NO_AUDIT, order.getState());
        assertEquals(newHours, order.getHours());
        assertEquals(venue.getVenueID(), order.getVenueID());
        assertEquals(newStartTime, order.getStartTime());
        assertEquals(newUserID, order.getUserID());
        assertEquals(newHours * venue.getPrice(), order.getTotal());

        // 验证 orderDao.save 方法被调用一次
        verify(orderDao, times(1)).save(order);
    }

    /**
     * 测试 submit 方法
     * 验证：新建订单，并保存到数据库
     */
    @Test
    public void testSubmit() {
        String venueName = "Test Venue";
        LocalDateTime startTime = LocalDateTime.of(2025, 7, 1, 15, 0);
        int hours = 3;
        String userID = "user2";

        // 模拟 venueDao 根据场馆名称返回 Venue 对象
        when(venueDao.findByVenueName(venueName)).thenReturn(venue);
        // 模拟 orderDao.save 方法返回保存后的订单（设置订单 ID 为 2）
        Order newOrder = new Order();
        newOrder.setOrderID(2);
        newOrder.setState(STATE_NO_AUDIT);
        newOrder.setHours(hours);
        newOrder.setVenueID(venue.getVenueID());
        newOrder.setStartTime(startTime);
        newOrder.setUserID(userID);
        newOrder.setTotal(hours * venue.getPrice());
        when(orderDao.save(any(Order.class))).thenReturn(newOrder);

        // 调用 submit 方法
        orderService.submit(venueName, startTime, hours, userID);
        // 验证 orderDao.save 方法被调用一次
        verify(orderDao, times(1)).save(any(Order.class));
    }

    /**
     * 测试 delOrder 方法
     * 验证：调用 delOrder 后调用 deleteById 删除订单
     */
    @Test
    public void testDelOrder() {
        orderService.delOrder(1);
        verify(orderDao, times(1)).deleteById(1);
    }

    /**
     * 测试 confirmOrder 方法
     * 验证：订单存在时，调用 updateState 将订单状态更新为 STATE_WAIT
     */
    @Test
    public void testConfirmOrder() {
        when(orderDao.findByOrderID(1)).thenReturn(order);
        orderService.confirmOrder(1);
        verify(orderDao, times(1)).updateState(STATE_WAIT, order.getOrderID());
    }

    /**
     * 测试 confirmOrder 方法 - 当订单不存在时抛出异常
     */
    @Test
    public void testConfirmOrder_NotFound() {
        when(orderDao.findByOrderID(999)).thenReturn(null);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> orderService.confirmOrder(999));
        assertEquals("订单不存在", ex.getMessage());
    }

    /**
     * 测试 finishOrder 方法
     * 验证：订单存在时，将订单状态更新为 STATE_FINISH
     */
    @Test
    public void testFinishOrder() {
        when(orderDao.findByOrderID(1)).thenReturn(order);
        orderService.finishOrder(1);
        verify(orderDao, times(1)).updateState(STATE_FINISH, order.getOrderID());
    }

    /**
     * 测试 finishOrder 方法 - 当订单不存在时抛出异常
     */
    @Test
    public void testFinishOrder_NotFound() {
        when(orderDao.findByOrderID(999)).thenReturn(null);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> orderService.finishOrder(999));
        assertEquals("订单不存在", ex.getMessage());
    }

    /**
     * 测试 rejectOrder 方法
     * 验证：订单存在时，将订单状态更新为 STATE_REJECT
     */
    @Test
    public void testRejectOrder() {
        when(orderDao.findByOrderID(1)).thenReturn(order);
        orderService.rejectOrder(1);
        verify(orderDao, times(1)).updateState(STATE_REJECT, order.getOrderID());
    }

    /**
     * 测试 rejectOrder 方法 - 当订单不存在时抛出异常
     */
    @Test
    public void testRejectOrder_NotFound() {
        when(orderDao.findByOrderID(999)).thenReturn(null);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> orderService.rejectOrder(999));
        assertEquals("订单不存在", ex.getMessage());
    }
}
