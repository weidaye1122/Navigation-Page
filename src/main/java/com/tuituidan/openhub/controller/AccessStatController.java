package com.tuituidan.openhub.controller;

import com.tuituidan.openhub.bean.vo.AccessStatSummaryVo;
import com.tuituidan.openhub.bean.vo.AccessStatVo;
import com.tuituidan.openhub.consts.Consts;
import com.tuituidan.openhub.service.AccessStatService;
import javax.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AccessStatController.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2026/7/6
 */
@RestController
@RequestMapping(Consts.API_V1 + "/access-statistics")
public class AccessStatController {

    @Resource
    private AccessStatService accessStatService;

    /**
     * visit
     *
     * @return AccessStatSummaryVo
     */
    @PostMapping("/visit")
    public ResponseEntity<AccessStatSummaryVo> visit() {
        return ResponseEntity.ok(accessStatService.recordVisit());
    }

    /**
     * todaySummary
     *
     * @return AccessStatSummaryVo
     */
    @GetMapping("/summary")
    public ResponseEntity<AccessStatSummaryVo> todaySummary() {
        return ResponseEntity.ok(accessStatService.todaySummary());
    }

    /**
     * todayDetail
     *
     * @return AccessStatVo
     */
    @GetMapping("/today")
    public ResponseEntity<AccessStatVo> todayDetail() {
        return ResponseEntity.ok(accessStatService.todayDetail());
    }

}
