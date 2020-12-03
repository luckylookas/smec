package com.smec.accountexercise.statistics;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Builder
public class StatisticsResult {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate day;
    private final String event;
    private final Integer count;
}
