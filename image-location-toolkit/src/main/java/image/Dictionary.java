package image;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class is used to filter text extracted from an image by Tesseract OCR through an English dictionary to find any
 * valid words.
 */
public class Dictionary {
    private Scanner scanner;
    private ArrayList<String> strings;

    /**
     * Constructor - initializes the scanner.
     */
    public Dictionary() {
        try {
            InputStream inputStream = Dictionary.class.getClassLoader().getResourceAsStream("dictionary.txt");
            scanner = new Scanner(ImageOperations.streamToFile(inputStream));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Searches through the dictionary for matches or partial matches to the input.
     *
     * @param imgStr a string of text extracted from the image by Tesseract OCR
     * @return an ArrayList of strings that were found as matches to dictionary words
     */
    public ArrayList<String> findMatches(String imgStr) {
        strings = new ArrayList<>(); // The strings that were found as matches
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            if (line.length() > 2 && imgStr.toUpperCase().contains(line.toUpperCase())) {
                for (int i = 0; i < strings.size(); i++) {
                    String match = strings.get(i);
                    if (match.contains(line)) {
                        strings.remove(i);
                        line = match;
                        break; // If the line is a substring of a match, the line is not added to the list of matches
                    }
                    if (line.toUpperCase().contains(match.toUpperCase())) {
                        strings.remove(i); // If the line has a match as a substring, the match is removed from the list
                        if (i == strings.size() - 1)
                            i--; // Corrects position to prevent skipping an element in the list
                    }
                }
                strings.add(line);
            }
        }
        return strings;
    }

    /**
     * Returns the ArrayList of dictionary matches as a String. Overrides java.lang.Object.toString().
     *
     * @return a String representation of the ArrayList of dictionary matches
     */
    public String toString() {
        if (strings != null) {
            String returnStr = "";
            for (String s : strings) {
                returnStr += s + " ";
            }
            return returnStr;
        }
        return "";
    }
}