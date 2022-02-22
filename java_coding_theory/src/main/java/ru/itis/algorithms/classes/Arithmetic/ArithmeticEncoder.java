package ru.itis.algorithms.classes.Arithmetic;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import ru.itis.algorithms.interfaces.FileEncoder;
import ru.itis.algorithms.models.Probability;
import ru.itis.algorithms.models.Sum;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;



public class ArithmeticEncoder implements FileEncoder {
    private String probabilitiesDestination;
    private String initialText;
    private long total;
    private StringBuilder code;
    private List<Byte> codes;
    private List<Probability> probabilities;
    private BigDecimal left;
    private BigDecimal right;
    private List<Sum> sums;

    @SneakyThrows
    private String readFromFile(String source) {

        Scanner s = new Scanner(new File(source), "utf-8");

        StringBuilder str = new StringBuilder();
        while (s.hasNextLine()) {
            str.append(s.nextLine()).append('\n');
        }
        str.delete(str.length() - 1, str.length());
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


    private boolean writeProbabilitiesToFile(String destination) {
        Map<Character, String> probabilities = new HashMap<>();
        for (Probability probability: this.probabilities) {
            probabilities.put(probability.getCharacter(), probability.getProbability());
        }
        String json = new Gson().toJson(probabilities);
        return this.writeToFile(json, destination);
    }

    @SneakyThrows
    private void setProbabilities() {
        Map<Character, Integer> chars = new HashMap<>();
        for (char ch : this.initialText.toCharArray()) {
            chars.put(ch, chars.get(ch) == null ? 1 : chars.get(ch) + 1);
        }
        if (chars.get('\0') != null) {
            throw new Exception("\\0 is used as escape character, but found in utf text. Returning");
        }
        this.initialText += '\0';
        chars.put('\0', 1);
        for (Map.Entry<Character, Integer> entry : chars.entrySet()) {
            this.probabilities.add(new Probability(entry.getKey(), Double.toString((double) entry.getValue() / (initialText.length()))));
        }
        // We sort due to HashMap is not ordered and we will fail when decoding it
        this.probabilities.sort((o1, o2) -> {
            int doubleCompare = Double.compare(Double.parseDouble(o1.getProbability()), Double.parseDouble(o2.getProbability()));
            if (doubleCompare == 0) {
                return -Character.compare(o1.getCharacter(), o2.getCharacter());
            }
            return -doubleCompare;
        });
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
    private void countIntervals(Character character) {
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
        this.left = left;
        this.right = right;
    }

    private void buildCode() {
        BigDecimal two = new BigDecimal(2);
        BigDecimal total = new BigDecimal(0);
        BigDecimal next = new BigDecimal("0.5");
        while (true) {
            BigDecimal current = total.add(next);
            int compareLeft = current.compareTo(left);
            int compareRight = current.compareTo(right);
            if (compareLeft < 0) {
                this.code.append('1');
                total = total.add(next);
            } else if (compareRight < 0) {
                this.code.append('1');
                break;
            } else {
                this.code.append('0');
            }
            next = next.divide(two);
        }

        int tail = this.code.length() % 8;
        if (tail != 0){
            for (int i = 0; i < 8 - tail; i++) {
                this.code.append('0');
            }
        }
        for (int i = 0; i < code.length(); i+=8) {
            codes.add((byte) Integer.parseInt(code.substring(i, i+8), 2));
        }
    }

    private void writeCode(String destination) {
        this.writeToFile(this.code.toString(), destination);
    }

    public ArithmeticEncoder(String probabilitiesDestination) {
        this.probabilitiesDestination = probabilitiesDestination;
        this.initialText = "";
        this.total = 0;
        this.code = new StringBuilder();
        this.codes = new ArrayList<>();
        this.probabilities = new ArrayList<>();
        this.left = new BigDecimal(0);
        this.right = new BigDecimal("1.0");
        this.sums = new ArrayList<>();
    }

    @Override
    public boolean encode(String source, String destination) {
        this.initialText = this.readFromFile(source);
        this.setProbabilities();
        this.setSums();
        for (Character character: this.initialText.toCharArray()){
            this.countIntervals(character);
        }
        this.buildCode();
        this.writeCode(destination);
        this.writeToFile(codes, destination + ".binary");
        this.writeProbabilitiesToFile(this.probabilitiesDestination);
        return true;
    }
}
