package com.charging.common.db;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.context.annotation.Bean;

/**
 * MyBatis-Plus 配置类
 * 
 * @MapperScan 各服务自行配置扫描路径
 */
public class MybatisPlusConfig {

    /**
     * 分页插件配置
     * 支持自动识别数据库类型
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}

/**
 * 基础 Mapper 接口
 * 所有 Mapper 接口继承此接口，获得基础 CRUD 能力
 * 
 * @param <T> 实体类型
 */
@Mapper
interface BaseMapperCustom<T> extends BaseMapper<T> {
    // 可扩展自定义方法
}
