package com.neutralplasma.virtusbot.handlers.playerLeveling

data class MultiplierData(
        var multiplier: Int,
        var endsOn: Long,

){

    private val isActive: Boolean
        get() = endsOn - System.currentTimeMillis() > 0

    fun setMultiplier(multiplier: Int, endsOn: Long){
        this.multiplier = multiplier;
        this.endsOn = endsOn + System.currentTimeMillis();
    }
    fun getActiveMultiplier(): Int {
        if (isActive){
            return this.multiplier;
        }
        return 1;
    }
}