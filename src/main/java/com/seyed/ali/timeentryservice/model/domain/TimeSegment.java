package com.seyed.ali.timeentryservice.model.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TimeSegment implements Serializable {

    @Id
    private String timeSegmentId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Duration duration;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonBackReference // this is the back part of the relationship – it will be omitted from serialization to avoid the infinite loop
    private TimeEntry timeEntry;

}