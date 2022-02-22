package ru.itis.algorithms.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Probability {
    Character character;
    String probability;

    @Override
    public String toString() {
        return "Probability{" +
                "character=" + character +
                ", probability='" + probability + '\'' +
                '}';
    }
}
