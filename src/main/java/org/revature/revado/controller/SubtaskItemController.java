package org.revature.revado.controller;

import org.revature.revado.dto.SubtaskCreateDTO;
import org.revature.revado.dto.SubtaskResponseDTO;
import org.revature.revado.service.SubtaskItemService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/subtasks")
public class SubtaskItemController {

    private final SubtaskItemService subtaskService;

    public SubtaskItemController(SubtaskItemService subtaskService) {
        this.subtaskService = subtaskService;
    }

    // POST /api/subtasks- Create a new subtask
    @PostMapping
    public SubtaskResponseDTO create(@RequestBody SubtaskCreateDTO dto) {
    return subtaskService.createSubtask(dto);
    }

    // GET /api/subtasks/todo/{todoId}-Get all subtasks for a Todo
    @GetMapping("/todo/{todoId}")
    public List<SubtaskResponseDTO> getByTodo(@PathVariable String todoId) {
        return subtaskService.getSubtasks(todoId);
    }
    // PUT /api/subtasks/{id}/completed-Update completion status
    @PutMapping("/{id}/completed")
    public SubtaskResponseDTO updateCompleted(@PathVariable String id,
                                              @RequestParam boolean value) {
        return subtaskService.updateCompleted(id, value);
    }

    // DELETE /api/subtasks/{id}- Delete subtask
    @DeleteMapping("/{id}")
    public String delete(@PathVariable String id) {
        subtaskService.deleteSubtask(id);
        return "Subtask deleted";
    }

}