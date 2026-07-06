package com.tuituidan.openhub.service;

import com.tuituidan.openhub.bean.entity.AccessStatLog;
import com.tuituidan.openhub.bean.vo.AccessStatRecordVo;
import com.tuituidan.openhub.bean.vo.AccessStatSummaryVo;
import com.tuituidan.openhub.bean.vo.AccessStatVo;
import com.tuituidan.openhub.repository.AccessStatLogRepository;
import com.tuituidan.openhub.util.RequestUtils;
import com.tuituidan.openhub.util.StringExtUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/**
 * AccessStatService.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2026/7/6
 */
@Service
public class AccessStatService {

    @Resource
    private AccessStatLogRepository accessStatLogRepository;

    /**
     * recordVisit
     *
     * @return AccessStatSummaryVo
     */
    public AccessStatSummaryVo recordVisit() {
        record(AccessStatLog.TYPE_VISIT);
        return todaySummary();
    }

    /**
     * recordLogin
     */
    public void recordLogin(boolean success) {
        record(AccessStatLog.TYPE_LOGIN, success, null);
    }

    /**
     * recordLogin
     */
    public void recordLogin(boolean success, HttpServletRequest request) {
        record(AccessStatLog.TYPE_LOGIN, success, request);
    }

    /**
     * totalSummary
     *
     * @return AccessStatSummaryVo
     */
    public AccessStatSummaryVo totalSummary() {
        return new AccessStatSummaryVo()
                .setVisitCount(accessStatLogRepository.countByType(AccessStatLog.TYPE_VISIT))
                .setLoginCount(accessStatLogRepository.countByType(AccessStatLog.TYPE_LOGIN));
    }

    /**
     * todaySummary
     *
     * @return AccessStatSummaryVo
     */
    public AccessStatSummaryVo todaySummary() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return new AccessStatSummaryVo()
                .setVisitCount(accessStatLogRepository.countToday(AccessStatLog.TYPE_VISIT, start, end))
                .setLoginCount(accessStatLogRepository.countToday(AccessStatLog.TYPE_LOGIN, start, end));
    }

    /**
     * todayDetail
     *
     * @return AccessStatVo
     */
    public AccessStatVo todayDetail() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return new AccessStatVo()
                .setTotal(totalSummary())
                .setToday(todaySummary())
                .setVisits(toRecordVo(accessStatLogRepository.findToday(AccessStatLog.TYPE_VISIT, start, end)))
                .setLogins(toRecordVo(accessStatLogRepository.findToday(AccessStatLog.TYPE_LOGIN, start, end)));
    }

    private void record(String type) {
        record(type, null, null);
    }

    private void record(String type, Boolean success, HttpServletRequest request) {
        accessStatLogRepository.save(new AccessStatLog()
                .setId(StringExtUtils.getUuid())
                .setType(type)
                .setEventTime(LocalDateTime.now())
                .setIp(request == null ? RequestUtils.getClientIp() : RequestUtils.getClientIp(request))
                .setSuccess(success));
    }

    private List<AccessStatRecordVo> toRecordVo(List<AccessStatLog> logs) {
        return logs.stream()
                .map(item -> new AccessStatRecordVo()
                        .setEventTime(item.getEventTime())
                        .setIp(item.getIp())
                        .setSuccess(item.getSuccess()))
                .collect(Collectors.toList());
    }

}
