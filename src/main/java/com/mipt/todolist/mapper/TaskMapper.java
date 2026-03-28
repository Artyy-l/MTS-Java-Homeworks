package com.mipt.todolist.mapper;

import com.mipt.todolist.dto.TaskCreateDto;
import com.mipt.todolist.dto.TaskResponseDto;
import com.mipt.todolist.dto.TaskUpdateDto;
import com.mipt.todolist.model.Task;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.LocalDateTime;
import java.util.HashSet;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class, HashSet.class})
public interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "completed", constant = "false")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "tags", expression = "java(dto.getTags() != null ? new HashSet<>(dto.getTags()) : new HashSet<>())")
    Task toEntity(TaskCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "completed", expression = "java(dto.getCompleted() != null ? dto.getCompleted() : task.isCompleted())")
    @Mapping(target = "tags", expression = "java(dto.getTags() != null ? new HashSet<>(dto.getTags()) : task.getTags())")
    void updateEntity(TaskUpdateDto dto, @MappingTarget Task task);

    TaskResponseDto toResponseDto(Task task);
}
