package com.hungnguyen.coffee.restapitjava.repository;

import com.hungnguyen.coffee.restapitjava.model.RedisToken;
import com.hungnguyen.coffee.restapitjava.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RedisTokenRepository extends CrudRepository<RedisToken, String> {


}
