package com.charging.charging.dto;

import lombok.Data;

@Data
public class FaultReportRequest {
    private Long pileId;
    private Integer slotId;
    private Long userId;
    private String faultType;
    private String description;
    private String images;
    private String contactPhone;
}
