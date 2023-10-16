package com.bit.eduventure.gps.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Table(name = "driverphoto")
@ToString
public class DriverPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "carnumber")
    private int carnumber;

    @Column(name = "photoname")
    private String photoname;

}
