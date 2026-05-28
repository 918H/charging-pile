package com.charging.common.db;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果封装
 * 
 * @param <T> 数据类型
 */
@Data
public class PageResult<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 当前页数据
     */
    private List<T> records;
    
    /**
     * 总记录数
     */
    private long total;
    
    /**
     * 当前页码
     */
    private long current;
    
    /**
     * 每页大小
     */
    private long size;
    
    /**
     * 总页数
     */
    private long pages;
    
    /**
     * 是否有上一页
     */
    private boolean hasPrevious;
    
    /**
     * 是否有下一页
     */
    private boolean hasNext;
    
    /**
     * 从 MyBatis-Plus IPage 转换
     */
    public static <T> PageResult<T> fromIPage(IPage<T> iPage) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(iPage.getRecords());
        result.setTotal(iPage.getTotal());
        result.setCurrent(iPage.getCurrent());
        result.setSize(iPage.getSize());
        result.setPages(iPage.getPages());
        result.setHasPrevious(iPage.getCurrent() > 1);
        result.setHasNext(iPage.getCurrent() < iPage.getPages());
        return result;
    }
    
    /**
     * 创建分页参数
     */
    public static Page<?> createPage(long current, long size) {
        return new Page<>(current, size);
    }
}
