package io.pivotal.pal.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTimeEntryRepository implements  TimeEntryRepository
{
    private Map<Long, TimeEntry> entries = new HashMap<>();

    public TimeEntry create(TimeEntry timeEntry) {
        long id = timeEntry.getId() == 0L ? entries.size()+1 : timeEntry.getId();
        timeEntry.setId(id);
        entries.put(id, timeEntry);
        return timeEntry;
    }

    public TimeEntry find(long id) {

        return entries.get(id);
    }

    public List<TimeEntry> list()
    {
         return new ArrayList<>(entries.values());
    }

    public TimeEntry update(long id, TimeEntry timeEntry)
    {
        timeEntry.setId(id);
        entries.put(id, timeEntry);
        return timeEntry;
    }

    public void delete(long id)
    {
        entries.remove(id);
    }
}
