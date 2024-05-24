package com.seyed.ali.timeentryservice.service;

import com.seyed.ali.timeentryservice.client.ProjectServiceClient;
import com.seyed.ali.timeentryservice.exceptions.OperationNotSupportedException;
import com.seyed.ali.timeentryservice.exceptions.ResourceNotFoundException;
import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import com.seyed.ali.timeentryservice.repository.TimeEntryRepository;
import com.seyed.ali.timeentryservice.service.interfaces.TimeEntryAssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeEntryAssignmentServiceImpl implements TimeEntryAssignmentService {

    private final ProjectServiceClient projectServiceClient;
    private final TimeEntryRepository timeEntryRepository;

    @Override
    @Transactional
    public String assignProject(String timeEntryId, String projectId) {
        if (this.projectServiceClient.isProjectValid(projectId)) {
            TimeEntry timeEntry = this.timeEntryRepository.findById(timeEntryId)
                    .orElseThrow(() -> {
                        log.error("time entry not found.");
                        return new ResourceNotFoundException("TimeEntry with id " + timeEntryId + " not found");
                    });
            timeEntry.setProjectId(projectId);
            this.timeEntryRepository.save(timeEntry);
            return projectId;
        }
        throw new OperationNotSupportedException("Something went wrong validating project :(");
    }

}
