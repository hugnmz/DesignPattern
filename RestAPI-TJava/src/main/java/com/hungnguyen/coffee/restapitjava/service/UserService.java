package com.hungnguyen.coffee.restapitjava.service;

import com.hungnguyen.coffee.restapitjava.dto.request.UserRequestDTO;
import com.hungnguyen.coffee.restapitjava.dto.response.PageResponse;
import com.hungnguyen.coffee.restapitjava.dto.response.UserDetailResponse;
import com.hungnguyen.coffee.restapitjava.model.User;
import jakarta.mail.MessagingException;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface UserService
{
    int addUser(UserRequestDTO userRequestDTO);

    long saveUser(UserRequestDTO request) throws MessagingException, UnsupportedEncodingException;
    long saveUser(User user);

    void updateUser(long userId, UserRequestDTO request);

    void changeStatus(long userId, UserStatus status);

    void deleteUser(long userId);

    UserDetailResponse getUser(long userId);

    PageResponse<?> getAllUsers(int pageNo, int pageSize, String sortBy);

     PageResponse<?> getAllUsersByMultipleColums(int pageNo, int pageSize, String... sorts);

    PageResponse<?> getUserWithSortByColumnAndSearch(int pageNo, int pageSize, String search, String sorts);

    PageResponse<?> advanceSearchByCriteria(int pageNo, int pageSize, String sortBy, String address,String... search);


    PageResponse<?> advanceSearchBySpecification(Pageable pageable, String[] user, String[] address);

    void confirmUser(int userId, String secretCode);

    UserDetailsService userDetailsService();
}
