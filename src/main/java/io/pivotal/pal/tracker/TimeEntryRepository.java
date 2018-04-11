package io.pivotal.pal.tracker;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TimeEntryRepository {
    TimeEntry create(TimeEntry any);
    TimeEntry find(long l);
    List<TimeEntry> list();
    TimeEntry update(long eq, TimeEntry any);
    void delete(long l);
}
