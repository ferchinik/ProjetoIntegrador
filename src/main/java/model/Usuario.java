package model;

public class Usuario {
    private int id;
    private int carteiraId;

    public Usuario(int id, int carteiraId) {
        this.id = id;
        this.carteiraId = carteiraId;
    }

    public int getId() {
        return id;
    }

    public int getCarteiraId() {
        return carteiraId;
    }
}
