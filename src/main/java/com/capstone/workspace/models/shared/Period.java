package com.capstone.workspace.models.shared;

import lombok.Data;

@Data
public class Period<T> {
    private T start;
    private T end;
}
