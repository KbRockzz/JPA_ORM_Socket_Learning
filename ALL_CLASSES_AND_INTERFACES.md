# All Classes and Interfaces

## App

Source: `src/main/java/App.java` (class)

```java
public class App {
    public static void main(String[] args) {
        Persistence.createEntityManagerFactory("mariadb-pu");
        
    }
}
```

## CandidateDao

Source: `src/main/java/dao/CandidateDao.java` (interface)

```java
public interface CandidateDao extends GenericDao<Candidate, String> {
    List<Candidate> findBySkillInOpenJobs(String skill);
}
```

## CompanyDao

Source: `src/main/java/dao/CompanyDao.java` (interface)

```java
public interface CompanyDao extends GenericDao<Company, String> {
    Map<String, Long> countPerJobByCompany(String companyName);
}
```

## GenericDao

Source: `src/main/java/dao/GenericDao.java` (interface)

```java
public interface GenericDao <T, ID>{
    T create(T t);
    T update(T t);
    boolean delete(ID id);
    T findById(ID id);
    List<T> loadALl();
}
```

## AbstractGenericDaoImpl

Source: `src/main/java/dao/impl/AbstractGenericDaoImpl.java` (class)

```java
public abstract class AbstractGenericDaoImpl<T, ID> implements GenericDao<T, ID> {
    private Class<T> entityClass;

    public AbstractGenericDaoImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected <R> R doInTransaction(Function<EntityManager, R> function) {
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = JPAUtil.getEntityManager();
            tx = em.getTransaction();
            tx.begin();
            R result = function.apply(em);
            tx.commit();
            return result;
        }
        catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public T create(T t) {
        return doInTransaction(em -> {
            em.persist(t);
            return t;
        });
    }

    @Override
    public T update(T t) {
        return doInTransaction(em -> {
            return em.merge(t);
        });
    }

    @Override
    public boolean delete(ID id) {
        return doInTransaction(em -> {
            T t = em.find(entityClass, id);
            em.remove(t);
            return true;
        });
    }

    @Override
    public T findById(ID id) {
        return doInTransaction(em -> {
            return em.find(entityClass, id);
        });
    }

    @Override
    public List<T> loadALl() {
        String query = "FROM " + entityClass.getSimpleName();
        return doInTransaction(em -> {
            return em.createQuery(query, entityClass).getResultList();
        });
    }
}
```

## CandidateDaoImpl

Source: `src/main/java/dao/impl/CandidateDaoImpl.java` (class)

```java
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
        all.forEach(c -> System.out.println(c.getName()));

        // --- findById ---
        System.out.println("\n=== findById(C1) ===");
        Candidate c1 = dao.findById("C1");
        System.out.println(c1.getName());

        // --- create ---
        System.out.println("\n=== create (C99) ===");
        Candidate newCandidate = Candidate.builder()
                .id("C99")
                .name("Test User")
                .email("test.user@email.com")
                .experience(1)
                .build();
        dao.create(newCandidate);
        System.out.println("Created: " + dao.findById("C99").getName());

        // --- update ---
        System.out.println("\n=== update (C99 experience -> 5) ===");
        newCandidate.setExperience(5);
        Candidate updated = dao.update(newCandidate);
        System.out.println("Updated: " + updated.getName());

        // --- delete ---
        System.out.println("\n=== delete (C99) ===");
        boolean deleted = dao.delete("C99");
        System.out.println("Deleted: " + deleted);
        Candidate afterDelete = dao.findById("C99");
        System.out.println("After delete findById(C99): " + (afterDelete != null ? afterDelete.getName() : null));

        // --- findBySkillInOpenJobs ---
        System.out.println("\n=== findBySkillInOpenJobs(Java) ===");
        List<Candidate> javaInOpen = dao.findBySkillInOpenJobs("Java");
        javaInOpen.forEach(c -> System.out.println(c.getName()));

        System.out.println("\n=== findBySkillInOpenJobs(Python) ===");
        List<Candidate> pythonInOpen = dao.findBySkillInOpenJobs("Python");
        pythonInOpen.forEach(c -> System.out.println(c.getName()));

        System.out.println("\n=== findBySkillInOpenJobs(SQL) ===");
        List<Candidate> sqlInOpen = dao.findBySkillInOpenJobs("SQL");
        sqlInOpen.forEach(c -> System.out.println(c.getName()));
    }
}
```

