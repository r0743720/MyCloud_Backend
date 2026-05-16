package com.mycloud.server.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "movement_alerts")
@Data
@NoArgsConstructor
public class MovementAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private Double accelerationX;

    @Column(nullable = false)
    private Double accelerationY;

    @Column(nullable = false)
    private Double accelerationZ;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
}
