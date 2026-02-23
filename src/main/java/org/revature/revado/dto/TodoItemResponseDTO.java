package org.revature.revado.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class TodoItemResponseDTO {
    private String id;
    private String title;
    private boolean isCompleted;
}