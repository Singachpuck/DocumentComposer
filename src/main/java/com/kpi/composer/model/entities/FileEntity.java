package com.kpi.composer.model.entities;

import com.kpi.composer.model.SupportedFormats;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private ZonedDateTime created;

    @Column
    @Enumerated(EnumType.STRING)
    private SupportedFormats format;

    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] bytes;

    @Column
    private long size;
}
