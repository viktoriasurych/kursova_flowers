    package com.example.kursova_flowers.model;

    import java.util.List;

    public class Bouquet {
        private int id;
        private String name;
        private List<FlowerInBouquet> flowers;
        private List<Accessory> accessories;

        public Bouquet() {}
        public Bouquet(int id, String name, List<FlowerInBouquet> flowers, List<Accessory> accessories) {
            this.id = id;
            this.name = name;
            this.flowers = flowers;
            this.accessories = accessories;
        }
        // Геттери і сеттери...
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public List<FlowerInBouquet> getFlowers() { return flowers; }
        public void setFlowers(List<FlowerInBouquet> flowers) { this.flowers = flowers; }
        public List<Accessory> getAccessories() { return accessories; }
        public void setAccessories(List<Accessory> accessories) { this.accessories = accessories; }

    }
