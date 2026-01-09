package com.hungnguyen.coffee.restapitjava.repository;

import com.hungnguyen.coffee.restapitjava.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long>, JpaSpecificationExecutor<User> {

    @Query(value = "select * from User u inner join Address a on u.id = a.user_Id where a.city like %H%") // dang query
        // tren
        // jpa
    List<User> getAllUser();

    //Distinct
    //@Query(value = "select distinct from User u where u.firstName = :firstName and u.lastName = :lastName")
    Page<User>  findDistinctByFirstNameAndLastName(String firstName, String lastName); // tra ve se phan trang. ko
    // phan trang thi dungf list

    // @Query(value = "select * from User u where u.email = ?1")
    List<User> findByEmail(String email);

    //@Query(value = "select * from User u where u.firstName = :firstName or u.lastName = :lastName")
    List<User> findByFirstNameOrLastName(String firstName, String lastName);

    //Is, Equals
    //@Query(value = "select * from User u where u.firstName = :firstName")
    List<User> findByFirstNameIs(String firstName);
    List<User> findByFirstNameEquals(String lastName);
    List<User> findByFirstName(String firstName);

    // 3 cach nay nhu nhau

    // Between
    //@Query(value = "select * from User  u where u.createdAt between ?1 and ?2")
    List<User> findByCreatedAtBetween(Date start, Date end);

    //less than
    //@Query(value = "select * from User u where u.age < :age ")
    List<User> findByAgeLessThan(Integer age);

    //Before and after
    //@Query(value = "select * from User u where u.createdAt < :start ")
    List<User> findByCreatedAtBefore(Date start);

    // IsNull, Null
    //@Query(value = "select * from User u where u.age is null ")
    List<User> findByAgeIsNull(Integer age);

    // like
    //@Query(value = "select * from User u where u.firstName like %:firstName%")
    List<User> findByFirstNameLike(String firstName);

    //Starting with
    //@Query(value = "select * from User u where u.lastName like :lastName%")
    List<User> findByLastNameStartingWith(String lastName);

    //Ending with
    //@Query(value = "select * from User u where u.lastName like %:lastName")
    List<User> findByLastNameEndingWith(String lastName);

    // containing tunong tu like
    //@Query(value = "select * from User u where u.lastName not like %:lastName% ")
    List<User> findByLastNameContaining(String lastName);

    // not
    //@Query(value = "select * from User u where u.lastName <> :lastName")
    List<User> findByLastNameNot(String lastName);

    // in - not in
    //@Query(value = "select * from User u where u.age in (18, 25, 30)")
    List<User> findByAgeIn(Collection<Integer> ages);

    //@Query(value = "select * from User u where u.activated = true")
    List<User> findByActivatedTrue();

    //IgnoreCase
    //orderby

    UserDetails findByUsername(String username);

    User getUserByEmail(String email);
}
