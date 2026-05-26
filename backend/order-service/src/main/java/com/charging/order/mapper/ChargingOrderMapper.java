package com.charging.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.order.entity.ChargingOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChargingOrderMapper extends BaseMapper<ChargingOrder> {
}
