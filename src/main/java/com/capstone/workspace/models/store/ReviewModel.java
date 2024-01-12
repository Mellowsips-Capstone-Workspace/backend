package com.capstone.workspace.models.store;

import com.capstone.workspace.models.shared.BaseModel;
import lombok.Data;

@Data
public class ReviewModel extends BaseModel {
    private int point;
    private String comment;
}
