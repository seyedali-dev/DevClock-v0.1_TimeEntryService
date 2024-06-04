package com.seyed.ali.timeentryservice.event;

import com.seyed.ali.timeentryservice.model.payload.ProjectDTO;
import com.seyed.ali.timeentryservice.repository.TimeEntryRepository;
import com.seyed.ali.timeentryservice.service.interfaces.TimeEntryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectEventListener {

    private final TimeEntryService timeEntryService;
    private final TimeEntryRepository timeEntryRepository;

    @KafkaListener(
            topics = "${spring.kafka.topic.name}",
            groupId = "${spring.kafka.consumer.group-id}" // if we change the group-id name, the events from the previous data will also be logged, otherwise, only the new events will be logged!
    )
    public void listenProject(@Payload ConsumerRecord<String, ProjectDTO> record) {
        ProjectDTO projectDTO = record.value();
        log.info("Received message: {}", projectDTO);

        this.timeEntryRepository.findByProjectId(projectDTO.getProjectId())
                .forEach(this.timeEntryService::deleteTimeEntry);
    }

}
