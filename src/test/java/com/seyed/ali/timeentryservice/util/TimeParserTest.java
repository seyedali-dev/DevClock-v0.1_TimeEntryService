package com.seyed.ali.timeentryservice.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TimeParserTest {

    private @InjectMocks TimeParser timeParser;

    private String startTimeStr;
    private String endTimeStr;
    private String durationStr;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Duration duration;

    @BeforeEach
    void setUp() {
        this.startTimeStr = "2024-05-11 08:00:00";
        this.endTimeStr = "2024-05-11 10:00:00";
        this.durationStr = "02:00:00";
        this.startTime = LocalDateTime.parse(this.startTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.endTime = LocalDateTime.parse(this.endTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.duration = Duration.between(this.startTime, this.endTime);
    }

    @Test
    void parseStringToLocalDateTime() {
        // given
        LocalDateTime localDateTime = LocalDateTime.of(2024, 5, 11, 8, 0);

        // when
        LocalDateTime parsedStringToLocalDateTime = this.timeParser.parseStringToLocalDateTime(this.startTimeStr);

        // then
        assertThat(parsedStringToLocalDateTime)
                .as("Must be same")
                .isEqualTo(localDateTime);
    }

    @Test
    void parseStringToDuration() {
        // given
        Duration duration = Duration.ofHours(2L);

        // when
        Duration result = this.timeParser.parseStringToDuration(this.durationStr);
        System.out.println(result);

        // then
        assertThat(result)
                .as("Must be same")
                .isEqualTo(duration);
    }

    @Test
    void parseLocalDateTimeToString() {
        // given

        // when
        String result = this.timeParser.parseLocalDateTimeToString(this.startTime);
        System.out.println(result);

        // then
        assertThat(result)
                .as("Must be same")
                .isEqualTo(this.startTimeStr);
    }

    @Test
    void parseDurationToString() {
        // given

        // when
        String result = this.timeParser.parseDurationToString(this.duration);
        System.out.println(result);

        // then
        assertThat(result)
                .as("Must be same")
                .isEqualTo(this.durationStr);
    }

    @Test
    void parseTimeToString() {
        // given
        String resultMessage = "startTime (" + startTimeStr + ")" +
                " | endTime (" + endTimeStr + ")" +
                " | duration(" + durationStr + ")";

        // when
        String result = this.timeParser.parseTimeToString(this.startTime, this.endTime, this.duration);
        System.out.println(result);

        // then
        assertThat(result)
                .as("Must be same")
                .isEqualTo(resultMessage);
    }

}