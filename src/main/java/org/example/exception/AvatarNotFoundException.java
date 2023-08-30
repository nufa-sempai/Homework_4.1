package org.example.exception;

public class AvatarNotFoundException extends RuntimeException {
    private final long id;

    public AvatarNotFoundException(Long studentId) {
        this.id = studentId;
    }

    @Override
    public String getMessage() {
        return "У студента с id: " + id + " аватар не найден";
    }
}