package ru.itis.algorithms.classes.Hamming;

import ru.itis.algorithms.interfaces.StringEncoder;

import java.util.ArrayList;
import java.util.List;

public class HammingEncoder implements StringEncoder {
    private StringBuilder initialCode;
    public StringBuilder encodedCode;
    private List<Integer> bits;

    public HammingEncoder() {
        this.initialCode = new StringBuilder();
        this.encodedCode = new StringBuilder();
        this.bits = new ArrayList<>();
    }

    private void insertCheckBits() {
        int i = 1;
        while (i < this.initialCode.length()) {
            this.initialCode.insert(i - 1, '0');
            this.bits.add(0);
            i *= 2;
        }
    }

    private void setControlBits() {
        for (int i = 0; i < this.initialCode.length(); i++) {
            StringBuilder binaryCode = new StringBuilder(Integer.toBinaryString(i + 1));
            int len = binaryCode.length();
            for (int j = len; j < this.bits.size(); j++) {
                binaryCode.insert(0, '0');
            }
            if (this.initialCode.charAt(i) != '0') {
                for (int j = 0; j < binaryCode.length(); j++) {
                    this.bits.set(this.bits.size() - j - 1, (this.bits.get(this.bits.size() - j - 1) + binaryCode.charAt(j) == '1' ? 1 : 0) % 2);
                }
            }
        }
    }

    private void correctControlBits() {
        int i = 1;
        int k = 0;
        while (i < this.initialCode.length()) {
            this.initialCode.deleteCharAt(i - 1);
            this.initialCode.insert(i - 1, this.bits.get(k++));
            i *= 2;
        }
    }

    @Override
    public boolean encode(String text) {
        this.initialCode.append(text);
        this.insertCheckBits();
        this.setControlBits();
        this.correctControlBits();
        this.encodedCode = initialCode;
        return true;
    }
}
