package com.seyed.ali.timeentryservice.repository;

import com.seyed.ali.timeentryservice.model.domain.TimeSegment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeSegmentRepository extends JpaRepository<TimeSegment, String> {
}