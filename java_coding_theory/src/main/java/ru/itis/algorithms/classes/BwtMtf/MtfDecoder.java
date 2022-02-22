package ru.itis.algorithms.classes.BwtMtf;

import ru.itis.algorithms.interfaces.StringDecoder;
import ru.itis.algorithms.models.OrderedChar;

import java.util.ArrayList;
import java.util.List;

public class MtfDecoder {
    private List<OrderedChar> alphabet;
    private List<Integer> codes;
    public StringBuilder text;

    public MtfDecoder(String alphabetString) {
        this.alphabet = new ArrayList<>();
        this.codes = new ArrayList<>();
        this.text = new StringBuilder();
        int i = 0;
        for (char ch : alphabetString.toCharArray()) {
            this.alphabet.add(new OrderedChar(ch, i++));
        }
    }

    public boolean decode(List<Integer> codes) {
        for (int code : codes
        ) {
            for (int i = 0; i < this.alphabet.size(); i++) {
                OrderedChar orderedChar = this.alphabet.get(i);
                if (code == orderedChar.getOrder()) {
                    this.text.append(orderedChar.getCharacter());
                    for (OrderedChar c: this.alphabet) {
                        if (c.getOrder() < orderedChar.getOrder()){
                            c.setOrder(c.getOrder() + 1);
                        }
                    }
                    orderedChar.setOrder(0);
                    break;
                }
            }
        }
        return true;
    }
}
