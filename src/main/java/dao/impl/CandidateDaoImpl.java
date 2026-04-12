package dao.impl;

import dao.CandidateDao;
import entity.Candidate;
import entity.JobStatus;

import java.util.List;

public class CandidateDaoImpl extends AbstractGenericDaoImpl<Candidate, String> implements CandidateDao {

    public CandidateDaoImpl() {
        super(Candidate.class);
    }

    @Override
    public List<Candidate> findBySkillInOpenJobs(String skill) {
        String jpql = "SELECT DISTINCT c FROM Candidate c " +
                      "JOIN c.skills s " +
                      "JOIN c.applications a " +
                      "JOIN a.job j " +
                      "WHERE s.name = :skill " +
                      "AND j.status = :status";
        return doInTransaction(em ->
            em.createQuery(jpql, Candidate.class)
              .setParameter("skill", skill)
              .setParameter("status", JobStatus.OPEN)
              .getResultList()
        );
    }

    public static void main(String[] args) {
        CandidateDao dao = new CandidateDaoImpl();

        // --- loadAll ---
        System.out.println("=== loadAll ===");
        List<Candidate> all = dao.loadALl();
        all.forEach(System.out::println);

        // --- findById ---
        System.out.println("\n=== findById(C1) ===");
        Candidate c1 = dao.findById("C1");
        System.out.println(c1);

        // --- create ---
        System.out.println("\n=== create (C99) ===");
        Candidate newCandidate = Candidate.builder()
                .id("C99")
                .name("Test User")
                .email("test.user@email.com")
                .experience(1)
                .build();
        dao.create(newCandidate);
        System.out.println("Created: " + dao.findById("C99"));

        // --- update ---
        System.out.println("\n=== update (C99 experience -> 5) ===");
        newCandidate.setExperience(5);
        Candidate updated = dao.update(newCandidate);
        System.out.println("Updated: " + updated);

        // --- delete ---
        System.out.println("\n=== delete (C99) ===");
        boolean deleted = dao.delete("C99");
        System.out.println("Deleted: " + deleted);
        System.out.println("After delete findById(C99): " + dao.findById("C99"));

        // --- findBySkillInOpenJobs ---
        System.out.println("\n=== findBySkillInOpenJobs(Java) ===");
        List<Candidate> javaInOpen = dao.findBySkillInOpenJobs("Java");
        javaInOpen.forEach(System.out::println);

        System.out.println("\n=== findBySkillInOpenJobs(Python) ===");
        List<Candidate> pythonInOpen = dao.findBySkillInOpenJobs("Python");
        pythonInOpen.forEach(System.out::println);

        System.out.println("\n=== findBySkillInOpenJobs(SQL) ===");
        List<Candidate> sqlInOpen = dao.findBySkillInOpenJobs("SQL");
        sqlInOpen.forEach(System.out::println);
    }
}
