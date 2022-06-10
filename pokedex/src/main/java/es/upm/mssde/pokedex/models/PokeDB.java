package es.upm.mssde.pokedex.models;

import java.util.ArrayList;

public class PokeDB {
    private String num;
    private String name;
    private Type type1;
    private Type type2;

    public PokeDB() {
        this.num = "";
        this.name = "";
        this.type1 = new Type();
        this.type2 = new Type();
    }

    public PokeDB(String num, String name, Type type1) {
        this.num = num;
        this.name = name;
        this.type1 = type1;
        this.type2 = new Type();
    }

    public PokeDB(String num, String name, Type type1, Type type2) {
        this.num = num;
        this.name = name;
        this.type1 = type1;
        this.type2 = type2;
    }

    public String getNum() {
        return num;
    }

    public String getName() {
        return name;
    }

    public Type getType1() {
        return type1;
    }

    public Type getType2() {
        return type2;
    }

    public ArrayList<Type> getTypes() {
        ArrayList<Type> types = new ArrayList<>();

        types.add(type1);

        if (type2 != null) {
            types.add(type2);
        }

        return types;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType1(Type type1) {
        this.type1 = type1;
    }

    public void setType2(Type type2) {
        this.type2 = type2;
    }

    public String toString() {
        return "PokeDB [num=" + num + ", name=" + name + ", type1=" + type1 + ", type2=" + type2 + "]";
    }
}
