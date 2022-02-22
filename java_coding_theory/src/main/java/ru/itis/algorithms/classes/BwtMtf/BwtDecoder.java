package ru.itis.algorithms.classes.BwtMtf;

import ru.itis.algorithms.interfaces.StringDecoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BwtDecoder implements StringDecoder {
    public StringBuilder code;
    private int firstIndex;
    private char[] sortedChars;
    private String initialText;

    public BwtDecoder() {
        code = new StringBuilder();
        firstIndex = 0;
    }

    @Override
    public boolean decode(String code) {
        String[] tokens = code.split("\0");
        this.initialText = tokens[0];
        this.firstIndex = Integer.parseInt(tokens[1]);
        this.sortedChars = this.initialText.toCharArray();
        Arrays.sort(this.sortedChars);
        this.reflect();
        return true;
    }

    private void reflect() {
        int index = firstIndex;
        for (int i = 0; i < initialText.length(); i++) {
            int order = 0;
            char ch = this.sortedChars[index];
            int j = index;
            while (j >= 0 && this.sortedChars[index] == this.sortedChars[j]) {
                order += 1;
                j -= 1;
            }
            this.code.append(ch);

            for (int k = 0; k < this.initialText.length(); k++) {
                if (this.initialText.charAt(k) == ch) {
                    order -= 1;
                }
                if (order == 0) {
                    index = k;
                    break;
                }
            }
        }
    }
}
