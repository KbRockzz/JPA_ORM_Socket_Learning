package service.impl;

import dao.CompanyDao;
import dao.impl.CompanyDaoImpl;
import dto.CompanyDto;
import entity.Company;
import service.CompanyService;
import service.Mapper;

import java.util.List;

public class CompanyServiceImpl implements CompanyService {
    private CompanyDao companyDao;

    public CompanyServiceImpl(CompanyDao companyDao) {
        this.companyDao = new CompanyDaoImpl(Company.class);
    }

    @Override
    public CompanyDto create(CompanyDto companyDto) {
        Company company = Mapper.map(companyDto, Company.class);
        company = companyDao.create(company);
        return Mapper.map(company, CompanyDto.class);
    }

    @Override
    public CompanyDto update(CompanyDto companyDto) {
        Company company = Mapper.map(companyDto, Company.class);
        company = companyDao.update(company);
        return Mapper.map(company, CompanyDto.class);
    }

    @Override
    public boolean delete(CompanyDto companyDto) {
        Company company = Mapper.map(companyDto, Company.class);
        companyDao.delete(company.getId());
        return true;
    }

    @Override
    public CompanyDto findById(String companyId) {
        Company company = companyDao.findById(companyId);
        return Mapper.map(company, CompanyDto.class);
    }

    @Override
    public List<CompanyDto> loadAll() {
        List<Company> companies = companyDao.loadALl();
        return companies
                .stream()
                .map(company -> Mapper.map(company, CompanyDto.class))
                .toList();
    }

    public static void main(String[] args) {
        CompanyService companyService = new CompanyServiceImpl();
        List<CompanyDto> companyDtos = companyService.loadAll();
        for (CompanyDto companyDto : companyDtos) {
            System.out.println(companyDto);
        }
    }
}
