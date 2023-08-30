package org.example.repository;

import org.example.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
public interface StudentRepository extends JpaRepository<Student, Long> {
    Collection<Student> findStudentsByAgeBetween(int from, int to);
    Collection<Student> findStudentsByAge(Integer age);

    Collection<Student> findAllByFaculty_id(long facultyId);

    @Query(nativeQuery = true, value = """
            SELECT COUNT(*)
            FROM student
            """)
    Integer getTotalCountStudents();

    @Query(nativeQuery = true, value = """
            SELECT AVG(age)
            FROM student
            """)
    Double getAvgAgeStudents();

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM student
            ORDER BY id DESC
            LIMIT 5
            """)
    Collection<Student> getLastFiveStudents();
}