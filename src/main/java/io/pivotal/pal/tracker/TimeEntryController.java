package io.pivotal.pal.tracker;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/time-entries")
public class TimeEntryController {

    private TimeEntryRepository timeEntryRepository;

    public TimeEntryController(TimeEntryRepository timeEntryRepository)
    {
        this.timeEntryRepository = timeEntryRepository;
    }

    @SpeedLogger
    @PostMapping
    public ResponseEntity create(@RequestBody TimeEntry timeEntryToCreate)
    {
        TimeEntry createdTimeEntry = timeEntryRepository.create(timeEntryToCreate);
        return new ResponseEntity<>(createdTimeEntry, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeEntry> read(@PathVariable long id)
    {
        TimeEntry entry = timeEntryRepository.find(id);
        HttpStatus httpStatus = entry == null ? HttpStatus.NOT_FOUND : HttpStatus.OK;

        return  new ResponseEntity<>(entry,httpStatus);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable long id, @RequestBody TimeEntry expected) {

        TimeEntry entry = timeEntryRepository.update(id, expected);
        HttpStatus httpStatus = entry == null ? HttpStatus.NOT_FOUND : HttpStatus.OK;

        return new ResponseEntity<>(entry,httpStatus);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TimeEntry> delete(@PathVariable long id)
    {
        timeEntryRepository.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @GetMapping("")
    public ResponseEntity<List<TimeEntry>> list() {
        return new ResponseEntity<>(timeEntryRepository.list(), HttpStatus.OK);
    }
}
