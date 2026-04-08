package com.example.movieticket.models;

public class Ticket {
    private String id;
    private String userId;
    private String showtimeId;
    private String seatNumber;
    private double totalPrice;
    private String timestamp;

    public Ticket() {}

    public Ticket(String id, String userId, String showtimeId, String seatNumber, double totalPrice, String timestamp) {
        this.id = id;
        this.userId = userId;
        this.showtimeId = showtimeId;
        this.seatNumber = seatNumber;
        this.totalPrice = totalPrice;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getShowtimeId() { return showtimeId; }
    public void setShowtimeId(String showtimeId) { this.showtimeId = showtimeId; }
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}