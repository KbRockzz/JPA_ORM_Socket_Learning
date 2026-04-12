package dao;

import entity.Company;

import java.util.Map;

public interface CompanyDao extends GenericDao<Company, String> {
    Map<String, Long> countPerJobByCompany(String companyName);
}
