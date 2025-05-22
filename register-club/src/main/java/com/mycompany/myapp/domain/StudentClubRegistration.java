package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.RegistrationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A StudentClubRegistration.
 */
@Entity
@Table(name = "student_club_registration")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StudentClubRegistration implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "registration_date", nullable = false)
    private Instant registrationDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RegistrationStatus status;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "studentClubRegistration")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "user", "studentClubRegistration" }, allowSetters = true)
    private Set<StudentProfile> studentProfiles = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private Club club;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StudentClubRegistration id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getRegistrationDate() {
        return this.registrationDate;
    }

    public StudentClubRegistration registrationDate(Instant registrationDate) {
        this.setRegistrationDate(registrationDate);
        return this;
    }

    public void setRegistrationDate(Instant registrationDate) {
        this.registrationDate = registrationDate;
    }

    public RegistrationStatus getStatus() {
        return this.status;
    }

    public StudentClubRegistration status(RegistrationStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }

    public Set<StudentProfile> getStudentProfiles() {
        return this.studentProfiles;
    }

    public void setStudentProfiles(Set<StudentProfile> studentProfiles) {
        if (this.studentProfiles != null) {
            this.studentProfiles.forEach(i -> i.setStudentClubRegistration(null));
        }
        if (studentProfiles != null) {
            studentProfiles.forEach(i -> i.setStudentClubRegistration(this));
        }
        this.studentProfiles = studentProfiles;
    }

    public StudentClubRegistration studentProfiles(Set<StudentProfile> studentProfiles) {
        this.setStudentProfiles(studentProfiles);
        return this;
    }

    public StudentClubRegistration addStudentProfile(StudentProfile studentProfile) {
        this.studentProfiles.add(studentProfile);
        studentProfile.setStudentClubRegistration(this);
        return this;
    }

    public StudentClubRegistration removeStudentProfile(StudentProfile studentProfile) {
        this.studentProfiles.remove(studentProfile);
        studentProfile.setStudentClubRegistration(null);
        return this;
    }

    public Club getClub() {
        return this.club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public StudentClubRegistration club(Club club) {
        this.setClub(club);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StudentClubRegistration)) {
            return false;
        }
        return getId() != null && getId().equals(((StudentClubRegistration) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StudentClubRegistration{" +
            "id=" + getId() +
            ", registrationDate='" + getRegistrationDate() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
