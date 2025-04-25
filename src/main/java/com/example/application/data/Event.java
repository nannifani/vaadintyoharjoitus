package com.example.application.data;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;

@Entity
public class Event extends AbstractEntity {

    private String title;
    private String organizer;
    private LocalDate eventDate;
    private String contactEmail;
    private String contactPhone;

    @ManyToOne
    private Location location;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrganizer() {
        return organizer;
    }
    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }
    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getContactEmail() {
        return contactEmail;
    }
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }
}
