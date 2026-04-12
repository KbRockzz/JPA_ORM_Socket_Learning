package entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"jobs", "candidates"})

@Entity
@Table(name = "skills")
public class Skill {
    @Id
    @Column(name = "skill_id")
    private String id;
    private String name;

    @ManyToMany(mappedBy = "skills")
    private Set<Job> jobs;

    @ManyToMany(mappedBy = "skills")
    private Set<Candidate> candidates;
}
