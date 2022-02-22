package ru.itis.algorithms.classes.Fano;


import com.google.gson.Gson;
import lombok.SneakyThrows;
import ru.itis.algorithms.interfaces.FileEncoder;
import ru.itis.algorithms.models.Code;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FanoEncoder implements FileEncoder {
    private ArrayList<Code> codes;
    private String initialText;
    private List<Byte> bytes;
    private String probabilitiesDestination;

    public FanoEncoder(String probabilitiesDestination) {
        this.codes = new ArrayList<>();
        this.bytes = new ArrayList<>();
        this.initialText = "";
        this.probabilitiesDestination = probabilitiesDestination;
    }

    @SneakyThrows
    private String readFromFile(String source) {

        Scanner s = new Scanner(new File(source), "utf-8");

        StringBuilder str = new StringBuilder();
        while (s.hasNextLine()) {
            str.append(s.nextLine()).append('\n');
        }
        str.delete(str.length()-1, str.length());
        return str.toString();


    }

    @SneakyThrows
    private boolean writeToFile(String data, String destination) {
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(destination), StandardCharsets.UTF_8);
        writer.write(data);
        writer.flush();
        writer.close();
        return true;
    }

    @SneakyThrows
    private boolean writeToFile(List<Byte> data, String destination) {
        FileOutputStream writer = new FileOutputStream(destination);
        byte[] bytes = new byte[data.size()];
        int i = 0;
        for (Byte b : data) {
            bytes[i++] = b;
        }
        writer.write(bytes, 0, bytes.length);
        writer.flush();
        writer.close();
        return true;
    }


    private boolean writeCodesToFile(List<Code> codes, String destination) {
        Map<Character, String> probabilities = new HashMap<>();
        for (Code code : codes) {
            probabilities.put(code.getCharacter(), code.getProbability());
        }
        String json = new Gson().toJson(probabilities);
        return this.writeToFile(json, destination);
    }

    @SneakyThrows
    private void setCodes() {
        Map<Character, Integer> chars = new HashMap<>();
        for (char ch : this.initialText.toCharArray()) {
            chars.put(ch, chars.get(ch) == null ? 1 : chars.get(ch) + 1);
        }
        for (Map.Entry<Character, Integer> entry : chars.entrySet()) {
            this.codes.add(new Code(entry.getKey(), new StringBuilder(), Double.toString((double) entry.getValue() / (initialText.length()))));
        }
        this.codes.sort((o1, o2) -> {
            int doubleCompare = Double.compare(Double.parseDouble(o1.getProbability()), Double.parseDouble(o2.getProbability()));
            if (doubleCompare == 0) {
                return -Character.compare(o1.getCharacter(), o2.getCharacter());
            }
            return -doubleCompare;
        });
    }

    @Override
    public boolean encode(String source, String destination) {
        this.initialText = readFromFile(source);
        this.setCodes();

        this.divide(0, codes.size());
        this.writeCodesToFile(codes, this.probabilitiesDestination);
        this.writeBinary(destination);
        return true;
    }

    private void divide(int left, int right) {
        BigDecimal total = new BigDecimal(0);
        if (right - left < 2) {
            return;
        }
        for (int i = left; i < right; i++) {
            total = total.add(new BigDecimal(this.codes.get(i).getProbability()));
        }
        BigDecimal half = total.divide(new BigDecimal(2));
        int index = left;
        BigDecimal summary = new BigDecimal(0);
        loop:
        for (Code code : this.codes) {
            BigDecimal current = summary.add(new BigDecimal(code.getProbability()));
            switch (current.compareTo(half)) {
                case 0:
                    index++;
                    break loop;
                case 1:
                    index += current.subtract(half).compareTo(half.subtract(current)) > 0 ? 1 : 0;
                    break loop;
                case -1:
                    index++;
                    summary = current;
            }
        }
        for (int i = left; i < index; i++) {
            Code code = this.codes.get(i);
            code.getCode().append('0');
        }
        for (int i = index; i < right; i++) {
            Code code = this.codes.get(i);
            code.getCode().append('1');
        }
        this.divide(left, index);
        this.divide(index, right);
    }

    @SneakyThrows
    private boolean writeBinary(String destination) {
        StringBuilder str = new StringBuilder();
        for (char ch : this.initialText.toCharArray()) {
            for (Code code : this.codes) {
                if (code.getCharacter() == ch) {
                    str.append(code.getCode());
                }
            }
        }
        this.writeToFile(str.toString(), destination);
        int tail = str.length() % 8;
        if (tail != 0){
            for (int i = 0; i < 8 - tail; i++) {
                str.append('0');
            }
        }
        for (int i = 0; i < str.length(); i+=8) {
            bytes.add((byte) Integer.parseInt(str.substring(i, i+8), 2));
        }
        return writeToFile(bytes, destination + ".binary");
    }
}
