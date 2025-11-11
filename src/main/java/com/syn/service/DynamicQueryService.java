package com.syn.service;

import java.util.List;
import java.util.Map;

public interface DynamicQueryService {

    /**
     * 查询任意表的所有结果
     * @param tableName 表名
     * @param columns 要查询的列字段列表
     * @return 查询结果列表
     */
    List<Map<String, Object>> queryTable(String tableName, List<String> columns);

    /**
     * 条件查询任意表，List层为OR关系，Map层为AND关系
     * @param tableName 表名
     * @param columns 要查询的列字段列表
     * @param conditions 条件列表，List中的每个Map表示一组AND条件，List中的多个Map之间是OR关系
     * @return 查询结果列表
     */
    List<Map<String, Object>> queryWithConditions(String tableName, List<String> columns, 
                                                             List<Map<String, Object>> conditions);

    /**
     * 分页查询任意表
     * @param tableName 表名
     * @param columns 要查询的列字段列表
     * @param offset 起始点
     * @param limit 偏移长度
     * @return 查询结果列表
     */
    List<Map<String, Object>> queryWithPagination(String tableName, List<String> columns, 
                                                              int offset, int limit);

    /**
     * 获取表的总记录数
     * @param tableName 表名
     * @return 总记录数
     */
    int getTotalCount(String tableName);

}