package ru.itis.algorithms.classes.launchers;

import ru.itis.algorithms.classes.Arithmetic.ArithmeticDecoder;
import ru.itis.algorithms.classes.Arithmetic.ArithmeticEncoder;

public class MainArithmetic {
    public static void main(String[] args) {
        String source = "src/main/resources/Arithmetic/voyna-i-mir-tom-1.txt";
        String encodedPath = "src/main/resources/Arithmetic/encode.al.binary";
        String decodedPath = "src/main/resources/Arithmetic/2.txt";
        String probabilitiesDestination = "src/main/resources/Arithmetic/Frequency.alg";
//        ArithmeticEncoder encoder = new ArithmeticEncoder(probabilitiesDestination);
//        encoder.encode(source, encodedPath);
        ArithmeticDecoder decoder = new ArithmeticDecoder(probabilitiesDestination);
        decoder.decode(encodedPath, decodedPath);
    }
}
