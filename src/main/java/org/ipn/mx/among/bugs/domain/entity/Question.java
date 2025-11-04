package org.ipn.mx.among.bugs.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(length=200)
    private String questionText;

    @Column(columnDefinition = "JSON")
    private String options;

    @ManyToMany(mappedBy = "questions")
    private List<Trivia> trivias;
}
