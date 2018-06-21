package aerodynamics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Aerodynamics{
    
    
    static JFrame fr = new JFrame("It's going to become conscious one day!");
    static JPanel panel = new Draw();
    public static void main(String[] args){
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        fr.setSize(Draw.width + 6, Draw.height + 35);
        fr.add(panel);
        fr.setResizable(false);
        fr.setLocation(150, 20);
        fr.setVisible(true);
        
        panel.setFocusable(true);
    }
}
