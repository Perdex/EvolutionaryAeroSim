package aerodynamics;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

public class Draw extends JPanel{
    
    public static final int width = 1000, height = 600;
    
    long t, lastTime, startTime;
    int fpsCount = 0;
    double time;
    Simulation sim = new Simulation();
    GeneticParent gen = new GeneticParent(sim);
    
    public Draw(){
        startTime = System.currentTimeMillis();
        sim.init(this);
        gen.start();
        lastTime = System.currentTimeMillis();
    }//public Draw
    
    
    
    @Override
    public void paint(Graphics g){
        try{
            
            //clear BG
            g.setColor(Color.black);
            g.fillRect(0, 0, width, height);
            
            
            //draw nozzles
            g.setColor(Color.white);
            
            
            for(int i = 0; i < 8; i++){
                for(int j = 0; j < 10; j++){
                    
                    int x0 = j * 70 + 320;
                    int y0 = i * 70 + 50;
                    
                    for(Line l: GeneticParent.nozzles[i * 10 + j].lines){
                        g.drawLine((int)((l.x1 - Nozzle.x0)/2 + x0), (int)((l.y1 - Nozzle.y0)/2 + y0),
                                (int)((l.x2 - Nozzle.x0)/2 + x0), (int)((l.y2 - Nozzle.y0)/2 + y0));
                        l = l.getMirror();
                        g.drawLine((int)((l.x1 - Nozzle.x0)/2 + x0), (int)((l.y1 - Nozzle.y0)/2 + y0),
                                (int)((l.x2 - Nozzle.x0)/2 + x0), (int)((l.y2 - Nozzle.y0)/2 + y0));
                    }
                    
                    g.drawString(Integer.toString(i * 10 + j + 1), x0 - 20, y0 - 20);
                    g.drawString("points: " + GeneticParent.nozzles[i * 10 + j].points, x0 - 20, y0 + 30);
                    
                    if(i * 10 + j + 1 == GeneticParent.nozzle)
                        g.drawRect(x0 - 22, y0 - 33, 70, 70);
                    
                    if(i * 10 + j + 1 == GeneticParent.topNum + 1){
                        g.setColor(Color.green);
                        g.drawRect(x0 - 22, y0 - 33, 70, 70);
                        g.setColor(Color.white);
                    }
                    
                }
            }
            
            
            //draw particles
            for(int i = 0; i < Simulation.particles.size(); i++){

                try{
                    AirParticle p = Simulation.particles.get(i);

                    int shade = (int)(p.v() / 5);
                    if(shade > 255)
                        shade = 255;
                    if(shade > 0){
                        g.setColor(new Color(255, 255 - shade, 255 - shade));
                        g.drawLine((int)p.x, (int)p.y, (int)p.x, (int)p.y);
                    }

                }catch(Exception e){System.out.println(e + " at draw particle #" + i);}
            }
            
            
            //draw lines
            for(Line l: Simulation.lines){
                g.setColor(Color.white);
                g.drawLine((int)l.x1, (int)l.y1, (int)l.x2, (int)l.y2);
            }
            
            
            g.setFont(new Font("", 0, 12));
            g.setColor(new Color(10, 175, 10));
            
            //technical details
            String s = "run time " + (System.currentTimeMillis() - startTime) / 60000 + " min; avg ";
            
            if(GeneticParent.iteration != 0)
                s += (System.currentTimeMillis() - startTime) / 1000 / GeneticParent.iteration + "s per iteration; ";
            
            s += (100 + (int)(Math.sqrt(GeneticParent.iteration) * 40)) + " frames per sim; ";
            s += "draw time: " + (int)time + "ms; " + (int)(1000000/sim.time) + "fps";
            
            g.drawString(s, width - 450, height - 5);
            
            //draw mouse coordinates
            try{
                g.drawString(Integer.toString((int)this.getMousePosition().getX()), 950, 12);
                g.drawString(Integer.toString((int)this.getMousePosition().getY()), 975, 12);
            }catch(Exception e){}
            
            
            g.setFont(new Font("", 0, 24));
            
            if(GeneticParent.iteration <= GeneticParent.maxIterations){
                g.drawString("Iteration " + GeneticParent.iteration + "/" + GeneticParent.maxIterations, 20, 35);
                g.drawString("Nozzle " + GeneticParent.nozzle, 20, 65);
            }
            
            g.drawString("Top points: " + GeneticParent.topPoints, 20, 95);
            g.drawString("Top nozzle: " + (GeneticParent.topNum + 1), 20, 125);
            
            if(GeneticParent.iteration == GeneticParent.maxIterations + 1)
                g.drawString("This is the best nozzle!", 20, 470);
            
            
            //calculate how much to wait
            t = System.currentTimeMillis() - lastTime;
            
            if(fpsCount > 25){                                  //if >25 frames passed, reset time
                time = 0;                                       //aka keeps max time up for 25 frames
            }
            if(t > time){                                       //update time to be max time
                time = t;
                fpsCount = 0;
            }else{
                fpsCount++;
            }

            long toWait = 30 - t;
            
            //wait
            if(toWait > 0)
                Thread.sleep(toWait);
            
            
            
        }catch(Exception e){
            System.out.println(e + " at Draw.paint");
        }
        
        lastTime = System.currentTimeMillis();
        
        GeneticParent.go = true;
        
        repaint();
    }//paint
    
    
}
