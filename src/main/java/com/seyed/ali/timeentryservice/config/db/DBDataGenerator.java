package com.seyed.ali.timeentryservice.config.db;

import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import com.seyed.ali.timeentryservice.model.domain.TimeSegment;
import com.seyed.ali.timeentryservice.repository.TimeEntryRepository;
import com.seyed.ali.timeentryservice.repository.TimeSegmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class DBDataGenerator {

    private final TimeEntryRepository timeEntryRepository;
    private final TimeSegmentRepository timeSegmentRepository;
    private int counter = 0;

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            // user -> default, project -> 1
            this.timeEntryRepository.saveAll(this.generateTimeEntries("c3140cf9-1552-4e38-bfb5-3f9e99f65ea3", "1", "1"));
            this.timeEntryRepository.saveAll(this.generateTimeEntries("c3140cf9-1552-4e38-bfb5-3f9e99f65ea3", "1", "2"));
            // user -> default, project -> 2
            this.timeEntryRepository.saveAll(this.generateTimeEntries("c3140cf9-1552-4e38-bfb5-3f9e99f65ea3", "2", "3"));
            this.timeEntryRepository.saveAll(this.generateTimeEntries("c3140cf9-1552-4e38-bfb5-3f9e99f65ea3", "2", "4"));
        };
    }

    /**
     * Generates a list of TimeEntry objects for a given user, project, and task.
     * Each TimeEntry is associated with a list of TimeSegment objects.
     * <p>
     * Data will be entered for a month, and each day will have 2 {@link TimeEntry} and each TimeEntry will have 5 {@link TimeSegment}.
     *
     * @param userId    The ID of the user for whom the TimeEntry objects are generated.
     * @param projectId The ID of the project for which the TimeEntry objects are generated.
     * @param taskId    The ID of the task for which the TimeEntry objects are generated.
     * @return A list of TimeEntry objects.
     */
    public List<TimeEntry> generateTimeEntries(String userId, String projectId, String taskId) {
        List<TimeEntry> timeEntryList = new ArrayList<>();

        // Define the start and end dates for the TimeEntry objects
        LocalDateTime start = LocalDateTime.now().minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime end = start.plusMonths(1);

        // Iterate over each day between the start and end dates
        for (LocalDateTime date = start; date.isBefore(end); date = date.plusDays(1)) {
            // Create 2 TimeEntry objects for each day
            for (int i = 0; i < 2; i++) {
                // Instantiate a new list for each TimeEntry
                List<TimeSegment> timeSegmentList = new ArrayList<>();

                // Create a new TimeEntry object
                TimeEntry timeEntry = new TimeEntry();
                timeEntry.setTimeEntryId("ID_" + ++this.counter);
                timeEntry.setUserId(userId);
                timeEntry.setProjectId(projectId);
                timeEntry.setTaskId(taskId);

                // Generate TimeSegments for this TimeEntry
                for (int j = 0; j < 5; j++) { // Each TimeEntry will have 5 TimeSegments
                    LocalDateTime startTime = date.withHour(9 + j); // Start time is between 9:00 and 13:00
                    LocalDateTime endTime = startTime.plusHours(1); // Each TimeSegment lasts 1 hour

                    // Create a new TimeSegment object
                    TimeSegment timeSegment = new TimeSegment();
                    timeSegment.setTimeSegmentId(UUID.randomUUID().toString());
                    timeSegment.setStartTime(startTime);
                    timeSegment.setEndTime(endTime);
                    timeSegment.setDuration(Duration.between(startTime, endTime));
                    timeSegment.setTimeEntry(timeEntry);

                    // Add the TimeSegment to the list
                    timeSegmentList.add(timeSegment);
                }

                // Associate the list of TimeSegments with the TimeEntry
                timeEntry.setTimeSegmentList(timeSegmentList);

                // Save the TimeSegments to the database
                this.timeSegmentRepository.saveAll(timeSegmentList);

                // Add the TimeEntry to the list
                timeEntryList.add(timeEntry);
            }
        }

        // Return the list of TimeEntry objects
        return timeEntryList;
    }


}
