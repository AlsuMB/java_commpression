package ru.itis.algorithms.classes.launchers;

import ru.itis.algorithms.classes.Fano.FanoDecoder;
import ru.itis.algorithms.classes.Fano.FanoEncoder;

import java.util.Arrays;

public class MainFano {
    public static void main(String[] args) {
        String source = args[0];
        String encodedPath = args[1];
        String decodedPath = args[2];
        String probabilitiesDestination = args[3];
        FanoEncoder encoder = new FanoEncoder(probabilitiesDestination);
        encoder.encode(source, encodedPath);
        FanoDecoder decoder = new FanoDecoder(probabilitiesDestination);
        decoder.decode(encodedPath, decodedPath);
    }
}
