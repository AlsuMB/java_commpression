package ru.itis.algorithms.classes.launchers;


import lombok.SneakyThrows;
import ru.itis.algorithms.classes.BwtMtf.BwtDecoder;
import ru.itis.algorithms.classes.BwtMtf.BwtEncoder;
import ru.itis.algorithms.classes.BwtMtf.MtfDecoder;
import ru.itis.algorithms.classes.BwtMtf.MtfEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class MainBwtMtf {
    public static void main(String[] args) {
        String source = args[0];
        String encodedBWTPath = args[1];
        String encodedMTFPath = args[2];
        String decodedMTFPath = args[3];
        String decodedBWTPath = args[4];
        String alphabetPath = args[5];
        BwtEncoder bwtEncoder = new BwtEncoder();
        String string = readFromFile(source);
        bwtEncoder.encode(string);
        writeToFile(bwtEncoder.code.toString(), encodedBWTPath);
        Set<Character> charset = new HashSet<Character>();
        for (Character ch : bwtEncoder.code.toString().toCharArray()) {
            charset.add(ch);
        }
        StringBuilder alphabet = new StringBuilder();
        for (Character ch: charset) {
            alphabet.append(ch);
        }
        writeToFile(alphabet.toString(), alphabetPath);
        MtfEncoder encoder = new MtfEncoder(alphabet.toString());
        encoder.encode(bwtEncoder.code.toString());
        writeToFile(encoder.codes.toString(), encodedMTFPath);
        String decodeAlphabet = readFromFile(alphabetPath);
        MtfDecoder decoder = new MtfDecoder(decodeAlphabet);
        decoder.decode(encoder.codes);
        writeToFile(decoder.text.toString(), decodedMTFPath);
        BwtDecoder bwtDecoder = new BwtDecoder();
        bwtDecoder.decode(decoder.text.toString());
        writeToFile(bwtDecoder.code.toString(), decodedBWTPath);
    }

    @SneakyThrows
    private static boolean writeToFile(String data, String destination) {
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(destination), StandardCharsets.UTF_8);
        writer.write(data);
        writer.flush();
        writer.close();
        return true;
    }

    @SneakyThrows
    private static String readFromFile(String source) {

        Scanner s = new Scanner(new File(source), "utf-8");

        StringBuilder str = new StringBuilder();
        while (s.hasNextLine()) {
            str.append(s.nextLine()).append('\n');
        }
        str.delete(str.length() - 1, str.length());
        return str.toString();


    }
}
