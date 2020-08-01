package com.neutralplasma.virtusbot.storage;

public class PlayerStorage {

    private String name;
    private String identifier;
    private String license;
    private String firstname;
    private String lastname;
    private String phonenumber;
    private int is_dead;
    private String group;
    private int permission_level;
    private int bank;
    private int money;
    private String loadout;
    private String job;
    private int jobgrade;

    public PlayerStorage(String name, String identifier, String license, String firstname, String lastname,
                         String phonenumber, int is_dead, String group, int permission_level, int bank, int money,
                         String loadout, String job, int jobgrade){
        this.name = name;
        this.identifier = identifier;
        this.license = license;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phonenumber = phonenumber;
        this.is_dead = is_dead;
        this.group = group;
        this.permission_level = permission_level;
        this.bank = bank;
        this.money = money;
        this.loadout = loadout;
        this.job = job;
        this.jobgrade = jobgrade;
    }


    public int getBank() {
        return bank;
    }

    public int getIs_dead() {
        return is_dead;
    }

    public int getMoney() {
        return money;
    }

    public int getPermission_level() {
        return permission_level;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getGroup() {
        return group;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getLastname() {
        return lastname;
    }

    public String getLicense() {
        return license;
    }

    public String getLoadout() {
        return loadout;
    }

    public String getName() {
        return name;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public String getJob() {
        return job;
    }

    public int getJobgrade() {
        return jobgrade;
    }

    public void setBank(int bank) {
        this.bank = bank;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setIs_dead(int is_dead) {
        this.is_dead = is_dead;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public void setLoadout(String loadout) {
        this.loadout = loadout;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPermission_level(int permission_level) {
        this.permission_level = permission_level;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public void setJobgrade(int jobgrade) {
        this.jobgrade = jobgrade;
    }
}
