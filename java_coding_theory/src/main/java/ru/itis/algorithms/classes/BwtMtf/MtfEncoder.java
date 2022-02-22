package ru.itis.algorithms.classes.BwtMtf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.itis.algorithms.interfaces.StringEncoder;
import ru.itis.algorithms.models.OrderedChar;

import java.util.ArrayList;
import java.util.List;



public class MtfEncoder implements StringEncoder {
    private List<OrderedChar> alphabet;
    public List<Integer> codes;
    private String initialText;

    public MtfEncoder(String alphabetString) {
        this.alphabet = new ArrayList<>();
        this.codes = new ArrayList<>();
        int i = 0;
        for (char ch : alphabetString.toCharArray()) {
            this.alphabet.add(new OrderedChar(ch, i++));
        }
    }

    @Override
    public boolean encode(String text) {
        for (char ch : text.toCharArray()) {
            for (int i = 0; i < this.alphabet.size(); i++) {
                OrderedChar orderedChar = this.alphabet.get(i);
                if (ch == orderedChar.getCharacter()){
                    this.codes.add(orderedChar.getOrder());
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
