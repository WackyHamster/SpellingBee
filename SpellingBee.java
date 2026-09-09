import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SpellingBee {
    private static final String menuSyntax = "0";
    private static List<String> panagrams;
    private static List<String> words;
    private static Scanner scanner = new Scanner(System.in);
    private static String input = "";

    static {
        try {
            SpellingBee.panagrams = Files.readAllLines(Paths.get("panagrams.txt"));
            SpellingBee.words = Files.readAllLines(Paths.get("words.txt"));
        } catch(IOException error) {
            error.printStackTrace();
        }
    }

    private final String panagram = SpellingBee.panagrams.get((int) (Math.random() * SpellingBee.panagrams.size()));
    private final char center = this.panagram.charAt((int) (Math.random() * this.panagram.length()));
    private final String comb = makeComb();
    private final ArrayList<String> guessed = new ArrayList<>();
    private final int maxWords;
    private final int maxScore;
    private int reqScore;
    private int score = 0;

    public SpellingBee() {
        int max = 0;
        int count = 0;
        for(String val : SpellingBee.words) {
            int add = getValue(val, false);
            if (add > 0) {
                count++;
                max += add;
                //System.out.println("+" + add + "; " + val);
            }
        }
        this.maxWords = count;
        this.maxScore = max;
        this.reqScore = (int) Math.round(this.maxScore / 4.0 + 800 / (1 + Math.pow(Math.E,-(0.0018*this.maxScore - 5.5))));
        if (this.reqScore > this.maxScore) {
            this.reqScore = this.maxScore;
        }

        System.out.println("Type " + SpellingBee.menuSyntax + " for menu" + "\n" + this.maxWords + " words" + "\n" + this.maxScore + " points possible");
        printStats();
        while (this.score < this.reqScore && SpellingBee.scanner.hasNextLine()) {
            
            SpellingBee.input = SpellingBee.scanner.nextLine().trim().toLowerCase();
            if (SpellingBee.input.equals(SpellingBee.menuSyntax)) {
                break;
            }
            this.score += getValue(SpellingBee.input, true);
            printStats();
        }
        if (this.score >= this.reqScore) {
            System.out.println("Comb Complete!");
        } else {
            System.out.println("Exiting...");
        }

        SpellingBee.input = "";
        new SpellingBee();
    }

    private boolean isInList(List<String> words, String target, int low, int high) {
        if (low > high) {
            return false;
        }
        int mid = low + (high - low) / 2;
        if (words.get(mid).compareTo(target) == 0) {
            return true;
        } else if (words.get(mid).compareTo(target) > 0) {
            return isInList(words, target, low, mid - 1);
        } else {
            return isInList(words, target, mid + 1, high);
        }
    }
    private String makeComb() {
        ArrayList<Integer> index = new ArrayList<>();
        for(int i = 0; i < this.panagram.length(); i++) {
            if (panagram.charAt(i) != this.center) {
                index.add(i);
            }
        }
        return "    " + removeLetter(index) + "   " + removeLetter(index) + "\n  " + removeLetter(index) + "   " + this.center + "   " + removeLetter(index) + "\n" + "    " + removeLetter(index) + "   " + removeLetter(index);
    }
    private char removeLetter(ArrayList<Integer> list) {
        return this.panagram.charAt(list.remove((int) (Math.random() * list.size())));
    }  
    private int occurrences(String guess, char target) {
        int count = 0;
        for (int i = 0; i < guess.length(); i++) {
            if (guess.charAt(i) == target) {
                count++;
            }
        }
        return count;
    }
    private int panagramBonus(String guess, boolean isGuess) {
        StringBuilder letterChecker = new StringBuilder();
        for (int i = 0; i < this.panagram.length(); i++) {
            letterChecker.append("(?=.*").append(this.panagram.charAt(i)).append(")");
        }
        letterChecker.append(".*");
        if (guess.matches(letterChecker.toString())) {
            if (isGuess) {
                System.out.println("Panagram!");
            }
            return 2;
        } else {
            return 1;
        }
    }
    private boolean isValidWord() {
        return true;
    }
    private int getValue(String guess, boolean isGuess) {
        if (isInList(SpellingBee.words, guess, 0, SpellingBee.words.size() - 1)) {
            if (!isInList(this.guessed, guess, 0, this.guessed.size() - 1)) {
                if (guess.matches("[" + this.panagram + "]+")) {
                    if (guess.contains(String.valueOf(this.center))) {
                        int points = panagramBonus(guess, isGuess) * (guess.length() + occurrences(guess, this.center) - 1);
                        if (isGuess) {
                            this.guessed.add(guess);
                            Collections.sort(this.guessed);
                            System.out.println("+" + points);
                        }
                        return points;
                    } else if (isGuess) {
                        System.out.println("Must contain center letter");
                    }
                } else if (isGuess) {
                    System.out.println("May only contain provided letters");
                }
            } else if (isGuess) {
                System.out.println("Already guessed" + "\n" + "Guessed: ");
                for (String val : this.guessed) {
                    System.out.print(val + " ");
                }
                System.out.println();
            }
        } else if (isGuess) {
            System.out.println("Not in word list");
        }
        return 0;
    }
    private void printStats() {
        System.out.println(this.comb + "\n" + "Score: " + this.score + " / " + this.reqScore + "\n" + "Words: " + this.guessed.size() + " / " + this.maxWords);
        //System.out.println(this.panagram);
    }
}
