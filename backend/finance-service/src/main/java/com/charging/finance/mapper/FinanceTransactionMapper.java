package com.charging.finance.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.finance.entity.FinanceTransaction;
import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface FinanceTransactionMapper extends BaseMapper<FinanceTransaction> {}
