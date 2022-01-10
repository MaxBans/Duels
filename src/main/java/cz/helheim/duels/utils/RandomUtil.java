package cz.helheim.duels.utils;

import java.util.Random;

public class RandomUtil {

    char[] chars = new char[]{ 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };

    public String generate(){
        Random rnd = new Random();
        int charsLength = chars.length;
        int passLength = 8;
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < passLength; i++) {
            int index = rnd.nextInt(charsLength - i - 1);
            // Simple swap
            char a = chars[i + index];
            chars[i + index] = chars[i];
            chars[i] = a;
            password.append(a);
        }

        return password.toString();
    }
}
