package com.tuituidan.openhub.bean.vo;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * AccessStatRecordVo.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2026/7/6
 */
@Getter
@Setter
@Accessors(chain = true)
public class AccessStatRecordVo {

    private LocalDateTime eventTime;

    private String ip;

    private Boolean success;

}
