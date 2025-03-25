package com.lucas.taskboard.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Coluna {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private int ordem;

    @Enumerated(EnumType.STRING)
    private TipoColuna tipo;

    @ManyToOne
    private Board board;

    @OneToMany(mappedBy = "coluna", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TaskCard> cards = new ArrayList<>();
}