## CompanyDaoImpl

Source: `src/main/java/dao/impl/CompanyDaoImpl.java` (class)

```java
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

        String companyName = company.getName();
        System.out.println("\n=== countPerJobByCompany(" + companyName + ") ===");
        Map<String, Long> stats = companyDao.countPerJobByCompany(companyName);
        stats.forEach((title, count) -> System.out.println(title + ": " + count));
    }
}
```

## JPAUtil

Source: `src/main/java/db/JPAUtil.java` (class)

```java
public class JPAUtil {
    private static final String PERSISTENCE_UNIT_NAME = "mariadb-pu";
    private static EntityManagerFactory emf;
    private static EntityManager entityManager;

    static {
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static void close() {
        if (emf != null) {
            emf.close();
        }
    }
}
```

## CompanyDto

Source: `src/main/java/dto/CompanyDto.java` (class)

```java
public class CompanyDto {
    private String id;
    private String name;
    private String industry;
}
```

## Application

Source: `src/main/java/entity/Application.java` (class)

```java
public class Application {
    @Id
    @Column(name = "candidate_id")
    private String candidateId;

    @Id
    @Column(name = "job_id")
    private String jobId;

    @Column(name = "appliedDate")
    private LocalDate appliedDate;

    @Enumerated(EnumType.STRING)
    private AppStatus status;

    @ManyToOne
    @JoinColumn(name = "candidate_id", insertable = false, updatable = false)
    private Candidate candidate;

    @ManyToOne
    @JoinColumn(name = "job_id", insertable = false, updatable = false)
    private Job job;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @EqualsAndHashCode
    public static class ApplicationPK implements Serializable {
        private String candidateId;
        private String jobId;
    }
}
```

## ApplicationPK

Source: `src/main/java/entity/Application.java` (class)

```java
    public static class ApplicationPK implements Serializable {
        private String candidateId;
        private String jobId;
    }
```

## Candidate

Source: `src/main/java/entity/Candidate.java` (class)

```java
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
```

## Company

Source: `src/main/java/entity/Company.java` (class)

```java
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
```

## Job

Source: `src/main/java/entity/Job.java` (class)

```java
public class Job {
    @Id
    @Column(name = "job_id")
    private String id;
    private String title;
    private String description;
    private Double salary;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @ManyToOne
    @JoinColumn(name = "company_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Company company;

    @ManyToMany
    @JoinTable(name = "jobs_skills",
            joinColumns = @JoinColumn(name = "job_id"),
                inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills;

    @OneToMany(mappedBy = "job")
    private Set<Application> applications;
}
```

## Skill

Source: `src/main/java/entity/Skill.java` (class)

```java
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
```

## CompanyService

Source: `src/main/java/service/CompanyService.java` (interface)

```java
public interface CompanyService {
    CompanyDto create(CompanyDto companyDto);
    CompanyDto update(CompanyDto companyDto);
    boolean delete(CompanyDto companyDto);
    CompanyDto findById(String companyId);
    List<CompanyDto> loadAll();
}
```

## Mapper

Source: `src/main/java/service/Mapper.java` (class)

```java
public class Mapper {
    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <S, T> T map(S source, Class<T> targetClass) {
        return objectMapper.convertValue(source, targetClass);
    }
}
```

## CompanyServiceImpl

Source: `src/main/java/service/impl/CompanyServiceImpl.java` (class)

```java
public class CompanyServiceImpl implements CompanyService {
    private CompanyDao companyDao;

    public CompanyServiceImpl() {
        this.   companyDao = new CompanyDaoImpl(Company.class);
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
            System.out.println(companyDto.getName());
        }
    }
}
```
