package nl.wesleydev.wesleydev.helpers;

public class MathHelper {
    public static int getPercentage(int value, int percentage) {
        return (int)(value*(percentage/100.0f));
    }

    public static int getRandomInteger(int min, int max) {
        return (int)(Math.random() * ((max - min) + 1)) + min;
    }
}
