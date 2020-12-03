package com.smec.accountexercise.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/account/{accountId}")
    public EventDto post(@PathVariable("accountId") String accountId, @RequestBody EventDto eventDto) {
        return eventService.save(accountId, eventDto);
    }

    @GetMapping("/account/{accountId}")
    public List<String> getList(@PathVariable("accountId") String accountId) {
        return eventService.getIdsForAccount(accountId);
    }

    @GetMapping("/{id}")
    public EventDto get(@PathVariable("id") String id) {
        return eventService.findById(id);
    }
}
