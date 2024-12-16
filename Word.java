import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Word {
    private String word;
    private List<Double> vector;

    // Constructor that takes a line from the file
    public Word(String line) {
        String[] parts = line.split(" "); // Splits line by spaces
        this.word = parts[0]; // First part is the word
        
        this.vector = new ArrayList<>(); // The rest is the vector

        // Turns the numbers into the vector
        for (int i = 1; i < parts.length; i++) {
            this.vector.add(Double.parseDouble(parts[i])); // Adds the double to the arraylist
        }
    }

    // Getter for word
    public String getWord() {
        return word;
    }

    // Getter for vector
    public List<Double> getVector() {
        return vector;
    }

    // Compare to method that compares the vectors of a word given an axis
    // Returns 0 if the vector values are the same the index axis
    // Returns 1 if word is less than this
    // Returns -1 if word is greater than this
    public int compareTo(Word word, int axis){
        if (this.getVector().get(axis) == word.getVector().get(axis)) return 0; // Equal case
        return this.getVector().get(axis) < word.getVector().get(axis) ? -1 : 1;
    }

    // Static method to read words from file
    // Returns a list of word objects
    public static List<Word> readWordsFromFile(String filePath) throws IOException {
        List<Word> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(new Word(line));
            }
        }
        return words;
    }
}









