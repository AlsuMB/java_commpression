package ru.itis.algorithms.classes.Fano;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import ru.itis.algorithms.interfaces.FileDecoder;
import ru.itis.algorithms.models.Code;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FanoDecoder implements FileDecoder {
    private StringBuilder decodedString;
    private ArrayList<Code> codes;
    private BinaryTree tree;
    private BinaryNode walker;

    @SneakyThrows
    private String readFromFile(String source) {
        Scanner s = new Scanner(new File(source), "utf-8");

        StringBuilder str = new StringBuilder();
        while (s.hasNextLine()) {
            str.append(s.nextLine()).append('\n');
        }
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

    private void readCodesFromFile(String source) {
        String json = readFromFile(source);
        Type mapType = new TypeToken<Map<Character, String>>() {
        }.getType();
        Map<Character, String> chars = new Gson().fromJson(json, mapType);
        for (Map.Entry<Character, String> entry : chars.entrySet()) {
            this.codes.add(Code.builder().character(entry.getKey()).probability(entry.getValue()).build());
        }
        this.codes.sort((o1, o2) -> {
            int doubleCompare = Double.compare(Double.parseDouble(o1.getProbability()), Double.parseDouble(o2.getProbability()));
            if (doubleCompare == 0) {
                return -Character.compare(o1.getCharacter(), o2.getCharacter());
            }
            return -doubleCompare;
        });
    }

    private void buildTree(int left, int right, BinaryNode node) {
        BigDecimal total = new BigDecimal(0);
        if (right - left == 1) {
            node.value = this.codes.get(left).getCharacter();
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
        BinaryNode leftNode = new BinaryNode();
        BinaryNode rightNode = new BinaryNode();
        node.left = leftNode;
        node.right = rightNode;
        this.buildTree(left, index, leftNode);
        this.buildTree(index, right, rightNode);
    }

    public FanoDecoder(String probabilitiesSource) {
        this.decodedString = new StringBuilder();
        this.codes = new ArrayList<>();
        readCodesFromFile(probabilitiesSource);
        this.tree = new BinaryTree();
        this.buildTree(0, this.codes.size(), this.tree.root);
        this.walker = this.tree.root;
    }

    @Override
    public boolean decode(String source, String destination) {
        String code = this.readFromFile(source);
        for (char ch : code.toCharArray()) {
            this.walkTree(ch);
        }
        writeToFile(decodedString.toString(), destination);
        return true;
    }

    private void walkTree(Character character) {
        if (this.walker == null) {
            return;
        }
        switch (character) {
            case '0':
                this.walker = this.walker.left;
                break;
            case '1':
                this.walker = this.walker.right;
                break;
        }

        if (this.walker.value != null) {
            this.decodedString.append(this.walker.value);
            this.walker = this.tree.root;
        }
    }
}


class BinaryNode {
    public BinaryNode left;
    public BinaryNode right;
    public Character value;
}

class BinaryTree {
    public BinaryNode root = new BinaryNode();
}
