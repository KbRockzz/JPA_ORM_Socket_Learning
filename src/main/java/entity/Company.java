package entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "jobs")

@JsonIgnoreProperties

@Entity
@Table(name = "companies")
public class Company {
    @Id
    @Column(name = "company_id")
    private String id;
    private String name;
    private String industry;

    @OneToMany(mappedBy = "company")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Set<Job> jobs;
}
