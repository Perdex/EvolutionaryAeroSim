package aerodynamics;


public class Line {
    static int lineMetaSize = 3;
    public static boolean drawing = false, snap = false, symmetry = true;
    public static double lastx = 0, lasty = 0;
    public static int gridSize = 10;
    public static Line[][] metaLine = new Line[Draw.width / lineMetaSize + 4][Draw.height / lineMetaSize + 4];
    
    public double x1, y1, x2, y2;
    
    
    public Line(double x1, double y1, double x2, double y2){
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }//line
    
    public Line getMirror(){
        
        return new Line(x1, 600 - y1, x2, 600 - y2);
        
    }
    
    public Line copyOf(){
        return new Line(x1, y1, x2, y2);
    }
    
    public void mutate(){
        
        int r = (int)(Math.random() * 4);
        
        if(r == 0)
            x1 += Math.random() * 20 - 10;
        else if(r == 1)
            x2 += Math.random() * 20 - 10;
        else if(r == 2)
            y1 += Math.random() * 20 - 10;
        else
            y2 += Math.random() * 20 - 10;
        
    }//mutate
    
}
