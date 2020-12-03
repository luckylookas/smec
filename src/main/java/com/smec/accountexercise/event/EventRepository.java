package com.smec.accountexercise.event;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends CrudRepository<Event, String> {

    @Query("select e.id from Event e where e.account.id = :accountId")
    List<String> findAllByAccountId(@Param("accountId") String accountId);

    void deleteAllByHappenedAtBefore(LocalDateTime pivot);
}
