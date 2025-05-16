package com.employeeportal.model.onboarding;

import com.employeeportal.model.registration.Employee;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.persistence.*;

@Data
@Entity
@Table(name = "preview_details")
public class PreviewDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int previewId;
    private String signatureUrl;
    private LocalDate date;
    private String place;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "employeeId", nullable = false)
    private Employee employee;

    public void setDate(String date) {
        this.date = date == null ? null : LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    public String getDate() {
        return date == null ? null : date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }


}
