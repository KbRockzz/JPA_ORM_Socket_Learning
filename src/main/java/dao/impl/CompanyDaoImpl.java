package dao.impl;

import dao.CompanyDao;
import entity.Company;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CompanyDaoImpl extends AbstractGenericDaoImpl<Company, String> implements CompanyDao {
    public CompanyDaoImpl(Class<Company> entityClass) {
        super(entityClass);
    }

    @Override
    public Map<String, Long> countPerJobByCompany(String companyName) {
        String jpql = "SELECT j.title, COUNT(a) FROM Job j " +
                      "LEFT JOIN j.applications a " +
                      "JOIN j.company c " +
                      "WHERE c.name = :companyName " +
                      "GROUP BY j.id, j.title " +
                      "ORDER BY COUNT(a) DESC";
        return doInTransaction(em -> {
            List<Object[]> rows = em.createQuery(jpql, Object[].class)
                    .setParameter("companyName", companyName)
                    .getResultList();
            Map<String, Long> result = new LinkedHashMap<>();
            for (Object[] row : rows) {
                result.put((String) row[0], (Long) row[1]);
            }
            return result;
        });
    }

    public static void main(String[] args) {
        CompanyDao companyDao = new CompanyDaoImpl(Company.class);

        Company company = companyDao.findById("CP6");
        System.out.println(company);

        System.out.println("\n=== countPerJobByCompany(TechCorp) ===");
        Map<String, Long> stats = companyDao.countPerJobByCompany("TechCorp");
        stats.forEach((title, count) -> System.out.println(title + ": " + count));
    }
}
