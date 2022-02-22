package ru.itis.algorithms.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Code {
    Character character;
    StringBuilder code;
    String probability;

    @Override
    public String toString() {
        return "Code{" +
                "character=" + character +
                ", code=" + code +
                ", probability='" + probability + '\'' +
                '}';
    }
}
