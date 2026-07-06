package com.tuituidan.openhub.bean.vo;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * AccessStatVo.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2026/7/6
 */
@Getter
@Setter
@Accessors(chain = true)
public class AccessStatVo {

    private AccessStatSummaryVo total;

    private AccessStatSummaryVo today;

    private List<AccessStatRecordVo> visits;

    private List<AccessStatRecordVo> logins;

}
