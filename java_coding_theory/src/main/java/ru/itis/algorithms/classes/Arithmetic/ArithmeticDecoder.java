package ru.itis.algorithms.classes.Arithmetic;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import ru.itis.algorithms.interfaces.FileDecoder;
import ru.itis.algorithms.models.Probability;
import ru.itis.algorithms.models.Sum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class ArithmeticDecoder implements FileDecoder {
    private List<Probability> probabilities;
    private StringBuilder text;
    private BigDecimal left;
    private BigDecimal right;
    private BigDecimal number;
    private String code;
    private List<Sum> sums;

    private void buildNumber() {
        BigDecimal two = new BigDecimal(2);
        BigDecimal total = new BigDecimal(0);
        BigDecimal next = new BigDecimal("0.5");

        for (int i = 0; i < code.length(); i++) {
            if (code.charAt(i) == '1') {
                total = total.add(next);
            }
            next = next.divide(two);
        }

        this.number = total;
    }

    private void setSums() {
        BigDecimal sum = new BigDecimal(0);
        for (Probability probability : this.probabilities) {
            BigDecimal prob = new BigDecimal(probability.getProbability());
            this.sums.add(new Sum(probability.getCharacter(), sum, sum.add(prob)));
            sum = sum.add(prob);
        }
    }

    @SneakyThrows
    private BigDecimal[] countIntervals(Character character) {
        int index = -1;
        for (int i = 0; i < this.sums.size(); i++) {
            if (this.sums.get(i).getCharacter().charValue() == character.charValue()) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            throw new Exception(String.format("Unknown character %s", character));
        }
        BigDecimal difference = this.right.subtract(left);
        BigDecimal left = this.left.add(difference.multiply(this.sums.get(index).getLeft())); // left + difference * sums[char].left
        BigDecimal right = this.left.add(difference.multiply(this.sums.get(index).getRight())); // left + difference * sums[char].right
        return new BigDecimal[]{left, right};
    }

    private void buildText() {
        loop1:
        while (true) {
            for (Probability probability : this.probabilities) {
                BigDecimal[] interval = countIntervals(probability.getCharacter());
                int compareLeft = this.number.compareTo(interval[0]);
                int compareRight = this.number.compareTo(interval[1]);
                if (compareLeft >= 0 && compareRight < 0) {
                    if (probability.getCharacter() == '\0') {
                        break loop1;
                    }
                    this.text.append(probability.getCharacter());
                    this.left = interval[0];
                    this.right = interval[1];
                }
            }
        }
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
    private String readFromFile(String source) {

        Scanner s = new Scanner(new File(source), "utf-8");

        StringBuilder str = new StringBuilder();
        while (s.hasNextLine()) {
            str.append(s.nextLine()).append('\n');
        }
        return str.toString();

    }

    private void readProbabilitiesFromFile(String source) {
        String json = readFromFile(source);
        Type mapType = new TypeToken<Map<Character, String>>() {
        }.getType();
        Map<Character, String> chars = new Gson().fromJson(json, mapType);
        for (Map.Entry<Character, String> entry : chars.entrySet()) {
            this.probabilities.add(Probability.builder().probability(entry.getValue()).character(entry.getKey()).build());
        }
        this.probabilities.sort((o1, o2) -> {
            int doubleCompare = Double.compare(Double.parseDouble(o1.getProbability()), Double.parseDouble(o2.getProbability()));
            if (doubleCompare == 0) {
                return -Character.compare(o1.getCharacter(), o2.getCharacter());
            }
            return -doubleCompare;
        });
    }

    public ArithmeticDecoder(String probabilitiesSource) {
        this.probabilities = new ArrayList<>();
        readProbabilitiesFromFile(probabilitiesSource);
        this.text = new StringBuilder();
        this.left = new BigDecimal(0);
        this.right = new BigDecimal(1);
        this.sums = new ArrayList<>();
    }

    @Override
    public boolean decode(String source, String destination) {
        this.code = readFromFile(source);
        this.buildNumber();
        this.setSums();
        this.buildText();
        this.writeToFile(this.text.toString(), destination);
        return true;
    }
}
