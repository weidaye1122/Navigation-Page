package com.tuituidan.openhub.bean.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * AccessStatSummaryVo.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2026/7/6
 */
@Getter
@Setter
@Accessors(chain = true)
public class AccessStatSummaryVo {

    private long visitCount;

    private long loginCount;

}
