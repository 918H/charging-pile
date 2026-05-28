package com.charging.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.message.entity.UserMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMessageMapper extends BaseMapper<UserMessage> {
}
