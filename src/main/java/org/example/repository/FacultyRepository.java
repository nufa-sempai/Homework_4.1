package org.example.repository;

import org.example.model.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    Collection<Faculty> findAllByColorContainingIgnoreCaseOrNameContainingIgnoreCase(String color, String name);


    Optional<Faculty> findByNameIgnoreCase(String facultyName);
}