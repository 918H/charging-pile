package com.charging.support.controller;

import com.charging.common.core.response.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/support/ticket")
public class TicketController {
    
    @PostMapping("/create")
    public R createTicket() {
        return R.ok("工单创建成功");
    }
    
    @GetMapping("/list")
    public R listTickets() {
        return R.ok("工单列表");
    }
    
    @PutMapping("/{id}/assign")
    public R assignTicket(@PathVariable Long id) {
        return R.ok("工单分配成功");
    }
}
