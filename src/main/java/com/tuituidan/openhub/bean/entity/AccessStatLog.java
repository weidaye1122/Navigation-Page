package com.tuituidan.openhub.bean.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * AccessStatLog.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2026/7/6
 */
@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "nav_access_stat_log", schema = "team_nav")
public class AccessStatLog {

    public static final String TYPE_VISIT = "visit";

    public static final String TYPE_LOGIN = "login";

    @Id
    @Column(name = "id", length = 32)
    private String id;

    @Column(name = "type", length = 20)
    private String type;

    @Column(name = "event_time")
    private LocalDateTime eventTime;

    @Column(name = "ip", length = 64)
    private String ip;

    @Column(name = "success")
    private Boolean success;

}
