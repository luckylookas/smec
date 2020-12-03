package com.smec.accountexercise.event;

import com.smec.accountexercise.account.Account;
import com.smec.accountexercise.account.AccountService;
import com.smec.accountexercise.config.TimeService;
import com.smec.accountexercise.http.exceptions.NotFoundException;
import com.smec.accountexercise.statistics.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository repository;
    private final AccountService accountService;
    private final StatisticService statisticService;
    private final TimeService timeService;

    public EventDto save(String accountId, EventDto dto) {
        var account = accountService.findById(accountId);
        var saved = repository.save(Event.builder()
                .account(Account.builder().id(account.getId()).build())
                .happenedAt(Optional.ofNullable(dto.getHappenedAt()).orElse(timeService.now()))
                .type(dto.getType())
                .build());

        var finished = EventDto.builder().happenedAt(saved.getHappenedAt()).type(saved.getType()).id(saved.getId()).build();
        statisticService.save(finished, accountId);
        return finished;
    }

    public EventDto findById(String id) {
        return repository.findById(id)
                .map(it -> EventDto.builder().id(it.getId()).type(it.getType()).happenedAt(it.getHappenedAt()).build())
                .orElseThrow(NotFoundException::new);
    }

    public List<String> getIdsForAccount(String accountId) {
        return repository.findAllByAccountId(accountId);
    }
}
