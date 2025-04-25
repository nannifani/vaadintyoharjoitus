package com.example.application.services;

import com.example.application.data.Event;
import com.example.application.data.EventRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    private final EventRepository repository;

    public EventService(EventRepository repository) {
        this.repository = repository;
    }

    public Optional<Event> get(Long id) {
        return repository.findById(id);
    }

    public Event save(Event entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Event> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Event> list(Pageable pageable, Specification<Event> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }
}
