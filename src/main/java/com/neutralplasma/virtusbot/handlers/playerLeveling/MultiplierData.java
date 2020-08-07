package com.neutralplasma.virtusbot.handlers.playerLeveling;

public class MultiplierData {
    private int multiplier;
    private long endsOn = 0;


    public int getMultiplier() {
        if(isActive()){
            return multiplier;
        }else{
            return 1;
        }
    }

    public void setMultiplier(int multiplier, long time){
        this.multiplier = multiplier;
        this.endsOn = System.currentTimeMillis() + time;
    }

    public boolean isActive(){
        return endsOn - System.currentTimeMillis() > 0;
    }
}
