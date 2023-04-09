package com.a7codes.menu25.Classes;

public class ClassB {
    int id;
    String title;
    String price;
    String desc;
    int parent;
    String image;

    public ClassB(int id, String title, String price, String desc, int parent, String image) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.desc = desc;
        this.parent = parent;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
