package com.charging.support.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charging.common.core.response.R;
import com.charging.support.entity.SupportTicket;
import com.charging.support.entity.TicketReply;
import com.charging.support.mapper.SupportTicketMapper;
import com.charging.support.mapper.TicketReplyMapper;
import com.charging.support.service.TicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
@Slf4j
@Service
public class TicketServiceImpl implements TicketService {
    @Resource private SupportTicketMapper ticketMapper;
    @Resource private TicketReplyMapper replyMapper;
    @Override
    public R list(String status, Integer priority) {
        log.info("查询工单列表：status={}, priority={}", status, priority);
        LambdaQueryWrapper<SupportTicket> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(SupportTicket::getStatus, status);
        if (priority != null) wrapper.eq(SupportTicket::getPriority, priority);
        wrapper.orderByDesc(SupportTicket::getCreateTime);
        List<SupportTicket> list = ticketMapper.selectList(wrapper);
        return R.ok(list);
    }
    @Override
    public R create(Long userId, String type, String content) {
        log.info("创建工单：userId={}, type={}", userId, type);
        SupportTicket ticket = new SupportTicket();
        ticket.setTicketNo("T" + System.currentTimeMillis());
        ticket.setUserId(userId);
        ticket.setType(type);
        ticket.setContent(content);
        ticket.setStatus("pending");
        ticket.setPriority("normal");
        ticketMapper.insert(ticket);
        return R.ok(ticket.getId());
    }
    @Override
    public R assign(Long ticketId, Long handlerId) {
        log.info("分配工单：ticketId={}, handlerId={}", ticketId, handlerId);
        SupportTicket ticket = ticketMapper.selectById(ticketId);
        if (ticket != null) {
            ticket.setHandlerId(handlerId);
            ticket.setStatus("processing");
            ticketMapper.updateById(ticket);
        }
        return R.ok("分配成功");
    }
    @Override
    public R reply(Long ticketId, Long userId, String content) {
        log.info("回复工单：ticketId={}", ticketId);
        TicketReply reply = new TicketReply();
        reply.setTicketId(ticketId);
        reply.setUserId(userId);
        reply.setContent(content);
        reply.setType(1);
        replyMapper.insert(reply);
        return R.ok("回复成功");
    }
    @Override
    public R close(Long ticketId, String result) {
        log.info("关闭工单：ticketId={}", ticketId);
        SupportTicket ticket = ticketMapper.selectById(ticketId);
        if (ticket != null) {
            ticket.setStatus("closed");
            ticketMapper.updateById(ticket);
        }
        return R.ok("工单已关闭");
    }
    @Override
    public R detail(Long ticketId) {
        log.info("查询工单详情：ticketId={}", ticketId);
        SupportTicket ticket = ticketMapper.selectById(ticketId);
        return R.ok(ticket);
    }
}
