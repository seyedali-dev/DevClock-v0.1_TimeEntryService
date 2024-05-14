package com.seyed.ali.timeentryservice.repository;

import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, String> {

    TimeEntry findByUserId(String userId);

    TimeEntry findByUserIdAndTimeEntryId(String userId, String id);

}