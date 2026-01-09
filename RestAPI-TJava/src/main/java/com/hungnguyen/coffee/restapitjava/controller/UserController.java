package com.hungnguyen.coffee.restapitjava.controller;

import com.hungnguyen.coffee.restapitjava.dto.request.UserRequestDTO;
import com.hungnguyen.coffee.restapitjava.dto.response.ResponseData;
import com.hungnguyen.coffee.restapitjava.dto.response.ResponseError;
import com.hungnguyen.coffee.restapitjava.dto.response.ResponseSuccess;
import com.hungnguyen.coffee.restapitjava.dto.response.UserDetailResponse;
import com.hungnguyen.coffee.restapitjava.exception.ResourceNotFoundException;
import com.hungnguyen.coffee.restapitjava.service.UserService;
import com.sun.java.accessibility.util.Translator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.juli.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor // tạo ra ctor có tham số để inject
@Validated
@Slf4j
@Tag(name = "User Controller")
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService service;

    @Autowired
    private UserService userService;

    //mô tả api cách 1
    @Operation(method = "POST", summary = "Add new user", description = "Send a request via this API to create new user")
    @PostMapping(value = "/")
    public ResponseData<Long> addUser(@Valid @RequestBody UserRequestDTO user) {
         log.info("Request add user, {} {}", user.getFirstName(), user.getLastName());


        try {
            long userId = userService.saveUser(user);
            return new ResponseData<>(HttpStatus.CREATED,
                    "OK", userId);
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST, "Add user fail");
        }
    }


    // chuẩn
    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.ACCEPTED) //202
    public ResponseData<Integer> updateUser(@RequestBody UserRequestDTO userRequestDTO, @PathVariable int userId){
        System.out.println("Request update userId=" + userId);

        try {
            userService.addUser(userRequestDTO);

            return new ResponseData(HttpStatus.ACCEPTED, "User Updated Successfully", 1);
        } catch (ResourceNotFoundException e) {
            return new ResponseError(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        // neu loi
        //return new ResponseError(HttpStatus.BAD_REQUEST, "errored");


        // neu ko loi

        //return new ResponseData<>(HttpStatus.ACCEPTED,"added", 1);
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String patchUser(@RequestParam(required = false) boolean status// phải nhập giá trị cho cái trường này, có tể thể
                            // require là
                            // false để có th nhập hay ko
            , @PathVariable int userId){
        return "User patched";
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String deleteUser(@PathVariable int userId){
        return "User deleted";
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserRequestDTO getUser(@PathVariable int userId){
        System.out.println("Request Get userId=" + userId);
        return new UserRequestDTO("hung","java","j91","ala");
    }

    @Operation(summary = "Get lisst", description = "Send a request ...")
    @GetMapping("/list")
    public ResponseData<List<UserDetailResponse>> getUserList(@RequestParam(defaultValue = "0") int pageNo,
                                                 @RequestParam(defaultValue =
            "10") int pageSize, @RequestParam(required = false) String sortBy){
        System.out.println("Request Get userList");
        return new ResponseData<>(HttpStatus.OK,"users", userService.getAllUsers(pageNo, pageSize, sortBy));
    }

    @Operation(summary = "Get lisst sort and search", description = "Send a request ...")
    @GetMapping("/list")
    public ResponseData<List<UserDetailResponse>> getUserWithSortByColumnAndSearch(@RequestParam(defaultValue = "0") int pageNo,
                                                              @RequestParam(defaultValue =
                                                                      "20") int pageSize,
                                                                                   @RequestParam(defaultValue = "20",
                                                                                           required = false) String search,
                                                                                       @RequestParam(required =
                    false) String sortBy){
        log.info("Request Get userList");
        return new ResponseData<>(HttpStatus.OK,"users", userService.getUserWithSortByColumnAndSearch(pageNo,
                pageSize, search,sortBy));
    }

    @Operation(summary = "Get lisst sort and search", description = "Send a request ...")
    @GetMapping("/list")
    public ResponseData<?> advanceSearchByCriteria(@RequestParam(defaultValue = "0") int pageNo,
                                                   @RequestParam(defaultValue = "20") int pageSize, @RequestParam(defaultValue = "20", required = false) String sortBy,
      @RequestParam(required = false) String address,
                                                   @RequestParam(required =
              false) String... search){
        log.info("Criteria");
        return new ResponseData<>(HttpStatus.OK,"users", userService.advanceSearchByCriteria(pageNo,
                pageSize, sortBy, address,search));
    }

    @Operation(summary = "Get lisst sort and search", description = "Send a request ...")
    @GetMapping("/list")
    public ResponseData<?> advanceSearchBySpecification(Pageable pageable,
                                                        @RequestParam(required = false) String[] user,
                                                        @RequestParam(required = false) String[] address){
        log.info("Criteria");
        return new ResponseData<>(HttpStatus.OK,"users", userService.advanceSearchBySpecification(pageable, user,
                address);
    }

    @GetMapping("/confirm/{userId}")
    public  ResponseData<?> confirmUser(@PathVariable int userId, @RequestParam String secretCode, HttpServletResponse httpServletResponse) throws IOException {
        log.info("Confirm User - api");

        try{
            userService.confirmUser(userId, secretCode);

            return new ResponseData(HttpStatus.OK, "User Confirmed Successfully");
        }catch (Exception e){
            return new ResponseError(HttpStatus.BAD_REQUEST,"Confirm failure");
        }finally {
            // TODO: confirm thanh cong direct to login page...
            httpServletResponse.sendRedirect("http://tayjava.vn");
        }
    }
}


/*
- @Valid: kích hoạt cơ chế  sử dụng validation
- @RestControllerAdvice: @ControllerAdvice + @ResponseBody: nơi bắt và xử lí exception và logic chung cho toàn bộ REST
 API
 -> bắt exception tập trung, ko phải try-catch trpong từng controller

- @ExceptionHandler: dùng để bắt và xử lí exception trong SpringMVC. Có thể bắt 1 hay nhiều exception, xử lí lỗi thay
 vì để spring trả loi 500, 400... -> khi controller ném ra lỗi, spring tìm method có @ExceptionHandler phù hợp để xử lí
 -@ResponseStatus: Dùng để gán HTTPStatus cho response khi method chạy xong hay khi spring ném ra exception.
 -@Validated: @Valid + Group validation: tức cùng 1 dto nhưng có thể validate khác nhau theo từng ngữ cảnh  hay nói
 cách khác rule này chỉ dc ktra cho cái groups/interface này - ngoaài ra còn dùng dc cả ở class/method/parameter.

 -ResponseEntity<T>: là lớp wrapper bao ngoài cho response HTTP, giúp ta kiểm soát toàn bộ: HTTP header, http status,
  body
 */
