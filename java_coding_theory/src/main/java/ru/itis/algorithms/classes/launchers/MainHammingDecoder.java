package ru.itis.algorithms.classes.launchers;


import lombok.SneakyThrows;
import ru.itis.algorithms.classes.Hamming.HammingDecoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class MainHammingDecoder {
    public static void main(String[] args) {
        String source = args[0];
        String decodedPath = args[1];
        HammingDecoder decoder = new HammingDecoder();
        decoder.decode(readFromFile(source));
        writeToFile(decoder.decodedCode.toString(), decodedPath);
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
