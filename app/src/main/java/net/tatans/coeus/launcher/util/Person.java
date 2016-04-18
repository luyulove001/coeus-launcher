package net.tatans.coeus.launcher.util;

/**
 * Created by SiLiPing on 2015/12/23.
 */
public class Person {
    private String name;
    private String pinYinName;
    private String id;
    public Person() {
        super();
    }

    public Person(String name) {
        super();
        this.name = name;
    }

    public Person(String name, String pinYinName) {
        super();
        this.name = name;
        this.pinYinName = pinYinName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinYinName() {
        return pinYinName;
    }

    public void setPinYinName(String pinYinName) {
        this.pinYinName = pinYinName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
