package com.seyed.ali.timeentryservice.service;

import com.seyed.ali.timeentryservice.client.AuthenticationServiceClient;
import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import com.seyed.ali.timeentryservice.repository.TimeEntryRepository;
import com.seyed.ali.timeentryservice.util.TimeParser;
import com.seyed.ali.timeentryservice.util.TimeParserUtilForTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
@ExtendWith(MockitoExtension.class)
class TimeEntryTrackingServiceImplTest extends TimeParserUtilForTests {

    private @InjectMocks TimeEntryTrackingServiceImpl timeEntryTrackingService;
    private @Mock TimeEntryRepository timeEntryRepository;
    private @Mock AuthenticationServiceClient authenticationServiceClient;
    private @Mock TimeParser timeParser;

    private String startTimeStr;
    private String endTimeStr;
    private String durationStr;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Duration duration;
    private TimeEntry timeEntry;

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

    @Test
    public void startTrackingTimeEntryTest() {
        // given
        when(this.authenticationServiceClient.getCurrentLoggedInUsersId())
                .thenReturn("some_user_id");
        when(this.timeEntryRepository.save(isA(TimeEntry.class)))
                .thenReturn(this.timeEntry);

        // when
        String timeEntryId = this.timeEntryTrackingService.startTrackingTimeEntry();

        // then
        assertThat(timeEntryId)
                .isNotNull();

        verify(this.timeEntryRepository, times(1))
                .save(isA(TimeEntry.class));
    }

    /*@Test // TODO: implement testing for this - it Fu**d me up :(
    public void stopTrackingTimeEntryTest() {
        // Arrange
        String userId = "testUserId";
        LocalDateTime startTime = LocalDateTime.of(2024, 5, 11, 8, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 5, 11, 10, 0, 0);

        TimeEntry timeEntry = new TimeEntry();
        timeEntry.setId("1");
        timeEntry.setStartTime(this.startTime);
        timeEntry.setUserId(userId);

        when(this.authenticationServiceClient.getCurrentLoggedInUsersId())
                .thenReturn(userId);

        when(this.timeEntryRepository.findByUserIdAndId(isA(String.class), isA(String.class)))
                .thenReturn(timeEntry);

        when(this.timeEntryRepository.save(isA(TimeEntry.class)))
                .thenReturn(timeEntry);

        when(this.timeParser.parseLocalDateTimeToString(this.startTime))
                .thenReturn(this.startTimeStr);

        LocalDateTime fixedEndTime = this.endTime;
        MockedStatic<LocalDateTime> localDateTimeMockedStatic = mockStatic(LocalDateTime.class);
        localDateTimeMockedStatic
                .when(LocalDateTime::now)
                .thenReturn(fixedEndTime);
        LocalDateTime endTimeNow = LocalDateTime.now();
        String endTimeNowStr = this.parseLocalDateTimeToString(fixedEndTime);
        when(this.timeParser.parseLocalDateTimeToString(endTimeNow))
                .thenReturn(endTimeNowStr);

        when(this.timeParser.parseDurationToString(isA(Duration.class)))
                .thenReturn(this.durationStr);

        // Act
        TimeEntryDTO result = this.timeEntryService.stopTrackingTimeEntry(timeEntry.getId());
        System.out.println(result);

        // Assert
        assertThat(result)
                .isNotNull();
        assertThat(result.startTime())
                .as("StartTimeString must be same")
                .isEqualTo(this.startTimeStr);
        assertThat(result.endTime())
                .as("EndTimeString must be same")
                .isEqualTo(this.endTimeStr);
        assertThat(result.duration())
                .as("DurationString must be same")
                .isEqualTo(this.durationStr);

        verify(this.timeEntryRepository).save(timeEntry);

        localDateTimeMockedStatic.close();
    }*/

    @Test
    public void continueTrackingTimeEntryTest() {/*
        // Given
        String timeEntryId = "test-id";
        String userId = "test-user-id";
        LocalDateTime startTime = this.startTime.minusHours(2);
        LocalDateTime endTime = this.endTime.minusHours(1);
        LocalDateTime continueTime = LocalDateTime.now();
        Duration previousDuration = Duration.between(startTime, endTime);
        Duration newDuration = Duration.between(endTime, continueTime);
        Duration totalDuration = previousDuration.plus(newDuration);

        TimeEntry timeEntry = new TimeEntry();
        timeEntry.setId(timeEntryId);
        timeEntry.setUserId(userId);
        timeEntry.setStartTime(startTime);
        timeEntry.setEndTime(endTime);
        timeEntry.setDuration(totalDuration);
        System.out.println(timeEntry);

        when(this.authenticationServiceClient.getCurrentLoggedInUsersId())
                .thenReturn(userId);
        when(this.timeEntryRepository.findByUserIdAndId(userId, timeEntryId))
                .thenReturn(timeEntry);
        when(this.timeEntryRepository.save(any(TimeEntry.class)))
                .thenReturn(timeEntry);
        when(this.timeParser.parseLocalDateTimeToString(any(LocalDateTime.class)))
                .thenReturn("test-time");
        when(this.timeParser.parseDurationToString(any(Duration.class)))
                .thenReturn("test-duration");

        // When
        TimeEntryDTO result = this.timeEntryTrackingService.continueTrackingTimeEntry(timeEntryId);
        System.out.println(result);

        // Then
        assertThat(result.startTime())
                .isEqualTo("test-time");
        assertThat(result.endTime())
                .isEqualTo("test-time");
        assertThat(result.duration())
                .isEqualTo("test-duration");

        verify(this.timeEntryRepository, times(1))
                .save(any(TimeEntry.class));*/
    }

}