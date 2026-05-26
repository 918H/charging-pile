package com.charging.user.service;

import com.charging.user.entity.SignInRecord;
import java.util.Map;

public interface SignInService {
    Map<String, Object> signIn(Long userId);
    Integer getContinuousDays(Long userId);
    Map<String, Object> getCalendar(Long userId, Integer year, Integer month);
    boolean makeUpSignIn(Long userId, java.time.LocalDate date);
}
