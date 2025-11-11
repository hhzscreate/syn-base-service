package com.syn.service.impl;

import com.syn.mapper.DynamicQueryMapper;
import com.syn.service.DynamicQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

@Service
public class DynamicQueryServiceImpl implements DynamicQueryService {

    @Autowired
    private DynamicQueryMapper dynamicQueryMapper;

    @Override
    public List<Map<String, Object>> queryTable(String tableName, List<String> columns) {
        validateParameters(tableName, columns);
        return dynamicQueryMapper.queryTable(tableName, columns);
    }

    @Override
    public List<Map<String, Object>> queryWithConditions(String tableName, List<String> columns, 
                                                             List<Map<String, Object>> conditions) {
        validateParameters(tableName, columns);
        Assert.notEmpty(conditions, "Conditions cannot be empty");
        
        // 验证条件结构
        for (Map<String, Object> condition : conditions) {
            Assert.notEmpty(condition, "Each condition map cannot be empty");
        }
        
        return dynamicQueryMapper.queryWithConditions(tableName, columns, conditions);
    }

    @Override
    public List<Map<String, Object>> queryWithPagination(String tableName, List<String> columns, 
                                                              int offset, int limit) {
        validateParameters(tableName, columns);
        Assert.isTrue(offset >= 0, "Offset must be greater than or equal to 0");
        Assert.isTrue(limit > 0, "Limit must be greater than 0");
        
        return dynamicQueryMapper.queryWithPagination(tableName, columns, offset, limit);
    }

    @Override
    public int getTotalCount(String tableName) {
        Assert.hasText(tableName, "Table name cannot be empty");
        return dynamicQueryMapper.getTotalCount(tableName);
    }

    /**
     * 验证通用参数
     */
    private void validateParameters(String tableName, List<String> columns) {
        Assert.hasText(tableName, "Table name cannot be empty");
        Assert.notEmpty(columns, "Columns list cannot be empty");
        
        // 简单的SQL注入防护检查
        validateTableName(tableName);
        for (String column : columns) {
            validateColumnName(column);
        }
    }

    /**
     * 验证表名，防止SQL注入
     */
    private void validateTableName(String tableName) {
        // 只允许字母、数字和下划线
        if (!tableName.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Invalid table name. Only letters, numbers and underscores are allowed.");
        }
    }

    /**
     * 验证列名，防止SQL注入
     */
    private void validateColumnName(String columnName) {
        // 只允许字母、数字、下划线和星号
        if (!columnName.matches("^[a-zA-Z0-9_*]+$")) {
            throw new IllegalArgumentException("Invalid column name. Only letters, numbers, underscores and asterisk are allowed.");
        }
    }

}