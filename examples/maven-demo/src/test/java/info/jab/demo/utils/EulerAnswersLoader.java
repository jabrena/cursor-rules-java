package info.jab.demo.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class EulerAnswersLoader {

    private List<String> loadFile() {
        try (var inputStream = getClass().getClassLoader().getResourceAsStream("euler/answers.txt");
            var reader = new BufferedReader(new InputStreamReader(inputStream))) {

            return reader.lines().toList();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public String getAnswer(int index) {
        return loadFile().stream()
                .skip(index - 1)
                .findFirst()
                .map(line -> line.split(":")[1].trim())
                .orElse("");
    }

    public int getAnswerToInt(int index) {
        return Integer.parseInt(getAnswer(index));
    }

    public long getAnswerToLong(int index) {
        return Long.parseLong(getAnswer(index));
    }
}
