package com.charging.charging.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("charging_fault")
public class ChargingFault {

    @TableId(type = IdType.AUTO)
    private Long faultId;

    private Long pileId;

    private Integer slotId;

    private Long userId;

    private String faultType;

    private String description;

    private String images;

    private String contactPhone;

    private Integer status;

    private String handlerResponse;

    private Long handlerId;

    private LocalDateTime handledAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
