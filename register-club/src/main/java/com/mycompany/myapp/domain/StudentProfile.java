package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A StudentProfile.
 */
@Entity
@Table(name = "student_profile")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StudentProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "student_id", nullable = false, unique = true)
    private String studentId;

    @NotNull
    @Size(max = 100)
    @Column(name = "full_name", length = 100, nullable = false)
    private String fullName;

    @NotNull
    @Column(name = "grade", nullable = false)
    private String grade;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "studentProfiles", "club" }, allowSetters = true)
    private StudentClubRegistration studentClubRegistration;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StudentProfile id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentId() {
        return this.studentId;
    }

    public StudentProfile studentId(String studentId) {
        this.setStudentId(studentId);
        return this;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getFullName() {
        return this.fullName;
    }

    public StudentProfile fullName(String fullName) {
        this.setFullName(fullName);
        return this;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGrade() {
        return this.grade;
    }

    public StudentProfile grade(String grade) {
        this.setGrade(grade);
        return this;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public StudentProfile user(User user) {
        this.setUser(user);
        return this;
    }

    public StudentClubRegistration getStudentClubRegistration() {
        return this.studentClubRegistration;
    }

    public void setStudentClubRegistration(StudentClubRegistration studentClubRegistration) {
        this.studentClubRegistration = studentClubRegistration;
    }

    public StudentProfile studentClubRegistration(StudentClubRegistration studentClubRegistration) {
        this.setStudentClubRegistration(studentClubRegistration);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StudentProfile)) {
            return false;
        }
        return getId() != null && getId().equals(((StudentProfile) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StudentProfile{" +
            "id=" + getId() +
            ", studentId='" + getStudentId() + "'" +
            ", fullName='" + getFullName() + "'" +
            ", grade='" + getGrade() + "'" +
            "}";
    }
}
