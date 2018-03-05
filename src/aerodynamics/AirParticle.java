package aerodynamics;

public class AirParticle {
    
    public double x, y, xs, ys, t;
    
    
    public AirParticle(double x, double y, double xs, double ys){
        this.x = x;
        this.y = y;
        this.xs = xs;
        this.ys = ys;
    }
    
    public double v(){
        return Math.sqrt(xs * xs + ys * ys);
    }
    
    
    public boolean move(double t){
        //boolean answers "should this be removed?"
        
        x += xs * t;
        y += ys * t;
        
        if(x < 0 || x >= 300 || y < 150 || y >= Draw.height - 150){
            Simulation.ranky += Math.abs(ys);
            return true;
        }
        
        try{
            Simulation.metaArray[(int)(x/Simulation.metaSize) + 2][(int)((y - 150)/Simulation.metaSize) + 2].add(this);
        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println("AirParticle.move: out of array");
        }
        
        return false;
    }
    
    
    public void interact(PressurePoint p, double time, boolean containsThis){
        
        
        if(p.p <= 0)
            return;
        
        double dx = p.x - this.x;
        double dy = p.y - this.y;
        double pressure = p.p;
        
        
        
        if(containsThis){
            if(p.p == 1)        //if this is only particle in its p.p
                return;
            dx = (p.p * this.x - p.x)/(p.p - 1) - this.x;   //correct position
            dy = (p.p * this.y - p.y)/(p.p - 1) - this.y;
            pressure--;
        }
        
        
        double vLength = Math.sqrt(dx * dx + dy * dy);
        
        if(vLength == 0)
            return;
        
        //if(Math.sqrt(vLengthSq) > )
        //double relativeSpeed = Math.sqrt(square(this.xs - p.xs) + square(this.ys - p.ys));
        
        dx /= vLength * (vLength + 1) * (vLength + 1);
        dy /= vLength * (vLength + 1) * (vLength + 1);
        
        
        this.xs -= dx * time * 200 * pressure;
        this.ys -= dy * time * 200 * pressure;
        
        
//        this.t -= Math.sqrt(square(dx * time * p.t) + square(dy * time * p.t));
//        
//        this.t = (this.t + p.t) / 2;
//        p.t = this.t;
    }//interact
    
//    private double square(double x){
//        return x * x;
//    }//square
    
    
    
    public void collideWithLine(double lineX, double lineY){
        double lLength = Math.sqrt(lineY*lineY + lineX*lineX);
        
        
        lineX /= lLength;
        lineY /= lLength;
        
        double temp = lineX * this.xs + lineY * this.ys;
        this.xs -= 0.01 * temp * lineX;
        this.ys -= 0.01 * temp * lineY;
        
        temp = lineX;
        lineX = -lineY;
        lineY = temp;
        temp = -2 * (lineX * this.xs + lineY * this.ys);
        
        this.xs += lineX * temp;
        this.ys += lineY * temp;
        
    }//collideWall
    
    
    
    
    public boolean intersects(Line l, double time){
        double lx = l.x2 - l.x1;                    //line's vector's x
        double ly = l.y2 - l.y1;                    //line's vector's y
        double px = xs * time;          //particle's velocity vector's x
        double py = ys * time;          //particle's velocity vector's y
        
        double pXl = px * ly - py * lx;             //(particle velocity) X line
        
        
        //check if collides
        if(pXl != 0){
            
            double plx = l.x1 - x;                //particle -> line = (plx, ply)
            double ply = l.y1 - y;
            double plXpv = plx * py - ply * px;     //(particle -> line) X (particle velocity)
            
            double n = plXpv / pXl;
            
            if(n >= 0 && n <= 1){
                
                double plXl = plx * ly - ply * lx;  //(particle -> line) X line
                
                double m = plXl / pXl;
                
                if(m >= 0 && m <= 1){
                    x += n * px;
                    y += n * py;
                    return true;
                }
            }
        }
        return false;
    }//intersects
    
}
