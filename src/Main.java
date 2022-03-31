import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setBackground(Color.white);
        GraphPanel gp = new GraphPanel();
        window.add(gp);
        window.pack();
        gp.startThread();
        window.setAlwaysOnTop(true);
        window.setVisible(true);
    }
}
