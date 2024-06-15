package com.seyed.ali.timeentryservice.model.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonManagedReference // this is the forward part of the relationship – the one that gets serialized normally
    private List<TimeSegment> timeSegmentList = new ArrayList<>();

    private String userId;
    private String projectId;
    private String taskId;

}
