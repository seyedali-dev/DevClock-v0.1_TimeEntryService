package com.seyed.ali.timeentryservice.service;

import com.seyed.ali.timeentryservice.client.AuthenticationServiceClient;
import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import com.seyed.ali.timeentryservice.model.domain.TimeSegment;
import com.seyed.ali.timeentryservice.model.dto.TimeEntryDTO;
import com.seyed.ali.timeentryservice.repository.TimeEntryRepository;
import com.seyed.ali.timeentryservice.util.TimeEntryUtility;
import com.seyed.ali.timeentryservice.util.TimeParser;
import com.seyed.ali.timeentryservice.util.TimeParserUtilForTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
    private @Mock TimeEntryUtility timeEntryUtility;

    private String startTimeStr;
    private String endTimeStr;
    private String durationStr;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Duration duration;
    private TimeEntry timeEntry;
    private TimeSegment timeSegment;

    @BeforeEach
    void setUp() {
        this.startTimeStr = "2024-05-11 08:00:00";
        this.endTimeStr = "2024-05-11 10:00:00";
        this.durationStr = "02:00:00";
        this.startTime = LocalDateTime.parse(this.startTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.endTime = LocalDateTime.parse(this.endTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.duration = this.parseStringToDuration(this.durationStr);

        this.timeSegment = new TimeSegment();
        this.timeSegment.setTimeSegmentId("1");
        this.timeSegment.setStartTime(startTime);
        this.timeSegment.setEndTime(endTime);
        this.timeSegment.setDuration(duration);

        this.timeEntry = new TimeEntry();
        this.timeEntry.setTimeEntryId(UUID.randomUUID().toString());
//        this.timeEntry.setTimeSegmentList(List.of(this.timeSegment)); // this is immutable; so we cannot add or remove any more data
        this.timeEntry.setTimeSegmentList(new ArrayList<>(List.of(this.timeSegment)));
    }

    @Test
    public void startTrackingTimeEntryTest() {
        // Arrange
        String userId = "testUserId";
        TimeSegment timeSegment = new TimeSegment();
        timeSegment.setStartTime(LocalDateTime.now());
        timeSegment.setEndTime(null);
        timeSegment.setDuration(Duration.ZERO);

        TimeEntry timeEntry = new TimeEntry();
        timeEntry.setUserId(userId);
        timeEntry.getTimeSegmentList().add(timeSegment);

        when(this.timeEntryUtility.createNewTimeEntry(isA(Boolean.class), isA(BigDecimal.class), isA(AuthenticationServiceClient.class)))
                .thenReturn(this.timeEntry);
        when(this.timeEntryRepository.save(isA(TimeEntry.class))).thenReturn(timeEntry);

        // Act
        String timeEntryId = this.timeEntryTrackingService.startTrackingTimeEntry(false, BigDecimal.ONE);
        System.out.println(timeEntryId);

        // Assert
        TimeSegment firstTimeSegment = timeEntry.getTimeSegmentList().getFirst();
        assertThat(timeEntryId).isNotNull();
        assertThat(timeEntry.getTimeSegmentList()).hasSize(1);
        assertThat(firstTimeSegment.getStartTime()).isNotNull();
        assertThat(firstTimeSegment.getEndTime()).isNull();
        assertThat(firstTimeSegment.getDuration()).isEqualTo(Duration.ZERO);

        verify(this.timeEntryRepository, times(1))
                .save(isA(TimeEntry.class));
    }

    @Test
    public void stopTrackingTimeEntryTest() {
        // Arrange
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String userId = "testUserId";
        LocalDateTime startTime = LocalDateTime.of(2024, 5, 11, 8, 0, 0);
        LocalDateTime endTime = LocalDateTime.now();
        String formattedEndTimeStr = endTime.format(formatter);
        Duration duration = Duration.between(startTime, endTime);

        TimeSegment timeSegment = new TimeSegment();
        timeSegment.setTimeSegmentId("1");
        timeSegment.setStartTime(startTime);
        timeSegment.setEndTime(endTime);
        timeSegment.setDuration(duration);

        TimeEntry timeEntry = new TimeEntry();
        timeEntry.setTimeEntryId("1");
        timeEntry.setUserId(userId);
        timeEntry.getTimeSegmentList().add(timeSegment);

        when(this.authenticationServiceClient.getCurrentLoggedInUsersId()).thenReturn(userId);
        when(this.timeEntryRepository.findByUserIdAndTimeEntryId(userId, timeEntry.getTimeEntryId())).thenReturn(timeEntry);
        when(this.timeParser.parseLocalDateTimeToString(any(LocalDateTime.class)))
                .thenAnswer(invocation -> {
                    LocalDateTime dateTime = invocation.getArgument(0);
                    System.out.println("dateTime: " + dateTime);
                    return dateTime.format(formatter);
                });
        when(this.timeParser.parseDurationToString(any(Duration.class))).thenReturn(duration.toString());

        // Act
        TimeEntryDTO result = this.timeEntryTrackingService.stopTrackingTimeEntry(timeEntry.getTimeEntryId());
        System.out.println("result: " + result);

        // Assert
        assertThat(result.startTime()).isEqualTo("2024-05-11 08:00:00");
        assertThat(result.endTime()).isEqualTo(formattedEndTimeStr);
        assertThat(result.duration()).isEqualTo(duration.toString());

        verify(this.timeEntryRepository, times(1)).save(any(TimeEntry.class));
    }

    @Test
    public void continueTrackingTimeEntryTest() {
        // Arrange
        String userId = "testUserId";
        String timeEntryId = "1";
        LocalDateTime continueTime = LocalDateTime.now();
        String formattedContinueTimeStr = continueTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        TimeSegment timeSegment = new TimeSegment();
        timeSegment.setStartTime(continueTime);
        timeSegment.setEndTime(null);
        timeSegment.setDuration(Duration.ZERO);

        TimeEntry timeEntry = new TimeEntry();
        timeEntry.setTimeEntryId(timeEntryId);
        timeEntry.setUserId(userId);
        timeEntry.getTimeSegmentList().add(timeSegment);

        when(this.authenticationServiceClient.getCurrentLoggedInUsersId()).thenReturn(userId);
        when(this.timeEntryRepository.findByUserIdAndTimeEntryId(isA(String.class), isA(String.class))).thenReturn(timeEntry);
        doNothing()
                .when(this.timeEntryUtility)
                .continueTimeEntry(isA(TimeEntry.class), isA(LocalDateTime.class));
        when(this.timeEntryRepository.save(isA(TimeEntry.class))).thenReturn(timeEntry);
        when(this.timeParser.parseLocalDateTimeToString(isA(LocalDateTime.class)))
                .thenReturn(formattedContinueTimeStr);

        // Act
        TimeEntryDTO result = this.timeEntryTrackingService.continueTrackingTimeEntry(timeEntryId);
        System.out.println(result);

        // Assert
        assertThat(result.timeEntryId()).isEqualTo(timeEntryId);
        assertThat(result.startTime()).isEqualTo(formattedContinueTimeStr);
        assertThat(result.endTime()).isNull();
        assertThat(result.duration()).isNull();

        TimeSegment firstTimeSegment = timeEntry.getTimeSegmentList().getFirst();
        assertThat(timeEntry.getTimeSegmentList()).hasSize(1);
        assertThat(firstTimeSegment.getStartTime()).isNotNull();
        assertThat(firstTimeSegment.getEndTime()).isNull();
        assertThat(firstTimeSegment.getDuration()).isEqualTo(Duration.ZERO);

        verify(this.timeEntryRepository, times(1)).save(any(TimeEntry.class));
    }

}