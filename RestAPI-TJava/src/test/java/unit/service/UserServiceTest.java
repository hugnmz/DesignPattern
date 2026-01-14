package unit.service;

import com.hungnguyen.coffee.restapitjava.model.User;
import com.hungnguyen.coffee.restapitjava.repository.UserRepository;
import com.hungnguyen.coffee.restapitjava.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.time;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


/*
- when(...).thenReturn(...) dung mo phong hanh vi cua mock object(dc danh dau boi @Mock) - Kiem tra cai gi: Mock data
 tra ve

 --thenAnswer: thuc hien 1 hanh dong tuy chinh nhu tu dong gan id cho object User
- assertEquals(expected, actual) : dung de kiem tra ket qua thuc te co dung nhu mong doi ko - ktra cai gi: Locgic
dung/sai
- verify(mock, times(n)): dung de ktra xem mock co dc goi dung cach hay ko - ktra cai gi: Hanh vi
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User mockUser;


    @BeforeEach // chạy trước mooix ca test
    void setUp(){
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFirstName("Hola");
        mockUser.setLastName("FPT");
        mockUser.setAge(20);
        mockUser.setEmail("hung@gmail.com");
    }

    @Test // danh dau ham nay la 1 testcase
    void testGetAllUser_ShouldReturnList(){

        // mô phỏng hành vi (Given phase)
        when(userRepository.findAll()).thenReturn(List.of(mockUser));

        // sau khi mô phỏng xong -> gọi hàm thật
        List<User> users = userService.getAllUsers(1,2,..);

        //sau khi gọi ha thật -> ktra kết quả thực tế có đúng vs mong đợi hay ko -> THEN PHASE
        assertEquals(1, users.size());
        assertEquals("Hola", users.get(0).getFirstName());

        // xem mock dc goi bao nhieu lan, co dung vs mong doi hay k
        verify(userRepository, times(1)).findAll();


    }
}
