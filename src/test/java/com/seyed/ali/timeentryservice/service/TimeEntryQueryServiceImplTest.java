package com.seyed.ali.timeentryservice.service;

import com.seyed.ali.timeentryservice.client.AuthenticationServiceClient;
import com.seyed.ali.timeentryservice.client.ProjectServiceClient;
import com.seyed.ali.timeentryservice.exceptions.ResourceNotFoundException;
import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import com.seyed.ali.timeentryservice.model.domain.TimeSegment;
import com.seyed.ali.timeentryservice.model.payload.ProjectDTO;
import com.seyed.ali.timeentryservice.model.payload.TimeEntryDTO;
import com.seyed.ali.timeentryservice.model.payload.TimeSegmentDTO;
import com.seyed.ali.timeentryservice.model.payload.response.TimeEntryResponse;
import com.seyed.ali.timeentryservice.repository.TimeEntryRepository;
import com.seyed.ali.timeentryservice.repository.TimeSegmentRepository;
import com.seyed.ali.timeentryservice.service.cache.TimeEntryCacheManager;
import com.seyed.ali.timeentryservice.util.TimeEntryUtility;
import com.seyed.ali.timeentryservice.util.TimeParser;
import com.seyed.ali.timeentryservice.util.TimeParserUtilForTests;
import com.seyed.ali.timeentryservice.util.converter.TimeEntryConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@SuppressWarnings("unused")
@ExtendWith(MockitoExtension.class)
class TimeEntryQueryServiceImplTest extends TimeParserUtilForTests {

    private @InjectMocks TimeEntryQueryServiceImpl timeEntryService;
    private @Mock TimeEntryRepository timeEntryRepository;
    private @Mock TimeSegmentRepository timeSegmentRepository;
    private @Mock AuthenticationServiceClient authenticationServiceClient;
    private @Mock TimeParser timeParser;
    private @Mock TimeEntryUtility timeEntryUtility;
    private @Mock TimeEntryConverter timeEntryConverter;
    private @Mock TimeEntryCacheManager timeEntryCacheManager;
    private @Mock ProjectServiceClient projectServiceClient;

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
        this.timeEntry.getTimeSegmentList().add(this.timeSegment);

        TimeSegmentDTO timeSegmentDTO = new TimeSegmentDTO("1", this.startTimeStr, this.endTimeStr, this.durationStr, "userId");

