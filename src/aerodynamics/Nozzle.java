package aerodynamics;

import java.util.ArrayList;

public class Nozzle {
    
    public static final int x0 = 150, y0 = 300;
    
    public int points = 0;
    public ArrayList<Line> lines = new ArrayList();
    
    // Initial constructor
    public Nozzle(){
        createSegments(3);
    }
    
    
    private void createSegments(int n){
        for(int i = 0; i < n; i++){
            double x1 = Math.pow(Math.random() * 6 - 3, 3), y1 = Math.pow(Math.random() * 6 - 3, 3), 
                    x2 = Math.pow(Math.random() * 6 - 3, 3), y2 = Math.pow(Math.random() * 6 - 3, 3);
            
            lines.add(new Line(x0 + x1, y0 + y1, x0 + x2, y0 + y2));
        }
    }//createSegments
    
    
    // Copy-mutate constructor
    public Nozzle(ArrayList<Line> l){
        lines = l;
        
        // Mutate a random number of lines
        int r = (int)Math.pow(Math.random() * 3, 2);
        for(int i = 0; i < r; i++){
            lines.get((int)(lines.size() * Math.random())).mutate();
        }
        
        // 10% of the time, add a segment. 10% of the time, remove a segment.
        r = (int)(Math.random() * 10);
        if(r == 0){
            createSegments(1);
        }else if(r == 1 && lines.size() > 1){
            lines.remove((int)(Math.random() * lines.size()));
        }
    }//public nozzle
    
    // Replaces the simulated nozzle with this one
    public void install(){
        
        Simulation.lines = new ArrayList();
        
        for(Line l: lines){
            Simulation.lines.add(l);
            Simulation.lines.add(l.getMirror());
        }
        
    }//install
    
    // Create 5 offspring
    public Nozzle[] mutate(){
        Nozzle[] mutations = new Nozzle[5];
        
        for(int i = 0; i < 5; i++){
            mutations[i] = new Nozzle(copy(lines));
        }
        
        return mutations;
    }//mutate
    
    
    private ArrayList<Line> copy(ArrayList<Line> in){
        ArrayList<Line> out = new ArrayList();
        for(Line i: in)
            out.add(i.copyOf());
        
        return out;
    }//copy
    
}
