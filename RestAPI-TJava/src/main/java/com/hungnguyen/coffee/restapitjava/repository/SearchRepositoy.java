package com.hungnguyen.coffee.restapitjava.repository;

import com.hungnguyen.coffee.restapitjava.dto.response.PageResponse;
import com.hungnguyen.coffee.restapitjava.model.Address;
import com.hungnguyen.coffee.restapitjava.model.User;
import com.hungnguyen.coffee.restapitjava.repository.criteria.SearchCriteria;
import com.hungnguyen.coffee.restapitjava.repository.criteria.UserSearchCriteriaQueryConsumer;
import com.hungnguyen.coffee.restapitjava.repository.specification.SpecSearchCreteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hungnguyen.coffee.restapitjava.repository.specification.SearchOperation.*;

@Repository
public class SearchRepositoy {

    private final JsonMapper.Builder builder;
    @PersistenceContext // lam viec voi jpa va hibernate
    private EntityManager entityManager;

    public SearchRepositoy(JsonMapper.Builder builder) {
        this.builder = builder;
    }

    public PageResponse<?> getAllUsersWithSortByColumnAndSearch(int pageNo, int pageSize, String search, String sortBy) {

        StringBuilder sqlQuery = new StringBuilder("select new package com.hungnguyen.coffee.restapitjava.dto" +
                ".response.UserDetailresponse(u.id, u.firstName,  u.lastName, u.lastName, u.email) from User u where " +
                "1=1");
        if(StringUtils.hasLength(search)) {
            sqlQuery.append(" and lower(u.firstName) like lower(:firstName)"); //JQL
            sqlQuery.append(" or lower(u.lastName) like lower(:lastName)");
            sqlQuery.append(" or lower(u.email) like lower(:email)");
        }

        if(StringUtils.hasLength(sortBy)) {

            Pattern patter = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = patter.matcher(sortBy);

            if(matcher.find()){
                    sqlQuery.append(String.format(" order by u.%s %s", matcher.group(1), matcher.group(3)));
            }
        }

        Query selectQuery = entityManager.createQuery(sqlQuery.toString());

        selectQuery.setFirstResult(pageNo);
        selectQuery.setMaxResults(pageSize);
        if(StringUtils.hasLength(search)) {
            selectQuery.setParameter("firstName", "%" + search + "%");
            selectQuery.setParameter("lastName", "%" + search + "%");
            selectQuery.setParameter("email", "%" + search + "%");
        }

        List users = selectQuery.getResultList();
        //query ra list user

        //query so record
        StringBuilder sqlCountQuery = new StringBuilder("select count(*) from User u where 1=1");
        if(StringUtils.hasLength(search)) {
            sqlCountQuery.append(" and lower(u.firstName) like lower(?1)"); //JQL
            sqlCountQuery.append(" or lower(u.lastName) like lower(?2)");
            sqlCountQuery.append(" or lower(u.email) like lower(?3)");
        }

        Query selectCountQuery = entityManager.createQuery(sqlCountQuery.toString());

        if(StringUtils.hasLength(search)) {
            selectQuery.setParameter(1, "%" + search + "%");
            selectQuery.setParameter(2, "%" + search + "%");
            selectQuery.setParameter(3, "%" + search + "%");
        }
        Long totalElements=  (Long) selectCountQuery.getSingleResult();

        Page<?> page = new PageImpl<Object>(users, PageRequest.of(pageNo, pageSize), totalElements);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .items(page.stream().toList())
                .build();
    }

    public PageResponse advanceSearchUser(int pageNo, int pageSize, String sortBy, String address,String... search) {
        // firstName: T, lastName: T
        List<SearchCriteria> criteria = new ArrayList<>();

        // lay ra danh sach user
        if(search != null){
            for(String s : search){
                Pattern pattern = Pattern.compile("(\\w+?)(:|>|<)(x*)");
                Matcher matcher = pattern.matcher(s);
                if(matcher.find()){
                    criteria.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
                }
            }
        }

        // lay ra so luong ban ghi
        List<User> users = getUsers( pageNo,  pageSize, criteria, sortBy, address);

        Long totalElements = getTotalElements(criteria, address);
        return PageResponse.builder()
                .pageNo(pageNo) //offset = vi tri cua ban ghi torng danh sach
                .pageSize(pageSize)
                .totalPages(totalElements.intValue())
                .items(users)
                .build();

    }

