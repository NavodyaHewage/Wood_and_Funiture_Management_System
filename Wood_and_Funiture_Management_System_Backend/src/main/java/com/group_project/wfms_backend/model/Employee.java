package com.group_project.wfms_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name ="Employee")
@Data
@AllArgsConstructor
@NoArgsConstructor


public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name ="Full_Name",nullable=false,length =200)
    private String fullName;

    @Column(name="Designation",length = 100)
    private String designation;

    @Column(name="Address",columnDefinition = "TEXT")
    private String address;

    @Column(name = "NIC", unique = true, length = 20)
    private String nic;

    @Column(name ="Mobile_Number",length =15)
    private String mobileNumber;

    @Column(name="Date_Joined")
    private LocalDate dateJoined;

    @Column(name="Is_active")
    private Boolean isActive;

    @OneToMany(mappedBy = "employee",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<EmployeeAttendance> attendanceRecords;

    @OneToMany(mappedBy = "employee",cascade=CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Employeesalarydetails> employeeRecords;

    @OneToMany(mappedBy = "employee",cascade=CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Employeeloan> loans;

    @OneToMany(mappedBy = "employee",cascade = CascadeType.ALL,fetch=FetchType.LAZY)
    private List<Rawmaterialcuttingfee>cuttingFees;


}
