package ru.itis.algorithms.classes.BwtMtf;

import lombok.SneakyThrows;
import ru.itis.algorithms.interfaces.StringEncoder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BwtEncoder implements StringEncoder {
    public StringBuilder code;
    private String initialText;
    private List<String[]> words;

    public BwtEncoder(){
        this.code = new StringBuilder();
        this.words = new ArrayList<>();
    }

    private void cycleWords(){
        StringBuilder word = new StringBuilder(this.initialText);
        for (int i = 0; i < this.initialText.length(); i++) {
            words.add(new String[]{Integer.toString(i), word.toString()});
            word.append(word.charAt(0));
            word.delete(0, 1);
        }
    }
    private void buildCode(){
        int firstIndex = 0;
        for (int i = 0; i < words.size(); i++) {
            String[] word = words.get(i);
            this.code.append(word[1].charAt(word[1].length() - 1));
            if ( word[0].equals("0")){
                firstIndex = i;
            }
        }
        this.code.append('\0').append(firstIndex).append('\0');
    }
    @SneakyThrows
    @Override
    public boolean encode(String text) {
        this.initialText = text;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\0'){
                throw new Exception("\\0 is used as escape character, but found in text. Returning");
            }
        }
        this.cycleWords();
        this.words.sort(Comparator.comparing(o -> o[1]));
        this.buildCode();
        return true;
    }
}
