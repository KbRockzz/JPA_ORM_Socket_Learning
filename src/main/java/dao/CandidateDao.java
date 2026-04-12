package dao;

import entity.Candidate;

import java.util.List;

public interface CandidateDao extends GenericDao<Candidate, String> {
    List<Candidate> findBySkillInOpenJobs(String skill);
}