    private Long getTotalElements(List<SearchCriteria> criteria, String address) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);

        Root<User> root = query.from(User.class);

        // xu li ca dieu kien
        Predicate predicate = criteriaBuilder.conjunction();
        UserSearchCriteriaQueryConsumer queryConsumer = new UserSearchCriteriaQueryConsumer(criteriaBuilder,
                predicate, root);

        if(StringUtils.hasLength(address)) {
            Join<Address, User> addressUserJoin = root.join("addresses");

            Predicate addressPredicate = criteriaBuilder.like(addressUserJoin.get("city"), "%" + address + "%");
            query.select(criteriaBuilder.count(root));
            query.where(predicate, addressPredicate);
        }else{
            criteria.forEach(queryConsumer);

            predicate = queryConsumer.getPredicate();
            query.select(criteriaBuilder.count(root));
            query.where(predicate);
        }

        return entityManager.createQuery(query).getSingleResult();

    }

    private List<User> getUsers(int pageNo, int pageSize, List<SearchCriteria> criteria, String sortBy,
                                String address) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);

        Root<User> root = query.from(User.class);

        // xu li ca dieu kien
        Predicate predicate = criteriaBuilder.conjunction();
        UserSearchCriteriaQueryConsumer queryConsumer = new UserSearchCriteriaQueryConsumer(criteriaBuilder,
                predicate, root);

        if(StringUtils.hasLength(address)) {
            Join<Address, User> addressUserJoin = root.join("addresses");

            Predicate addressPredicate = criteriaBuilder.like(addressUserJoin.get("city"), "%" + address + "%");

            query.where(predicate, addressPredicate);
        }else{
            criteria.forEach(queryConsumer);

            predicate = queryConsumer.getPredicate();

            query.where(predicate);
        }


        //sort
        if(StringUtils.hasLength(sortBy)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(asc|desc)");
            Matcher matcher = pattern.matcher(sortBy);
            if(matcher.find()){
                String columnName = matcher.group(1);
                if(matcher.group(3).equalsIgnoreCase("desc"))
                    query.orderBy(criteriaBuilder.desc(root.get(columnName)));
                else
                    query.orderBy(criteriaBuilder.asc(root.get(columnName)));
            }
        }

        return entityManager.createQuery(query).setFirstResult(pageNo).setMaxResults(pageSize).getResultList();
    }

    public PageResponse getUserJoinedAddress(Pageable pageable, String[] user, String[] address) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);

        Root<User> root = query.from(User.class);

        // xu li ca dieu kien
        Predicate predicate = criteriaBuilder.conjunction();
        UserSearchCriteriaQueryConsumer queryConsumer = new UserSearchCriteriaQueryConsumer(criteriaBuilder,
                predicate, root);

            Join<Address, User> addressUserJoin = root.join("addresses");

            Predicate addressPredicate = criteriaBuilder.like(addressUserJoin.get("city"), "%" + address + "%");

            query.where(predicate, addressPredicate);

            // build query
            List<Predicate> userPre = new ArrayList<>();
            List<Predicate> addressPre = new ArrayList<>();

            Pattern pattern = Pattern.compile("(\\w+?)(:)(asc|desc)");
            for(String u : user){
                Matcher matcher = pattern.matcher(u);
                if(matcher.find()){
                    SpecSearchCreteria  criteria = new SpecSearchCreteria(matcher.group(1),matcher.group(2),
                            matcher.group(3),matcher.group(4),matcher.group(5));
                    Predicate predicate1 = toUserPredicate(root, criteriaBuilder, criteria);

                    userPre.add(predicate1);
                }
            }

        for(String a : address){
            Matcher matcher = pattern.matcher(a);
            if(matcher.find()){
                SpecSearchCreteria  criteria = new SpecSearchCreteria(matcher.group(1),matcher.group(2),
                        matcher.group(3),matcher.group(4),matcher.group(5));
                Predicate predicate1 = toAddressPredicate(addressUserJoin, criteriaBuilder, criteria);

                userPre.add(predicate1);
            }
        }

        Predicate usePredicateArr = criteriaBuilder.or(userPre.toArray(new Predicate[0]));

        Predicate addressPredicateArr = criteriaBuilder.or(addressPre.toArray(new Predicate[0]));

        Predicate finalPredicate = criteriaBuilder.and(usePredicateArr, addressPredicateArr);
        query.where(finalPredicate);


        long count = count(user, address);
        return PageResponse.builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPages(1000)
                .items(entityManager.createQuery(query).getResultList())
                .build();
    }

    private long count(String[] user, String[] address){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);

        Root<User> root = query.from(User.class);

        // xu li ca dieu kien
        Predicate predicate = criteriaBuilder.conjunction();
        UserSearchCriteriaQueryConsumer queryConsumer = new UserSearchCriteriaQueryConsumer(criteriaBuilder,
                predicate, root);

        Join<Address, User> addressUserJoin = root.join("addresses");

        Predicate addressPredicate = criteriaBuilder.like(addressUserJoin.get("city"), "%" + address + "%");

        query.where(predicate, addressPredicate);

        // build query
        List<Predicate> userPre = new ArrayList<>();
        List<Predicate> addressPre = new ArrayList<>();

        Pattern pattern = Pattern.compile("(\\w+?)(:)(asc|desc)");
        for(String u : user){
            Matcher matcher = pattern.matcher(u);
            if(matcher.find()){
                SpecSearchCreteria  criteria = new SpecSearchCreteria(matcher.group(1),matcher.group(2),
                        matcher.group(3),matcher.group(4),matcher.group(5));
                Predicate predicate1 = toUserPredicate(root, criteriaBuilder, criteria);

                userPre.add(predicate1);
            }
        }

        for(String a : address){
            Matcher matcher = pattern.matcher(a);
            if(matcher.find()){
                SpecSearchCreteria  criteria = new SpecSearchCreteria(matcher.group(1),matcher.group(2),
                        matcher.group(3),matcher.group(4),matcher.group(5));
                Predicate predicate1 = toAddressPredicate(addressUserJoin, criteriaBuilder, criteria);

                userPre.add(predicate1);
            }
        }

        Predicate usePredicateArr = criteriaBuilder.or(userPre.toArray(new Predicate[0]));

        Predicate addressPredicateArr = criteriaBuilder.or(addressPre.toArray(new Predicate[0]));

        Predicate finalPredicate = criteriaBuilder.and(usePredicateArr, addressPredicateArr);

        query.select(criteriaBuilder.count(root));
        query.where(finalPredicate);

        return entityManager.createQuery(query).getSingleResult();
    }

    public Predicate toUserPredicate(@NotNull Root<User> root, @NotNull CriteriaBuilder builder, SpecSearchCreteria criteria) {
        return switch (criteria.getOperation()) {
            case EQUALITY -> builder.equal(root.get(criteria.getKey()), criteria.getValue());
            case NEGATION -> builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN -> builder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN -> builder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LIKE -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
            case STARTS_WITH -> builder.like(root.get(criteria.getKey()), criteria.getValue().toString() + "%");
            case ENDS_WITH -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString());
            case CONTAINS ->  builder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
        };
    }

    public Predicate toAddressPredicate(@NotNull Join<Address,User> root, @NotNull CriteriaBuilder builder,
                                 SpecSearchCreteria criteria) {
        return switch (criteria.getOperation()) {
            case EQUALITY -> builder.equal(root.get(criteria.getKey()), criteria.getValue());
            case NEGATION -> builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN -> builder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN -> builder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LIKE -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
            case STARTS_WITH -> builder.like(root.get(criteria.getKey()), criteria.getValue().toString() + "%");
            case ENDS_WITH -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString());
            case CONTAINS ->  builder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
        };
    }
}
