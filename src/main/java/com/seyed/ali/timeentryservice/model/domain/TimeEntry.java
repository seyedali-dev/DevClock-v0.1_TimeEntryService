package com.seyed.ali.timeentryservice.model.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TimeEntry {

    @Id
    private String timeEntryId;

    @OneToMany(mappedBy = "timeEntry", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<TimeSegment> timeSegmentList = new ArrayList<>();

    private String userId;

}
