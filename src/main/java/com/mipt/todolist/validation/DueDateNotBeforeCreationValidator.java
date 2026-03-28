package com.mipt.todolist.validation;

import com.mipt.todolist.dto.TaskUpdateDto;
import com.mipt.todolist.model.Task;
import com.mipt.todolist.repository.TaskRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;
import java.util.Optional;

/**
 * Проверяет, что {@code dueDate} не раньше даты создания задачи (по id из пути)
 */
@Component
public class DueDateNotBeforeCreationValidator
        implements ConstraintValidator<DueDateNotBeforeCreation, TaskUpdateDto> {

    private final TaskRepository taskRepository;

    public DueDateNotBeforeCreationValidator(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public boolean isValid(TaskUpdateDto dto, ConstraintValidatorContext context) {
        if (dto == null || dto.getDueDate() == null) {
            return true;
        }
        HttpServletRequest request = CurrentHttpRequestHolder.get();
        if (request == null) {
            return true;
        }
        @SuppressWarnings("unchecked")
        Map<String, String> uriVars = (Map<String, String>) request.getAttribute(
                HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (uriVars == null) {
            return true;
        }
        String id = uriVars.get("id");
        if (id == null) {
            return true;
        }
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isEmpty()) {
            return true;
        }
        Task task = taskOpt.get();
        if (task.getCreatedAt() == null) {
            return true;
        }
        if (dto.getDueDate().isBefore(task.getCreatedAt().toLocalDate())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("dueDate")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
