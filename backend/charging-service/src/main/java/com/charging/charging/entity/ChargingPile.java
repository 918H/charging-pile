package com.charging.charging.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("charging_pile")
public class ChargingPile implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "pile_id", type = IdType.AUTO)
    private Long pileId;

    @TableField("pile_name")
    private String pileName;

    @TableField("pile_number")
    private String pileNumber;

    @TableField("location_name")
    private String locationName;

    @TableField("latitude")
    private Double latitude;

    @TableField("longitude")
    private Double longitude;

    @TableField("address")
    private String address;

    @TableField("total_slots")
    private Integer totalSlots;

    @TableField("power_type")
    private String powerType;

    @TableField("voltage")
    private Integer voltage;

    @TableField("current_capacity")
    private Integer currentCapacity;

    @TableField("power_rating")
    private Integer powerRating;

    @TableField("operator_id")
    private Long operatorId;

    @TableField("status")
    private Integer status;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
