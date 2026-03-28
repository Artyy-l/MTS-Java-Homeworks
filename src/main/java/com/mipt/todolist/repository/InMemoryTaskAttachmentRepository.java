package com.mipt.todolist.repository;

import com.mipt.todolist.model.TaskAttachment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryTaskAttachmentRepository implements TaskAttachmentRepository {

    private final Map<Long, TaskAttachment> storage = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    @Override
    public TaskAttachment save(TaskAttachment attachment) {
        if (attachment.getId() == null) {
            attachment.setId(idSequence.getAndIncrement());
        }
        storage.put(attachment.getId(), attachment);
        return attachment;
    }

    @Override
    public Optional<TaskAttachment> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<TaskAttachment> findByTaskId(String taskId) {
        return storage.values().stream()
                .filter(a -> taskId.equals(a.getTaskId()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }
}
