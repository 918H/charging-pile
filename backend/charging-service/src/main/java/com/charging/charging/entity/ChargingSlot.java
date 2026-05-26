package com.charging.charging.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("charging_slot")
public class ChargingSlot implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "slot_id", type = IdType.AUTO)
    private Long slotId;

    @TableField("pile_id")
    private Long pileId;

    @TableField("slot_number")
    private Integer slotNumber;

    @TableField("socket_type")
    private String socketType;

    @TableField("status")
    private Integer status;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
