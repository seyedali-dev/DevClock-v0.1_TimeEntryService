package com.seyed.ali.timeentryservice.service;

import com.seyed.ali.timeentryservice.client.AuthenticationServiceClient;
import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import com.seyed.ali.timeentryservice.model.dto.TimeEntryDTO;
import com.seyed.ali.timeentryservice.repository.TimeEntryRepository;
import com.seyed.ali.timeentryservice.util.TimeParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeEntryServiceImplTest {

    //<editor-fold desc="mocks">
    private @InjectMocks TimeEntryServiceImpl timeEntryService;
    private @Mock TimeEntryRepository timeEntryRepository;
    private @Mock AuthenticationServiceClient authenticationServiceClient;
    private @Mock TimeParser timeParser;
    //</editor-fold>

    //<editor-fold desc="fields">
    private String startTimeStr;
    private String endTimeStr;
    private String durationStr;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Duration duration;
    private TimeEntry timeEntry;
    //</editor-fold>

    //<editor-fold desc="setup">
    @BeforeEach
    void setUp() {
        this.startTimeStr = "2024-05-11 08:00:00";
        this.endTimeStr = "2024-05-11 10:00:00";
        this.durationStr = "02:00:00";
        this.startTime = LocalDateTime.parse(this.startTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.endTime = LocalDateTime.parse(this.endTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.duration = this.parseStringToDuration(this.durationStr);

        this.timeEntry = new TimeEntry();
        this.timeEntry.setId(UUID.randomUUID().toString());
        this.timeEntry.setStartTime(startTime);
        this.timeEntry.setEndTime(endTime);
        this.timeEntry.setDuration(duration);
    }
    //</editor-fold>

    @Test
    void getTimeEntries() {
        // given
        when(this.timeEntryRepository.findAll())
                .thenReturn(List.of(this.timeEntry));
        when(this.timeParser.parseLocalDateTimeToString(isA(LocalDateTime.class)))
                .thenReturn(this.endTimeStr);
        when(this.timeParser.parseDurationToString(isA(Duration.class)))
                .thenReturn(this.durationStr);

        // when
        List<TimeEntryDTO> result = this.timeEntryService.getTimeEntries();

        // then
        assertThat(result)
                .as("Must not be null")
                .isNotNull()
                .as("Must have 1 value")
                .hasSize(1);
        assertThat(result.getFirst().duration())
                .as("Must be equal to = PT2H")
                .isEqualTo(parseDurationToString(timeEntry.getDuration()));
    }

    public String parseDurationToString(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Test
    void getUsersTimeEntry() {
        // given
        when(this.timeEntryRepository.findByUserId(isA(String.class)))
                .thenReturn(this.timeEntry);
        when(this.timeParser.parseLocalDateTimeToString(isA(LocalDateTime.class)))
                .thenReturn(this.endTimeStr);
        when(this.timeParser.parseDurationToString(isA(Duration.class)))
                .thenReturn(this.durationStr);

        // when
        TimeEntryDTO result = this.timeEntryService.getUsersTimeEntry("some_user_id");

        // then
        assertThat(result)
                .as("Must not be null")
                .isNotNull();
        assertThat(result.duration())
                .as("Must be equal to = PT2H")
                .isEqualTo(parseDurationToString(this.timeEntry.getDuration()));
    }

    @Test
    public void addTimeEntryManuallyTest_WithDuration() {
        TimeEntryDTO timeEntryDTO = new TimeEntryDTO(null, this.startTimeStr, this.endTimeStr, this.durationStr);

        when(this.timeParser.parseStringToLocalDateTime(this.startTimeStr))
                .thenReturn(this.startTime);
        when(this.timeParser.parseStringToLocalDateTime(this.endTimeStr))
                .thenReturn(this.endTime);
        when(this.timeParser.parseStringToDuration(this.durationStr))
                .thenReturn(this.duration);
        when(this.authenticationServiceClient.getCurrentLoggedInUsersId())
                .thenReturn(null);
        when(this.timeEntryRepository.save(isA(TimeEntry.class)))
                .thenReturn(this.timeEntry);
        when(this.timeParser.parseTimeToString(this.startTime, this.endTime, this.duration))
                .thenReturn("startTime(2024-05-11 08:00:00) | endTime(2024-05-11 10:00:00) | duration(02:00:00)");

        // When
        String result = this.timeEntryService.addTimeEntryManually(timeEntryDTO);

        // Then
        assertThat(result)
                .as("Must not be null")
                .isNotNull()
                .isEqualTo("startTime(2024-05-11 08:00:00) | endTime(2024-05-11 10:00:00) | duration(02:00:00)");
        verify(this.timeEntryRepository, times(1))
                .save(isA(TimeEntry.class));
    }

    private Duration parseStringToDuration(String string) {
        String[] durationParts = string.split(":");
        long hours = Long.parseLong(durationParts[0]);
        long minutes = Long.parseLong(durationParts[1]);
        long seconds = Long.parseLong(durationParts[2]);
        return Duration
                .ofHours(hours)
                .plusMinutes(minutes)
                .plusSeconds(seconds);
    }

    @Test
    public void addTimeEntryManuallyTest_WithoutDuration() {
        // Given
        TimeEntryDTO timeEntryDTO = new TimeEntryDTO(null, this.startTimeStr, this.endTimeStr, null);

        when(this.timeParser.parseStringToLocalDateTime(this.startTimeStr))
                .thenReturn(this.startTime);
        when(this.timeParser.parseStringToLocalDateTime(this.endTimeStr))
                .thenReturn(this.endTime);
        when(this.authenticationServiceClient.getCurrentLoggedInUsersId())
                .thenReturn(null);
        when(this.timeEntryRepository.save(isA(TimeEntry.class)))
                .thenReturn(this.timeEntry);
        when(this.timeParser.parseTimeToString(this.startTime, this.endTime, this.duration))
                .thenReturn("startTime(2024-05-11 08:00:00) | endTime(2024-05-11 10:00:00) | duration(02:00:00)");

        // When
        String result = this.timeEntryService.addTimeEntryManually(timeEntryDTO);

        // Then
        assertThat(result)
                .as("Must not be null")
                .isNotNull()
                .isEqualTo("startTime(2024-05-11 08:00:00) | endTime(2024-05-11 10:00:00) | duration(02:00:00)");
        verify(this.timeEntryRepository, times(1))
                .save(isA(TimeEntry.class));
    }

}