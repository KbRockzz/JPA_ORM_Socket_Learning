package entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "applications")

@Entity
@Table(name = "candidates")
public class Candidate {
    @Id
    @Column(name = "cand_id")
    private String id;
    private String name;
    private String email;
    private int experience;

    @ManyToMany
    @JoinTable(name = "candidates_skills",
            joinColumns = @JoinColumn(name = "candidate_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills;

    @OneToMany(mappedBy = "candidate")
    private Set<Application> applications;
}
