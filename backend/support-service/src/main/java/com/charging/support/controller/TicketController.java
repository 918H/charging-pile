package com.charging.support.controller;
import com.charging.common.core.response.R;
import com.charging.support.service.TicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
@Slf4j
@RestController
@RequestMapping("/support/ticket")
public class TicketController {
    @Resource private TicketService ticketService;
    @GetMapping("/list")
    public R list(@RequestParam(required = false) String status,
                  @RequestParam(required = false) Integer priority) {
        return ticketService.list(status, priority);
    }
    @PostMapping("/create")
    public R create(@RequestParam Long userId, @RequestParam String type,
                    @RequestParam String content) {
        return ticketService.create(userId, type, content);
    }
    @PutMapping("/{id}/assign")
    public R assign(@PathVariable Long id, @RequestParam Long handlerId) {
        return ticketService.assign(id, handlerId);
    }
    @PostMapping("/{id}/reply")
    public R reply(@PathVariable Long id, @RequestParam Long userId,
                   @RequestParam String content) {
        return ticketService.reply(id, userId, content);
    }
    @PutMapping("/{id}/close")
    public R close(@PathVariable Long id, @RequestParam String result) {
        return ticketService.close(id, result);
    }
    @GetMapping("/{id}/detail")
    public R detail(@PathVariable Long id) {
        return ticketService.detail(id);
    }
}
