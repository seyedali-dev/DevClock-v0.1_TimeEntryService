package com.seyed.ali.timeentryservice.model.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TimeEntry {

    @Id
    private String id;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Duration duration;

    private String userId;

}
