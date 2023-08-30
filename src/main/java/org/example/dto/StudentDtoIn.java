package org.example.dto;

import java.util.Objects;

public class StudentDtoIn {
    private String name;
    private int age;
    private long facultyId;

    @Override
    public String toString() {
        return "StudentDtoIn{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", facultyId=" + facultyId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentDtoIn that = (StudentDtoIn) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public long getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(long facultyId) {
        this.facultyId = facultyId;
    }
}