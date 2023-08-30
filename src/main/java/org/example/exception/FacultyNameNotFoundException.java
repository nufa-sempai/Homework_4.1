package org.example.exception;

public class FacultyNameNotFoundException extends RuntimeException {
    private final String facultyName;

    public FacultyNameNotFoundException(String facultyName) {
        this.facultyName = facultyName;
    }

    @Override
    public String getMessage() {
        return "Факультет с именем: " + facultyName + " не найден";
    }
}