package aerodynamics;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
//import aerodynamics.sensors.*;

public class Simulation{
    
    public static final int metaSize = 3;
    public static ArrayList<Line> lines;
    public static ArrayList<AirParticle> particles; 
    public static PressurePoint[][] metaArray = 
            new PressurePoint[300 / metaSize + 4][300 / metaSize + 4];
//    public static ArrayList<ActiveSensor> activeSensors = new ArrayList<ActiveSensor>();
//    public static ArrayList<PassiveSensor> passiveSensors = new ArrayList<PassiveSensor>();
    
    public static int frame = 0;
    int toSpawn = 500;
    private int fpsCount = 0;       //calc/s
    private long t, lastTime;//, time3;
    double time;     //dID is delta time exponent
    Draw draw;
    
    public static double rank = 0, ranky = 0;
    
    public double deltaTime(){
        return 0.01;
    }//getDeltaTime
    
    public int run(){
        
        for(int i = 0; i < 300 / metaSize + 4; i++){        //add external pressure
            metaArray[0][i] = new PressurePoint();
            metaArray[1][i] = new PressurePoint();
            metaArray[300 / metaSize + 2][i] = new PressurePoint();
            metaArray[300 / metaSize + 3][i] = new PressurePoint();
            metaArray[i][0] = new PressurePoint();
            metaArray[i][1] = new PressurePoint();
            metaArray[i][300 / metaSize + 2] = new PressurePoint();
            metaArray[i][300 / metaSize + 3] = new PressurePoint();
        }
        
        for(int i = 2; i < 300 / metaSize + 3; i++)
                for(int j = 2; j < 300 / metaSize + 3; j++)
                    metaArray[i][j] = new PressurePoint();
            
        int maxFrames = 150 + (int)(Math.sqrt(GeneticParent.iteration) * 40);
        
        if(GeneticParent.iteration == GeneticParent.maxIterations + 1)
            maxFrames = 1000000000;
        
        for(frame = 0; frame < maxFrames; frame++){
            
            ArrayList<Integer> toRemove = new ArrayList<Integer>();
            
            
            //run particles
            try{
                //move all particles at once
                for(int i = 0; i < particles.size(); i++){
                    AirParticle p = particles.get(i);
                    
                    
                    if(p.move(deltaTime()))
                        toRemove.add(i);
                }
                
                
                //remove particles outside view
                int reducer = 0;
                for(int i: toRemove){
                    
                    rank += 2 * particles.get(i - reducer).xs;
                    
                    particles.remove(i - reducer);
                    reducer++;
                }

                toRemove = new ArrayList<Integer>();


                //run other stuff after checking if particle should be removed
                for(int i = 0; i < particles.size(); i++){
                    AirParticle p = particles.get(i);
                    try{
                        runParticle(p);
                    }catch(Exception e){
                        System.out.println(e + " at Simulation.runParticle");
                    }
                }
                
                
            }catch(ConcurrentModificationException e){
                System.out.println(e + " at Simulation.run -> run particles");
            }
            
            //spawn new particles
            spawnParticles((int)(deltaTime() * toSpawn));
            
            
            t = System.nanoTime() - lastTime;
            
            
            double toWait = 20000 - (t / 1000);
            
            if(fpsCount > 1000000 / time){          //if a second is passed, reset time
                time = 0;                           //aka keeps max time up for a second
            }
            if(t / 1000 > time){                    //update time to be max time
                time = t / 1000;
                fpsCount = 0;
            }else{
                fpsCount++;
            }
            
            if(GeneticParent.iteration == GeneticParent.maxIterations + 1 && toWait >= 1)
                try{
                    Thread.sleep((int)(toWait / 1000));
                }catch(Exception e){}
            
            lastTime = System.nanoTime();
            
        }//while loop
        
        
        
        try{
            Thread.sleep(5);
        }catch(Exception e){}
        
        
        
        for(AirParticle p: particles)
            rank += p.xs;
        
        int maxPressure = 0;
        for(PressurePoint[] p: metaArray)
            for(PressurePoint p2: p)
                if(p2.p > maxPressure)
                    maxPressure = p2.p;
        
        
        rank = Math.abs(rank) / (1 + maxPressure * maxPressure / 200);
        
        return (int)((rank - ranky) / maxFrames);
        
    }//run
    
    
    
    private void spawnParticles(int amount){
        
        try{
            int x = Nozzle.x0;
            int y = Nozzle.y0;
            for(int i = 0; i < amount; i++){
                Simulation.particles.add(new AirParticle(
                        x + Math.random() * 4 - 2, 
                        y + Math.random() * 4 - 2, 
                        Math.random() * 100 - 50,
                        Math.random() * 100 - 50));
            }
        }catch(Exception e){
        System.out.println(e + ": Probably for mouse being out of window");
        }
    }
    
    
    
    private void runParticle(AirParticle p){
        
        
        //interact with other particles
        for(int i = -2; i <= 2; i++){
            for(int j = -2; j <= 2; j++){
                
                //don't check corners
                if(i == -2 && j == -2){
                    j++;
                }else if(i == -2 && j == 2){
                    i = -1;
                    j = -2;
                }else if(i == 2 && j == -2){
                    j++;
                }else if(i == 2 && j == 2){
                    break;
                }
                
                boolean zeroes = false;
                if(i == 0 && j == 0)
                    zeroes = true;
                
                int x = (int)(p.x / metaSize) + i;
                int y = (int)((p.y - 150) / metaSize) + j;
                if(x >= 0 && x < 300/metaSize && y >= 0 && y < 300/metaSize)
                    p.interact(metaArray[x + 2][y + 2], deltaTime(), zeroes);
                
        }}
        
        
        
        //interact with lines
        for(Line l: lines){
            if(p.intersects(l, deltaTime()))
                p.collideWithLine(l.x2 - l.x1, l.y2 - l.y1);
            
        }//for lines
        
        
        //again to reduce leaking
        for(Line l: lines){
            if(p.intersects(l, deltaTime()))
                p.collideWithLine(l.x2 - l.x1, l.y2 - l.y1);
            
        }//for lines
        
        
        //again to reduce leaking
        for(Line l: lines){
            if(p.intersects(l, deltaTime()))
                p.collideWithLine(l.x2 - l.x1, l.y2 - l.y1);
            
        }//for lines
        
    }//runParticle
    
    
    
    public void init(Draw draw){
        reset();
        this.draw = draw;
        
        //init lines
        
        lastTime = System.nanoTime();
    }//init
    
    
    public void reset(){
        //init particles
        particles = new ArrayList<AirParticle>();
        
        rank = 0;
        ranky = 0;
        
        Line.drawing = false;
        lines = new ArrayList<Line>();
        
//        Simulation.lines.add(new Line(800, 290, 800, 310));
//        
//        Simulation.lines.add(new Line(800, 290, 820, 270));
//        Simulation.lines.add(new Line(800, 310, 820, 330));
//        
//        Simulation.lines.add(new Line(820, 270, 840, 270));
//        Simulation.lines.add(new Line(820, 330, 840, 330));
//        
//        Simulation.lines.add(new Line(840, 270, 860, 290));
//        Simulation.lines.add(new Line(840, 330, 860, 310));
        
        
//        for(int i = 0; i < 25; i++){
//            for(int j = 0; j < 25; j++){
//                particles.add(new AirParticle((int)(Draw.width * i / 50), (int)(Draw.height * j / 50),
//                        Math.random() * 50 - 25, Math.random() * 50 - 25));
//            }
//        }
    }//reset
}
