package org.example.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "student")
@ToString(exclude = "student")
@Builder
@Entity
public class Avatar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private long id;

    private String filePath;

    @Column(nullable = false)
    private long fileSize;

    private String mediaType;

    private byte[] data;

    @OneToOne
    private Student student;
}