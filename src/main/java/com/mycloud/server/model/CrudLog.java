package com.mycloud.server.model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "crud_log")
public class CrudLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "file_id")
    private Long fileId;

    @Column(nullable = false)
    private String fileName;

    //enumerated annotation necessery to bypass the automatic conversion
    //to smallint by Hibernate by overwriting manual fix on startup
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Action action;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public enum Action {
        CREATE, UPDATE, DELETE
    }
}
