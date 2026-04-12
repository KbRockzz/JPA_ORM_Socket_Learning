package entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"job", "candidate"})

@Entity
@Table(name = "applications")
@IdClass(Application.ApplicationPK.class)
public class Application {
    @Id
    @Column(name = "candidate_id")
    private String candidateId;

    @Id
    @Column(name = "job_id")
    private String jobId;

    @Column(name = "appliedDate")
    private LocalDate appliedDate;

    @Enumerated(EnumType.STRING)
    private AppStatus status;

    @ManyToOne
    @JoinColumn(name = "candidate_id", insertable = false, updatable = false)
    private Candidate candidate;

    @ManyToOne
    @JoinColumn(name = "job_id", insertable = false, updatable = false)
    private Job job;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @EqualsAndHashCode
    public static class ApplicationPK implements Serializable {
        private String candidateId;
        private String jobId;
    }
}