package com.charging.support.service;
import com.charging.common.core.response.R;
public interface TicketService {
    R list(String status, Integer priority);
    R create(Long userId, String type, String content);
    R assign(Long ticketId, Long handlerId);
    R reply(Long ticketId, Long userId, String content);
    R close(Long ticketId, String result);
    R detail(Long ticketId);
}
