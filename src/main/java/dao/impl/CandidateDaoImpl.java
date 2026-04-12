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
}
