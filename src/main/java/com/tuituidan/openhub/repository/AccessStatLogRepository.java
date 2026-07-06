package com.tuituidan.openhub.repository;

import com.tuituidan.openhub.bean.entity.AccessStatLog;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * AccessStatLogRepository.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2026/7/6
 */
public interface AccessStatLogRepository extends JpaRepository<AccessStatLog, String> {

    /**
     * countByType
     *
     * @param type type
     * @return long
     */
    long countByType(String type);

    /**
     * countToday
     *
     * @param type type
     * @param start start
     * @param end end
     * @return long
     */
    @Query("select count(u) from AccessStatLog u where u.type = ?1 and u.eventTime >= ?2 and u.eventTime < ?3")
    long countToday(String type, LocalDateTime start, LocalDateTime end);

    /**
     * findToday
     *
     * @param type type
     * @param start start
     * @param end end
     * @return List
     */
    @Query("select u from AccessStatLog u where u.type = ?1 and u.eventTime >= ?2 and u.eventTime < ?3 "
            + "order by u.eventTime desc")
    List<AccessStatLog> findToday(String type, LocalDateTime start, LocalDateTime end);

}
