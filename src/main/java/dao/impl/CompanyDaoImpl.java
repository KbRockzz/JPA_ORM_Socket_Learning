package dao.impl;

import dao.CompanyDao;
import entity.Company;

public class CompanyDaoImpl extends AbstractGenericDaoImpl<Company, String> implements CompanyDao{
    public CompanyDaoImpl(Class<Company> entityClass) {
        super(entityClass);
    }

    public static void main(String[] args) {
        CompanyDao companyDao = new CompanyDaoImpl(Company.class);

        Company company = companyDao.findById("CP6");
        System.out.println(company);
    }
}