        TimeEntryResponse timeEntryResponse = TimeEntryResponse.builder()
                .timeEntryId("1")
                .projectId("1")
                .taskId("1")
                .billable(false)
                .hourlyRate("10.0")
                .totalDuration(this.durationStr)
                .timeSegmentDTOList(List.of(timeSegmentDTO))
                .build();
    }

    @Test
    void getTimeEntries() {
        // given
        List<TimeEntry> timeEntryList = List.of(this.timeEntry);
        when(this.timeEntryRepository.findAll()).thenReturn(timeEntryList);

        // when
        List<TimeEntry> result = this.timeEntryService.getTimeEntries();
        System.out.println(result);

        // then
        assertThat(result)
                .as("Must not be null")
                .isNotNull()
                .as("Must have 1 value")
                .hasSize(1);
        Duration actualDuration = result.getFirst().getTimeSegmentList().getFirst().getDuration();
        String actualDurationStr = this.parseDurationToString(actualDuration);
        assertThat(actualDurationStr)
                .as("Must be equal to = PT2H")
                .isEqualTo(this.parseDurationToString(timeEntry.getTimeSegmentList().getLast().getDuration()));
    }

    @Test
    @DisplayName("getUsersTimeEntry should success when user is found")
    public void getUsersTimeEntry_UserIdValid_Success() {
        // given
        when(this.timeEntryRepository.findByUserId(isA(String.class))).thenReturn(Optional.ofNullable(this.timeEntry));

        // when
        TimeEntry result = this.timeEntryService.getUsersTimeEntry("some_user_id");
        System.out.println(result);

        // then
        assertThat(result)
                .as("Must not be null")
                .isNotNull();
        Duration actualDuration = result.getTimeSegmentList().getFirst().getDuration();
        String actualDurationStr = this.parseDurationToString(actualDuration);
        assertThat(actualDurationStr)
                .as("Must be equal to = PT2H")
                .isEqualTo(parseDurationToString(this.timeSegment.getDuration()));
    }

    @Test
    @DisplayName("getUsersTimeEntry should throw exception when user is not found")
    public void getUsersTimeEntry_UserIdInValid_ThrowResourceNotFoundException() {
        // given
        when(this.timeEntryRepository.findByUserId(isA(String.class))).thenThrow(new ResourceNotFoundException("User with id userId not found"));

        // when
        Throwable thrown = catchThrowable(() -> this.timeEntryService.getUsersTimeEntry("some_user_id"));
        System.out.println(thrown.getMessage());

        // then
        assertThat(thrown)
                .as("Must not be null")
                .isNotNull();
        assertThat(thrown.getMessage())
                .as("Must be equal to = User with id userId not found")
                .isEqualTo("User with id userId not found");
    }

    @Test
    @DisplayName("addTimeEntryManually should success when duration is present")
    public void addTimeEntryManually_WithDuration_Success() {
        TimeEntryDTO timeEntryDTO = TimeEntryDTO.builder()
                .timeEntryId(null)
                .projectId("1")
                .taskId("1")
                .startTime(this.startTimeStr)
                .endTime(this.endTimeStr)
                .duration(this.durationStr)
                .billable(false)
                .hourlyRate(BigDecimal.ZERO.toString())
                .build();

        when(this.timeEntryUtility.createTimeEntry(timeEntryDTO)).thenReturn(this.timeEntry);
        when(this.timeEntryRepository.save(isA(TimeEntry.class))).thenReturn(this.timeEntry);
        when(this.timeParser.parseTimeToString(this.startTime, this.endTime, this.duration)).thenReturn("startTime(2024-05-11 08:00:00) | endTime(2024-05-11 10:00:00) | duration(02:00:00)");

        // When
        String result = this.timeEntryService.addTimeEntryManually(timeEntryDTO);
        System.out.println(result);

        // Then
        assertThat(result)
                .as("Must not be null")
                .isNotNull()
                .isEqualTo("startTime(2024-05-11 08:00:00) | endTime(2024-05-11 10:00:00) | duration(02:00:00)");
        verify(this.timeEntryRepository, times(1))
                .save(isA(TimeEntry.class));
    }

    @Test
    @DisplayName("addTimeEntryManually should success when duration is not present")
    public void addTimeEntryManually_WithoutDuration_Success() {
        // Given
        TimeEntryDTO timeEntryDTO = TimeEntryDTO.builder()
                .timeEntryId(null)
                .projectId("1")
                .taskId("1")
                .startTime(this.startTimeStr)
                .endTime(this.endTimeStr)
                .duration(this.durationStr)
                .billable(false)
                .hourlyRate(BigDecimal.ZERO.toString())
                .build();
        when(this.timeEntryUtility.createTimeEntry(timeEntryDTO)).thenReturn(this.timeEntry);
        when(this.timeEntryRepository.save(isA(TimeEntry.class))).thenReturn(this.timeEntry);
        when(this.timeParser.parseTimeToString(this.startTime, this.endTime, this.duration)).thenReturn("startTime(2024-05-11 08:00:00) | endTime(2024-05-11 10:00:00) | duration(02:00:00)");

        // When
        String result = this.timeEntryService.addTimeEntryManually(timeEntryDTO);
        System.out.println(result);

        // Then
        assertThat(result)
                .as("Must not be null")
                .isNotNull()
                .isEqualTo("startTime(2024-05-11 08:00:00) | endTime(2024-05-11 10:00:00) | duration(02:00:00)");
        verify(this.timeEntryRepository, times(1))
                .save(isA(TimeEntry.class));
    }

    @Test
    @DisplayName("updateTimeEntry should success when timeEntryId is present")
    public void updateTimeEntryTest_ValidTimeEntryId_Success() {
        // Given
        String timeEntryId = "Some_timeEntry_id";
        TimeEntryDTO timeEntryDTO = TimeEntryDTO.builder()
                .timeEntryId(null)
                .projectId("1")
                .taskId("1")
                .startTime(this.startTimeStr)
                .endTime(this.endTimeStr)
                .duration(this.durationStr)
                .billable(false)
                .hourlyRate(BigDecimal.ZERO.toString())
                .build();

        String updatedStartTimeStr = parseLocalDateTimeToString(this.startTime.plusHours(1));
        String updatedEndTimeStr = parseLocalDateTimeToString(this.endTime.plusHours(1));
        TimeEntryDTO expectedUpdatedTimeEntryDTO = TimeEntryDTO.builder()
                .timeEntryId(null)
                .projectId("1")
                .taskId("1")
                .startTime(updatedStartTimeStr)
                .endTime(updatedEndTimeStr)
                .duration(this.durationStr)
                .billable(true)
                .hourlyRate(BigDecimal.TWO.toString())
                .build();
        TimeEntry expectedUpdateTimeEntry = new TimeEntry();
        expectedUpdateTimeEntry.setBillable(expectedUpdatedTimeEntryDTO.isBillable());
        expectedUpdateTimeEntry.setHourlyRate(new BigDecimal(expectedUpdatedTimeEntryDTO.getHourlyRate()));

        when(this.timeEntryRepository.findById(isA(String.class))).thenReturn(Optional.of(this.timeEntry));
        doNothing()
                .when(this.timeEntryUtility)
                .updateTimeEntry(isA(TimeEntry.class), isA(TimeEntryDTO.class), isA(TimeParser.class));
        when(this.timeEntryRepository.save(isA(TimeEntry.class))).thenReturn(expectedUpdateTimeEntry);
        when(this.timeEntryCacheManager.cacheTimeEntry(isA(String.class), isA(TimeEntry.class))).thenReturn(this.timeEntry);

        // When
        TimeEntry result = this.timeEntryService.updateTimeEntryManually(timeEntryId, timeEntryDTO);
        System.out.println(result);

        // Then
        assertThat(result)
                .as("Must not be null")
                .isNotNull();

        verify(this.timeEntryRepository, times(1))
                .save(isA(TimeEntry.class));
    }

    @Test
    @DisplayName("deleteTimeEntry should delete when timeEntryId is present")
    public void deleteTimeEntryTest() {
        // Given
        String id = "Some_timeEntry_id";
        doNothing()
                .when(this.timeEntryRepository)
                .deleteById(id);

        // When
        this.timeEntryService.deleteTimeEntry(id);

        // Then
        verify(this.timeEntryRepository, times(1))
                .deleteById(id);
    }

}