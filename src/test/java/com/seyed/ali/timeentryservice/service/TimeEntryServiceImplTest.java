package com.seyed.ali.timeentryservice.service;

import com.seyed.ali.timeentryservice.client.AuthenticationServiceClient;
import com.seyed.ali.timeentryservice.model.domain.TimeEntry;
import com.seyed.ali.timeentryservice.model.dto.TimeEntryDTO;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeEntryServiceImplTest extends TimeParserUtilForTests {

    private @InjectMocks TimeEntryServiceImpl timeEntryService;
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

    @Test
    void startTrackingTimeEntryTest() {
        // given
        when(this.authenticationServiceClient.getCurrentLoggedInUsersId())
                .thenReturn("some_user_id");
        when(this.timeEntryRepository.save(isA(TimeEntry.class)))
                .thenReturn(this.timeEntry);

        // when
        String timeEntryId = this.timeEntryService.startTrackingTimeEntry();

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
    public void updateTimeEntryTest() {
        // Given
        String id = "Some_timeEntry_id";
        TimeEntryDTO timeEntryDTO = new TimeEntryDTO(null, this.startTimeStr, this.endTimeStr, this.durationStr);

        when(this.timeEntryRepository.findById(id))
                .thenReturn(Optional.of(this.timeEntry));
        when(this.timeParser.parseStringToLocalDateTime(isA(String.class)))
                .thenReturn(this.endTime);
        when(this.timeParser.parseLocalDateTimeToString(isA(LocalDateTime.class)))
                .thenReturn(this.startTimeStr);
        when(this.timeParser.parseDurationToString(isA(Duration.class)))
                .thenReturn(this.durationStr);

        // When
        TimeEntryDTO result = this.timeEntryService.updateTimeEntryManually(id, timeEntryDTO);
        System.out.println(result);

        // Then
        assertThat(result)
                .as("Must not be null")
                .isNotNull();
        assertThat(result.duration())
                .as("Must be equal to = PT2H")
                .isEqualTo("02:00:00");

        verify(this.timeEntryRepository, times(1))
                .save(isA(TimeEntry.class));
    }

    @Test
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