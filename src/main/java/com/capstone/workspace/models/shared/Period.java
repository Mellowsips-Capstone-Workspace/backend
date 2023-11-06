package com.capstone.workspace.models.shared;

import lombok.Data;

import java.sql.Time;

@Data
public class Period<T> implements Comparable{
    private T start;
    private T end;

    @Override
    public int compareTo(Object o) {
        Period<T> converted = (Period<T>) o;

        if (start instanceof Time && converted.getStart() instanceof Time) {
            return ((Time) start).before((Time) converted.getStart()) ? -1 : 1;
        }

        return 0;
    }
}
