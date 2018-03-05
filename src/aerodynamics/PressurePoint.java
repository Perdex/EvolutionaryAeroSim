package aerodynamics;



public class PressurePoint {
    double x, y, xs, ys;
    int p, frame;
    
    public PressurePoint(){
        p = 0;
    }
    
    public void add(AirParticle particle){
        
        if(frame != Simulation.frame || p == 0){
            x = particle.x;
            y = particle.y;
            xs = particle.xs;
            ys = particle.ys;
            frame = Simulation.frame;
            p = 1;
            return;
        }
        
        x += (particle.x - x) / p;
        y += (particle.y - y) / p;
        xs += (particle.xs - xs) / p;
        ys += (particle.ys - ys) / p;
        p++;
        
    }
}
