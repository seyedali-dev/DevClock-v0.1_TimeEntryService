package com.seyed.ali.timeentryservice.event;

import com.seyed.ali.timeentryservice.model.payload.ProjectDTO;
import com.seyed.ali.timeentryservice.repository.TimeEntryRepository;
import com.seyed.ali.timeentryservice.service.interfaces.TimeEntryQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectEventService {

    private final TimeEntryQueryService timeEntryQueryService;
    private final TimeEntryRepository timeEntryRepository;

    public void handleDeleteOperation(ProjectDTO projectDTO) {
        log.info("\"Delete\" project, associated tasks & time entries: {}", projectDTO);
        this.timeEntryRepository.findByProjectId(projectDTO.getProjectId())
                .forEach(this.timeEntryQueryService::deleteTimeEntry);
    }

    public void handleDetachOperation(ProjectDTO projectDTO) {
        log.info("\"Detach\" project, associated tasks & time entries: {}", projectDTO);
        this.timeEntryRepository.findByProjectId(projectDTO.getProjectId())
                .forEach(timeEntry -> {
                    timeEntry.setProjectId(null);
                    timeEntry.setTaskId(null);
                    this.timeEntryRepository.save(timeEntry);
                });
    }

}
