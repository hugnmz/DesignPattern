package com.hungnguyen.coffee.restapitjava.service.impl;

import com.hungnguyen.coffee.restapitjava.dto.request.UserRequestDTO;
import com.hungnguyen.coffee.restapitjava.dto.response.PageResponse;
import com.hungnguyen.coffee.restapitjava.dto.response.UserDetailResponse;
import com.hungnguyen.coffee.restapitjava.exception.ResourceNotFoundException;
import com.hungnguyen.coffee.restapitjava.model.Address;
import com.hungnguyen.coffee.restapitjava.model.User;
import com.hungnguyen.coffee.restapitjava.repository.SearchRepositoy;
import com.hungnguyen.coffee.restapitjava.repository.UserRepository;
import com.hungnguyen.coffee.restapitjava.repository.specification.UserSpec;
import com.hungnguyen.coffee.restapitjava.repository.specification.UserSpecification;
import com.hungnguyen.coffee.restapitjava.repository.specification.UserSpecificationBuilder;
import com.hungnguyen.coffee.restapitjava.service.MailService;
import com.hungnguyen.coffee.restapitjava.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.usertype.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final SearchRepositoy  searchRepositoy;

    private final MailService mailService;

    private final KafkaTemplate<String, String> kafkaTemplate;
    @Override
    public int addUser(UserRequestDTO userRequestDTO) {
        System.out.println("addUser");
        if(userRequestDTO.getFirstName().equals("hung")){
            throw new ResourceNotFoundException("Ten nay ko ton tai");
        }
        return 0;
    }

    @Override
    public long saveUser(UserRequestDTO request) throws MessagingException, UnsupportedEncodingException {
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDob())

                .phone(request.getPhone())
                .email(request.getEmail())
                .addresses(convertToAddress(request.getAddresses()))
                .build();

        request.getAddresses().forEach(a ->
                user.saveAddress(Address.builder()
                        .floor(a.getFloor())
                        .building(a.getBuilding())
                        .streetNumber(a.getStreetNumber())
                        .street(a.getStreet())
                        .city(a.getCity())
                        .country(a.getCountry())
                        .addressType(a.getAddressType())
                        .build())
        );

        userRepository.save(user);

        if(user.getId() != null){

            String message= String.format("email=$s, id%s,code=%s",user.getEmail(),user.getId(), "123");
            kafkaTemplate.send("confirm-account-topic",message);

        }
        log.info("User save successful");
        return user.getId();
    }

    @Override
    public long saveUser(User user)  {
        userRepository.save(user);
        return user.getId();
    }

    @Override
    public void updateUser(long userId, UserRequestDTO request) {
        User user = getUserById(userId);

        user.setFirstName(request.getFirstName());
        //update o day

        log.info("Update User successful"); //chay trên hệ thông, lưu vào file. sout chỉ chạy trên máy cta
    }

    @Override
    public void changeStatus(long userId, UserStatus status) {

    }

    @Override
    public void deleteUser(long userId) {

    }

    @Override
    public UserDetailResponse getUser(long userId) {

        User user = getUserById(userId);
        return UserDetailResponse.builder().firstName(user.getFirstName()).lastName(user.getLastName()).phone(user.getPhone()).email(user.getEmail()).build();

    }

    @Override
    public List<UserDetailResponse> getAllUsers(int pageNo, int pageSize, String sortBy) {
        if(pageNo > 0){
            pageNo = pageNo - 1;
        }

        List<Sort.Order> sorts = new ArrayList<>();

        // neu co gia tri
        //firstname:asc||desc
        if(StringUtils.hasLength(sortBy)){
            Pattern patter = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = patter.matcher(sortBy);

            if(matcher.find()){
                if(matcher.group(3).equalsIgnoreCase("asc")){
                    sorts.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                }else{
                    sorts.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                }
            }
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sorts));

        Page<User> all = userRepository.findAll(pageable);


        return all.stream().map(user -> UserDetailResponse.builder().firstName(user.getFirstName()).lastName(user.getLastName()).phone(user.getPhone()).build()).toList();
    }

    @Override
    public PageResponse<?> getAllUsersByMultipleColums(int pageNo, int pageSize, String... sorts) {
        if(pageNo > 0){
            pageNo = pageNo - 1;
        }

        List<Sort.Order> orders = new ArrayList<>();

        // neu co gia tri
        //firstname:asc||desc
        for(String s: sorts){
            Pattern patter = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = patter.matcher(s);

            if(matcher.find()){
                if(matcher.group(3).equalsIgnoreCase("asc")){
                    orders.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                }else{
                    orders.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                }
            }
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sorts));

        Page<User> all = userRepository.findAll(pageable);
        List<UserDetailResponse> response =
                all.stream().map(user -> UserDetailResponse.builder().firstName(user.getFirstName()).lastName(user.getLastName()).phone(user.getPhone()).build()).toList();
        return PageResponse.builder().pageNo(pageNo).pageSize(pageSize).totalPages(all.getTotalPages()).items(response).build();

    }

    @Override
    public PageResponse<?> getUserWithSortByColumnAndSearch(int pageNo, int pageSize, String search, String sortBy) {
        return searchRepositoy.getAllUsersWithSortByColumnAndSearch(pageNo, pageSize, search,sortBy);
    }

    @Override
    public PageResponse<?> advanceSearchByCriteria(int pageNo, int pageSize, String sortBy, String address,
                                                   String... search) {
        return searchRepositoy.advanceSearchUser(pageNo, pageSize, sortBy, address,search);
    }

    @Override
    public PageResponse<?> advanceSearchBySpecification(Pageable pageable, String[] user, String[] address) {

        Page<User> users = null;
        List<User> list= new ArrayList<>();
        if(user !=null && address != null){
            // tim kiem tren ca bang user va address --> join
        list = searchRepositoy.getUserJoinedAddress(pageable, user, address);



        }else if(user !=null && address == null){
            // chi tim kiem tren user

//            Specification<User> spec =
//                    UserSpec.hasFirstName("T")
//
//            Specification<User> genderSpec =
//                    UserSpec.notEqualGender("gender");
//
//            Specification<User> finalSpec = spec.and(genderSpec);

            UserSpecificationBuilder builder = new UserSpecificationBuilder();

            for(String s : user){
                Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)(\\p{Punct})");

                Matcher matcher = pattern.matcher(s);

                if(matcher.find()){
                    builder.with(matcher.group(1), matcher.group(2),matcher.group(3), matcher.group(4),
                            matcher.group(5) );
                }
            }

            list = userRepository.findAll(builder.build());
            return PageResponse.builder()
                    .pageNo(pageable.getPageNumber())
                    .pageSize(pageable.getPageSize())
                    .totalPages(users.getTotalPages())
                    .items(list)
                    .build();

        }else{
            users = userRepository.findAll(pageable);
        }

        return PageResponse.builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPages(users.getTotalPages())
                .items(list)
                .build();
    }

    @Override
    public void confirmUser(int userId, String secretCode) {

        log.info("done confirmed user");

    }

    @Override
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username " + username + " not found"));
    }

    private Set<Address> convertToAddress(Set<Address> addresses) {
        Set<Address> result = new HashSet<>();
        addresses.forEach(a ->
                result.add(Address.builder()
                        .floor(a.getFloor())
                        .building(a.getBuilding())
                        .streetNumber(a.getStreetNumber())
                        .street(a.getStreet())
                        .city(a.getCity())
                        .country(a.getCountry())
                        .addressType(a.getAddressType())
                        .build())
        );
        return result;
    }

    private User getUserById(long userId) {
        return userRepository.findById( userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
