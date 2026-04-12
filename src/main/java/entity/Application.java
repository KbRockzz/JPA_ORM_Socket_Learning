package entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

@Entity
@Table(name = "applications")
public class Application {
    @Column(name = "appliedDate")
    private LocalDate appliedDate;

    @Enumerated(EnumType.STRING)
    private AppStatus status;

    @Id
    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    @Id
    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;
}
