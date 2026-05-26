package com.charging.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "user-service", contextId = "userClient")
public interface UserClient {

    @GetMapping("/membership/user/discount")
    BigDecimal calculateMembershipDiscount(@RequestParam("userId") Long userId, @RequestParam("amount") BigDecimal amount);

    @PostMapping("/points/add")
    Boolean addPoints(@RequestParam("userId") Long userId, @RequestParam("points") Integer points, 
                      @RequestParam("description") String description, @RequestParam("relatedOrder") String relatedOrder);
}
