package com.example.diabetes;

public class Appointment {
    private Integer id;
    private Integer patientUserId;
    private Integer doctorUserId;
    private String scheduledAt; // string for now
    private String status;      // REQUESTED/SCHEDULED/COMPLETED/CANCELLED
    private String patientNote;
    private String doctorNote;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getPatientUserId() { return patientUserId; }
    public void setPatientUserId(Integer patientUserId) { this.patientUserId = patientUserId; }

    public Integer getDoctorUserId() { return doctorUserId; }
    public void setDoctorUserId(Integer doctorUserId) { this.doctorUserId = doctorUserId; }

    public String getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(String scheduledAt) { this.scheduledAt = scheduledAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPatientNote() { return patientNote; }
    public void setPatientNote(String patientNote) { this.patientNote = patientNote; }

    public String getDoctorNote() { return doctorNote; }
    public void setDoctorNote(String doctorNote) { this.doctorNote = doctorNote; }
}
