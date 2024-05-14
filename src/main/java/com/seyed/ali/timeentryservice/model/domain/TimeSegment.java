package com.seyed.ali.timeentryservice.model.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TimeSegment {

    @Id
    private String timeSegmentId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Duration duration;

    @ManyToOne(cascade = CascadeType.ALL)
    private TimeEntry timeEntry;

}