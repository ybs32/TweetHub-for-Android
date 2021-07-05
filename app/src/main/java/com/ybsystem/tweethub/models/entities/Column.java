package com.ybsystem.tweethub.models.entities;

import com.ybsystem.tweethub.models.enums.ColumnType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Column extends Entity {

    private long id;
    private String name;
    private ColumnType type;
    private boolean isBootColumn;
}
