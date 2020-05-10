package Tests;

import org.junit.jupiter.api.Test;
import sample.Main;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    Main main;

    public MainTest() {
        main = new Main();
    }

    //Ohne Veränderungen am Originellen Code, kann nichts mehr getestet werden!

    @Test
    public void encryptsCorrectly() {
        assertEquals("Ã", main.encrypt("A"));
    }
}