package com.demo.unitTest;

import com.demo.dao.VenueDao;
import com.demo.entity.Venue;
import com.demo.service.impl.VenueServiceImpl;
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

/**
 * VenueServiceImpl 的单元测试类
 * 使用 JUnit5 和 Mockito 模拟 VenueDao，验证各个方法逻辑是否正确
 */
@ExtendWith(MockitoExtension.class)
public class VenueServiceImplTest {

    // 模拟 VenueDao，避免直接访问数据库
    @Mock
    private VenueDao venueDao;

    // 注入模拟的 VenueDao 到 VenueServiceImpl 实例中
    @InjectMocks
    private VenueServiceImpl venueService;

    // 测试用的 Venue 实例
    private Venue venue;

    /**
     * 在每个测试方法执行前初始化测试数据
     */
    @BeforeEach
    public void setUp() {
        venue = new Venue();
        venue.setVenueID(1);
        venue.setVenueName("Stadium One");
        venue.setDescription("A great venue");
        venue.setPrice(100);
        venue.setPicture("pic.png");
        venue.setAddress("123 Street");
        venue.setOpen_time("09:00");
        venue.setClose_time("21:00");
    }

    /**
     * 测试：根据 id 查询场馆信息
     * 模拟调用 DAO 的 getOne 方法返回测试数据，验证返回的 Venue 对象是否正确
     */
    @Test
    public void testFindByVenueID() {
        // 模拟当调用 getOne(1) 时返回 venue 对象
        when(venueDao.getOne(1)).thenReturn(venue);
        Venue result = venueService.findByVenueID(1);
        assertNotNull(result);  // 验证返回结果不为空
        assertEquals(1, result.getVenueID());  // 验证场馆 id 是否匹配
        verify(venueDao, times(1)).getOne(1);  // 验证 DAO 的 getOne 方法调用了一次
    }

    /**
     * 测试：根据场馆名称查询
     * 模拟 DAO 的 findByVenueName 方法返回测试数据，验证返回的 Venue 对象是否正确
     */
    @Test
    public void testFindByVenueName() {
        // 模拟当调用 findByVenueName("Stadium One") 时返回 venue 对象
        when(venueDao.findByVenueName("Stadium One")).thenReturn(venue);
        Venue result = venueService.findByVenueName("Stadium One");
        assertNotNull(result);  // 验证返回结果不为空
        assertEquals("Stadium One", result.getVenueName());  // 验证场馆名称是否匹配
        verify(venueDao, times(1)).findByVenueName("Stadium One");  // 验证 DAO 的方法调用次数
    }

    /**
     * 测试：分页查询所有场馆
     * 模拟 DAO 的 findAll(Pageable) 方法返回分页数据，验证分页信息及内容是否正确
     */
    @Test
    public void testFindAllPageable() {
        // 构造分页参数：第一页，每页最多 10 条记录
        Pageable pageable = PageRequest.of(0, 10);
        List<Venue> list = Collections.singletonList(venue);
        // 使用 PageImpl 构造分页结果
        Page<Venue> page = new PageImpl<>(list, pageable, list.size());
        // 模拟 DAO 返回分页结果
        when(venueDao.findAll(pageable)).thenReturn(page);
        Page<Venue> result = venueService.findAll(pageable);
        assertNotNull(result);  // 验证返回结果不为空
        assertEquals(1, result.getTotalElements());  // 验证总记录数是否正确
        verify(venueDao, times(1)).findAll(pageable);  // 验证 DAO 方法调用次数
    }

    /**
     * 测试：查询所有场馆（不分页）
     * 模拟 DAO 的 findAll 方法返回场馆列表，验证返回数据是否正确
     */
    @Test
    public void testFindAll() {
        List<Venue> list = Arrays.asList(venue);
        // 模拟 DAO 返回场馆列表
        when(venueDao.findAll()).thenReturn(list);
        List<Venue> result = venueService.findAll();
        assertNotNull(result);  // 验证返回结果不为空
        assertFalse(result.isEmpty());  // 验证列表不为空
        verify(venueDao, times(1)).findAll();  // 验证 DAO 方法调用次数
    }

    /**
     * 测试：创建新的场馆
     * 模拟 DAO 的 save 方法返回保存后的场馆对象，并验证返回的场馆 id 是否正确
     */
    @Test
    public void testCreate() {
        // 模拟 save 方法返回保存后的 venue 对象
        when(venueDao.save(venue)).thenReturn(venue);
        int id = venueService.create(venue);
        assertEquals(1, id);  // 验证返回的场馆 id 是否与预期一致
        verify(venueDao, times(1)).save(venue);  // 验证 DAO 的 save 方法调用次数
    }

    /**
     * 测试：更新场馆信息
     * 调用 update 方法后，内部会调用 DAO 的 save 方法，验证是否正确调用
     */
    @Test
    public void testUpdate() {
        // 调用更新方法
        venueService.update(venue);
        // 验证 DAO 的 save 方法被调用了一次
        verify(venueDao, times(1)).save(venue);
    }

    /**
     * 测试：删除场馆
     * 验证调用删除方法后，DAO 的 deleteById 方法是否被调用
     */
    @Test
    public void testDelById() {
        // 调用删除方法
        venueService.delById(1);
        // 验证 DAO 的 deleteById 方法被调用一次
        verify(venueDao, times(1)).deleteById(1);
    }

    /**
     * 测试：根据场馆名称统计场馆数量
     * 模拟 DAO 的 countByVenueName 方法返回统计值，验证返回结果是否正确
     */
    @Test
    public void testCountVenueName() {
        // 模拟当调用 countByVenueName("Stadium One") 返回 1
        when(venueDao.countByVenueName("Stadium One")).thenReturn(1);
        int count = venueService.countVenueName("Stadium One");
        assertEquals(1, count);  // 验证统计值是否正确
        verify(venueDao, times(1)).countByVenueName("Stadium One");  // 验证 DAO 方法调用次数
    }
}
