package aerodynamics;


public class GeneticParent extends Thread{
    
    public static int topPoints = 0, topNum = 0, bestPoints = 0;
    public static final int maxIterations = 400, nozzleCount = 80, choises = 16;
    static Nozzle[] nozzles = new Nozzle[nozzleCount];
    public static int iteration = 0, nozzle = 0;
    private static Simulation sim;
    public static Nozzle bestNozzle = null;
    public static boolean go = false;
    
    
    public GeneticParent(Simulation sim){
        this.sim = sim;
        for(int i = 0; i < nozzleCount; i++)
            nozzles[i] = new Nozzle();
    }
    
    @Override
    public void run(){
        
        try{
            Thread.sleep(500);
        }catch(InterruptedException e){}
        
        Nozzle[] top;
        
        while(!go){
            try{
                Thread.sleep(100);
            }catch(InterruptedException e){}
        }
        
        while(true){
            
            iteration++;
            
            top = evaluate();
            
            if(iteration == maxIterations)
                break;
            
            for(int i = 0; i < choises; i++){
                Nozzle[] mutations = top[i].mutate();
                for(int j = 0; j < 5; j++)
                    nozzles[j + i * 5] = mutations[j];
                
            }
        }
        
        iteration++;
        sim.reset();
        bestNozzle.install();
        nozzle = topNum + 1;
        topPoints = bestPoints;
        sim.run();
    }
    
    public static Nozzle[] evaluate(){
        
        
        for(int i = 0; i < nozzleCount; i++){
            sim.reset();
            nozzles[i].install();
            
            nozzle = i+1;
            nozzles[i].points = sim.run();
            
            if(i == 0 || nozzles[i].points > topPoints){
                topPoints = nozzles[i].points;
                topNum = i;
            }
            if(nozzles[i].points > bestPoints){
                bestPoints = nozzles[i].points;
                bestNozzle = nozzles[i];
            }
            
            //System.out.println(nozzles[i].points);
        }
        
        int[] top = new int[choises];
        for(int i = 0; i < choises; i++)
            top[i] = -1;
        
        for(int j = 0; j < choises; j++){
            for(int i = 0; i < nozzleCount; i++){
                
                if(top[j] == -1 || nozzles[i].points > nozzles[top[j]].points){
                    
                    boolean unique = true;
                    
                    for(int k = 0; k < choises; k++)
                        if(top[k] == i)
                            unique = false;
                    
                    if(unique)
                        top[j] = i;
                }
            }
        }
        
        Nozzle[] bestOnes = new Nozzle[choises];
        
        for(int i = 0; i < choises; i++)
            bestOnes[i] = nozzles[top[i]];
        
        return bestOnes;
    }
}
