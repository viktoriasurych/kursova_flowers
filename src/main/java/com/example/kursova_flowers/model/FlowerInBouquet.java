package com.example.kursova_flowers.model;

public class FlowerInBouquet {
    private int id;
    private Flower flower;
    private Bouquet bouquet;
    private double stemLength;
    private int quantity;

    public FlowerInBouquet() {}
    public FlowerInBouquet(int id, Flower flower, Bouquet bouquet, double stemLength, int quantity) {
        this.id = id;
        this.flower = flower;
        this.bouquet = bouquet;
        this.stemLength = stemLength;
        this.quantity = quantity;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Flower getFlower() { return flower; }
    public void setFlower(Flower flower) { this.flower = flower; }
    public Bouquet getBouquet() { return bouquet; }
    public void setBouquet(Bouquet bouquet) { this.bouquet = bouquet; }
    public double getStemLength() { return stemLength; }
    public void setStemLength(double stemLength) { this.stemLength = stemLength; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
