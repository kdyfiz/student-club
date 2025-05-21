package com.example.schoolclub.service.dto;

import com.example.schoolclub.domain.enumeration.RegistrationStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.example.schoolclub.domain.Registration} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RegistrationDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant registrationDate;

    @NotNull
    private RegistrationStatus status;

    @NotNull
    private UserDTO user;

    @NotNull
    private ClubDTO club;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Instant registrationDate) {
        this.registrationDate = registrationDate;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public ClubDTO getClub() {
        return club;
    }

    public void setClub(ClubDTO club) {
        this.club = club;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RegistrationDTO)) {
            return false;
        }

        RegistrationDTO registrationDTO = (RegistrationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, registrationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RegistrationDTO{" +
            "id=" + getId() +
            ", registrationDate='" + getRegistrationDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", user=" + getUser() +
            ", club=" + getClub() +
            "}";
    }
}
