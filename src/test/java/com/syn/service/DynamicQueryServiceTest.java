package com.syn.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class DynamicQueryServiceTest {

    @Autowired
    private DynamicQueryService dynamicQueryService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setup() {
        // 创建测试表
        jdbcTemplate.execute("DROP TABLE IF EXISTS test_user");
        jdbcTemplate.execute("CREATE TABLE test_user (id INT PRIMARY KEY, name VARCHAR(50), age INT, email VARCHAR(100))");
        
        // 插入测试数据
        jdbcTemplate.execute("INSERT INTO test_user VALUES (1, '张三', 25, 'zhangsan@syn.com')");
        jdbcTemplate.execute("INSERT INTO test_user VALUES (2, '李四', 30, 'lisi@syn.com')");
        jdbcTemplate.execute("INSERT INTO test_user VALUES (3, '王五', 35, 'wangwu@syn.com')");
        jdbcTemplate.execute("INSERT INTO test_user VALUES (4, '赵六', 28, 'zhaoliu@syn.com')");
        jdbcTemplate.execute("INSERT INTO test_user VALUES (5, '钱七', 40, 'qianqi@syn.com')");
    }

    // 辅助方法：检查Map中是否包含指定的键（不区分大小写）
    private boolean containsKeyIgnoreCase(Map<String, Object> map, String key) {
        for (String k : map.keySet()) {
            if (k.equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }
    
    @Test
    public void testQueryTable() {
        List<String> columns = Arrays.asList("id", "name", "age");
        List<Map<String, Object>> results = dynamicQueryService.queryTable("test_user", columns);
        
        assertNotNull(results);
        assertEquals(5, results.size());
        
        // 验证返回的字段（不区分大小写）
        for (Map<String, Object> row : results) {
            assertTrue(containsKeyIgnoreCase(row, "id"));
            assertTrue(containsKeyIgnoreCase(row, "name"));
            assertTrue(containsKeyIgnoreCase(row, "age"));
            assertFalse(containsKeyIgnoreCase(row, "email"));
        }
    }

    @Test
    public void testQueryWithConditions() {
        List<String> columns = Arrays.asList("id", "name", "age");
        
        // 测试OR条件：(id=1 AND age=25) OR (id=3 AND age=35)
        List<Map<String, Object>> conditions = new ArrayList<>();
        
        Map<String, Object> condition1 = new HashMap<>();
        condition1.put("id", 1);
        condition1.put("age", 25);
        
        Map<String, Object> condition2 = new HashMap<>();
        condition2.put("id", 3);
        condition2.put("age", 35);
        
        conditions.add(condition1);
        conditions.add(condition2);
        
        List<Map<String, Object>> results = dynamicQueryService.queryWithConditions("test_user", columns, conditions);
        
        assertNotNull(results);
        assertEquals(2, results.size());
        
        // 验证结果包含id为1和3的记录（不区分大小写）
        Set<Integer> ids = new HashSet<>();
        for (Map<String, Object> row : results) {
            Integer id = null;
            for (String key : row.keySet()) {
                if (key.equalsIgnoreCase("id")) {
                    id = (Integer) row.get(key);
                    break;
                }
            }
            assertNotNull(id, "ID should not be null");
            ids.add(id);
        }
        assertTrue(ids.contains(1));
        assertTrue(ids.contains(3));
    }

    @Test
    public void testQueryWithPagination() {
        List<String> columns = Arrays.asList("id", "name", "age");
        
        // 测试分页查询，从第0条开始，查询2条记录
        List<Map<String, Object>> results1 = dynamicQueryService.queryWithPagination("test_user", columns, 0, 2);
        assertNotNull(results1);
        assertEquals(2, results1.size());
        
        // 测试分页查询，从第2条开始，查询3条记录
        List<Map<String, Object>> results2 = dynamicQueryService.queryWithPagination("test_user", columns, 2, 3);
        assertNotNull(results2);
        assertEquals(3, results2.size());
        
        // 验证分页的正确性（不区分大小写）
        Set<Integer> page1Ids = new HashSet<>();
        for (Map<String, Object> row : results1) {
            Integer id = null;
            for (String key : row.keySet()) {
                if (key.equalsIgnoreCase("id")) {
                    id = (Integer) row.get(key);
                    break;
                }
            }
            assertNotNull(id, "ID should not be null");
            page1Ids.add(id);
        }
        assertTrue(page1Ids.contains(1) || page1Ids.contains(2));
        
        // 验证总数
        int totalCount = dynamicQueryService.getTotalCount("test_user");
        assertEquals(5, totalCount);
    }

    @Test
    public void testValidation() {
        List<String> columns = Arrays.asList("id", "name");
        
        // 测试表名验证
        assertThrows(IllegalArgumentException.class, () -> {
            dynamicQueryService.queryTable("test-user; DROP TABLE", columns);
        });
        
        // 测试列名验证
        assertThrows(IllegalArgumentException.class, () -> {
            List<String> invalidColumns = Arrays.asList("id", "name; DROP TABLE");
            dynamicQueryService.queryTable("test_user", invalidColumns);
        });
        
        // 测试空条件验证
        assertThrows(IllegalArgumentException.class, () -> {
            List<Map<String, Object>> emptyConditions = new ArrayList<>();
            dynamicQueryService.queryWithConditions("test_user", columns, emptyConditions);
        });
        
        // 测试分页参数验证
        assertThrows(IllegalArgumentException.class, () -> {
            dynamicQueryService.queryWithPagination("test_user", columns, -1, 2);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            dynamicQueryService.queryWithPagination("test_user", columns, 0, 0);
        });
    }

    @Test
    public void testWithInCondition() {
        List<String> columns = Arrays.asList("id", "name", "age");
        
        // 测试IN条件
        List<Map<String, Object>> conditions = new ArrayList<>();
        Map<String, Object> condition = new HashMap<>();
        condition.put("id", Arrays.asList(1, 2, 3));
        conditions.add(condition);
        
        List<Map<String, Object>> results = dynamicQueryService.queryWithConditions("test_user", columns, conditions);
        
        assertNotNull(results);
        assertEquals(3, results.size());
        
        // 验证结果包含id为1、2、3的记录（不区分大小写）
        Set<Integer> ids = new HashSet<>();
        for (Map<String, Object> row : results) {
            Integer id = null;
            for (String key : row.keySet()) {
                if (key.equalsIgnoreCase("id")) {
                    id = (Integer) row.get(key);
                    break;
                }
            }
            assertNotNull(id, "ID should not be null");
            ids.add(id);
        }
        assertTrue(ids.contains(1));
        assertTrue(ids.contains(2));
        assertTrue(ids.contains(3));
    }

}