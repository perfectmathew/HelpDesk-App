package com.perfect.hepdeskapp.solutions;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "solution", schema = "public")
public class Solution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String solution;
    @NotNull
    private boolean accepted;
    private String rejection_description;
    public Solution(String solution, boolean accepted) {
        this.solution = solution;
        this.accepted = accepted;
    }

    public Solution() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getRejection_description() {
        return rejection_description;
    }

    public void setRejection_description(String rejection_description) {
        this.rejection_description = rejection_description;
    }
}
