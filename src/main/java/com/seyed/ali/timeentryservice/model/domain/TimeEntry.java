package com.seyed.ali.timeentryservice.model.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TimeEntry implements Serializable {

    @Id
    private String timeEntryId;
    @Builder.Default
    private boolean billable = false;
    @Builder.Default
    private BigDecimal hourlyRate = BigDecimal.ZERO;

    @OneToMany(mappedBy = "timeEntry", cascade = CascadeType.ALL)
    @ToString.Exclude
    @Builder.Default
    private List<TimeSegment> timeSegmentList = new ArrayList<>();

    private String userId;
    private String projectId;
    private String taskId;

}
