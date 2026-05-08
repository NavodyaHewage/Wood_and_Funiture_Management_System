package com.group_project.wfms_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name="Employee_Attendance",uniqueConstraints={
        @UniqueConstraint(columnNames ={"Employee_id","Date"})
})
@Data
@AllArgsConstructor
@NoArgsConstructor

public class EmployeeAttendance {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="Attend_id")
    private Integer attendId;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="Employee_id",nullable =false)
    private Employee employee;

    @Column(name="Date",nullable= false)
    private LocalDate date;

    @Convert(converter = AttendanceStatusConverter.class)
    @Column(name="Status")
    private AttendanceStatus status = AttendanceStatus.PRESENT;

    @Column(name ="Check_In")
    private LocalTime checkIn;

    @Column(name ="Check_Out")
    private LocalTime checkOut;

    @Column(name="Remarks",columnDefinition = "TEXT")
    private String remarks;






}
