package org.revature.revado.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubtaskResponseDTO {

    private String id;
    private String title;
    private boolean completed;
}