package com.shared.idgeneration.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "id_generation")
public class IdGeneration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotNull(message = "ID type cannot be null.")
    private String idType; // E.g., "PROJECT", "SUPERVISOR"

    @Column(nullable = false)
    @NotNull(message = "Last ID cannot be null.")
    private Long lastId;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getIdType() { return idType; }
    public void setIdType(String idType) { this.idType = idType; }
    public Long getLastId() { return lastId; }
    public void setLastId(Long lastId) { this.lastId = lastId; }
}
