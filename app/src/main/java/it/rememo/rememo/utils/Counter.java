package it.rememo.rememo.utils;

// Counter for multiple firebase calls
public class Counter{
    int value;
    public Counter(int value) {
        this.value = value;
    }

    public int decrease() {
        return --value;
    }
}