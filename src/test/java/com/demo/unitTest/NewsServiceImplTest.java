package com.demo.unitTest;

import com.demo.dao.NewsDao;
import com.demo.entity.News;
import com.demo.service.impl.NewsServiceImpl;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NewsServiceImplTest {

    // 模拟 NewsDao 依赖
    @Mock
    private NewsDao newsDao;

    // 将模拟对象注入到 NewsServiceImpl 实例中
    @InjectMocks
    private NewsServiceImpl newsService;

    // 测试用的 News 对象
    private News news;

    @BeforeEach
    public void setUp() {
        // 初始化测试 News 对象
        news = new News();
        news.setNewsID(1);
        news.setTitle("Test Title");
        news.setContent("Test Content");
        news.setTime(LocalDateTime.now());
    }

    /**
     * 测试 findAll 方法
     * 模拟 newsDao.findAll 返回分页数据，并验证返回结果是否正确
     */
    @Test
    public void testFindAll() {
        // 构造分页参数，第一页，每页 10 条记录
        PageRequest pageable = PageRequest.of(0, 10);
        // 构造返回的分页数据，这里只包含一个测试 News 对象
        Page<News> page = new PageImpl<>(Arrays.asList(news));
        // 模拟 newsDao.findAll 方法返回构造好的分页数据
        when(newsDao.findAll(pageable)).thenReturn(page);

        // 调用 service 方法
        Page<News> result = newsService.findAll(pageable);
        // 断言返回的分页数据不为空，并且总记录数为 1
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        // 验证 newsDao.findAll 方法被调用一次
        verify(newsDao, times(1)).findAll(pageable);
    }

    /**
     * 测试 findById 方法
     * 模拟 newsDao.getOne 根据 newsID 返回数据，并验证返回的 News 对象是否正确
     */
    @Test
    public void testFindById() {
        // 模拟 newsDao.getOne 方法返回测试 News 对象
        when(newsDao.getOne(1)).thenReturn(news);

        // 调用 service 方法
        News result = newsService.findById(1);
        // 断言返回结果不为空，并且各字段与测试数据一致
        assertNotNull(result);
        assertEquals(news.getNewsID(), result.getNewsID());
        assertEquals(news.getTitle(), result.getTitle());
        // 验证 newsDao.getOne 方法被调用一次
        verify(newsDao, times(1)).getOne(1);
    }

    /**
     * 测试 create 方法
     * 模拟 newsDao.save 方法返回保存后的 News 对象，并验证返回的 newsID 是否正确
     */
    @Test
    public void testCreate() {
        // 构造待创建的 News 对象（通常 ID 为 0 或不设置）
        News newNews = new News();
        newNews.setTitle("New Title");
        newNews.setContent("New Content");
        newNews.setTime(LocalDateTime.now());
        // 构造保存后的 News 对象，此处模拟数据库生成的主键为 2
        News savedNews = new News(2, "New Title", "New Content", LocalDateTime.now());
        // 模拟 newsDao.save 方法返回保存后的对象
        when(newsDao.save(newNews)).thenReturn(savedNews);

        // 调用 service 的 create 方法，并验证返回的 newsID
        int id = newsService.create(newNews);
        assertEquals(2, id);
        // 验证 newsDao.save 方法被调用一次
        verify(newsDao, times(1)).save(newNews);
    }

    /**
     * 测试 delById 方法
     * 验证调用 delById 后，newsDao.deleteById 方法是否被正确调用
     */
    @Test
    public void testDelById() {
        // 调用 service 的删除方法
        newsService.delById(1);
        // 验证 newsDao.deleteById 方法被调用一次，参数为 1
        verify(newsDao, times(1)).deleteById(1);
    }

    /**
     * 测试 update 方法
     * 验证调用 update 后，newsDao.save 方法是否被正确调用
     */
    @Test
    public void testUpdate() {
        // 模拟修改后的新闻内容
        news.setTitle("Updated Title");
        news.setContent("Updated Content");
        // 调用 service 的更新方法
        newsService.update(news);
        // 验证 newsDao.save 方法被调用一次，参数为修改后的 news 对象
        verify(newsDao, times(1)).save(news);
    }
}
