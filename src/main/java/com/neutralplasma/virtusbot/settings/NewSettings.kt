package com.neutralplasma.virtusbot.settings

data class NewSettings(
    var stringData: MutableMap<String, String>,
    var longData: MutableMap<String, Long>,
    var intData: MutableMap<String, Int> ){

    fun addStringData(path: String, data: String){
        this.stringData[path] = data;
    }
    fun addLongData(path: String, data: Long){
        this.longData[path] = data;
    }
    fun addIntData(path: String, data: Int){
        this.intData[path] = data;
    }

    fun getStringData(path: String): String{
        return this.stringData.getOrDefault(path, "none");
    }
    fun getLongData(path: String): Long{
        return this.longData.getOrDefault(path, 0);
    }
    fun getIntData(path: String): Int{
        return this.intData.getOrDefault(path, 0);
    }
}