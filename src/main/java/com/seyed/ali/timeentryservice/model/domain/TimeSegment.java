package com.seyed.ali.timeentryservice.model.domain;

import jakarta.persistence.*;
import lombok.*;

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