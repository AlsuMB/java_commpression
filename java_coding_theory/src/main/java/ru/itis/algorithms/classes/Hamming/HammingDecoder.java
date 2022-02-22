package ru.itis.algorithms.classes.Hamming;

import ru.itis.algorithms.interfaces.StringDecoder;

import java.util.ArrayList;
import java.util.List;

public class HammingDecoder implements StringDecoder {
    private StringBuilder initialCode;
    public StringBuilder decodedCode;
    private List<Integer> bits;


    public HammingDecoder(){
        initialCode = new StringBuilder();
        bits = new ArrayList<>();
    }
    @Override
    public boolean decode(String code) {
        this.initialCode.append(code);
        this.setBits();
        this.checkCorrectness();
        this.setDecodedCode();
        return true;
    }

    private void checkCorrectness() {
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

        int i = 1;
        int error_bit = 0;
        for (Integer bit : this.bits) {
            if (bit == 1) {
                error_bit += i;
            }
            i *= 2;
        }

        if (error_bit != 0) {
            System.out.printf("Error found. Bit No. - %d. Fixing.", error_bit);
            int bit = Integer.parseInt(String.valueOf(this.initialCode.charAt(error_bit - 1)));
            this.initialCode.deleteCharAt(error_bit - 1);
            this.initialCode.insert(error_bit - 1, 1 - bit);
        }
    }

    private void setBits() {
        int i = 1;
        while (i < this.initialCode.length()) {
            this.bits.add(0);
            i *= 2;
        }
    }

    private void setDecodedCode() {
        int offset = 0;
        int i = 1;
        int len = this.initialCode.length();
        while (i < len) {
            this.initialCode.deleteCharAt(i - 1 - offset);
            offset += 1;
            i *= 2;
        }
        this.decodedCode = initialCode;
    }
}
