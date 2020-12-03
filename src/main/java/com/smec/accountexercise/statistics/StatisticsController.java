package com.smec.accountexercise.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticService service;

    @GetMapping("/account/{id}")
    public List<StatisticsResult> post(@PathVariable("id") String id) {
        return service.getStatisticsForAccount(id);
    }
}
