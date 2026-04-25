package com.group_project.wfms_backend.model;

import lombok.Getter;

@Getter
public enum AttendanceStatus {
    PRESENT("Present"),
    ABSENT("Absent"),
    HALF_DAY("HAlf Day"),
    LEAVE("Leave");
    private final  String displayName;


    AttendanceStatus(String displayName) {
        this.displayName = displayName;

    }
}
