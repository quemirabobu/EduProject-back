package com.bit.eduventure.gps.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Table(name = "GPS")
@ToString
public class GPS {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "carnumber")
    private int carnumber;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;
    @Column(name = "time")
    private String time;
    @Column(name = "phonenumber")
    private String phonenumber;



}

