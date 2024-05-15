package com.seyed.ali.timeentryservice.repository;

import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, String> {

    Optional<TimeEntry> findByUserId(String userId);

    TimeEntry findByUserIdAndTimeEntryId(String userId, String id);

}