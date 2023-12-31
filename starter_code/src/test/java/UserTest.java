import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.controllers.UserController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

import java.util.Optional;

public class UserTest {
    private UserController userController;
    private UserRepository userRepository;
    private CartRepository cartRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        cartRepository = mock(CartRepository.class);
        bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);
        userController = new UserController(userRepository, cartRepository, bCryptPasswordEncoder);
        setupUser();
    }

    private void setupUser() {
        User user = new User();
        Cart cart = new Cart();
        user.setId(0);
        user.setUsername("HaLH");
        user.setPassword("123456Aa!");
        user.setCart(cart);
        when(userRepository.findByUsername("HaLH")).thenReturn(user);
        when(userRepository.findById(0L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("user")).thenReturn(null);
    }

    @Test
    void testCreateUserValidRequest() {
        // Arrange
        when(bCryptPasswordEncoder.encode("123456Aa!")).thenReturn("password");
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("HaLH");
        createUserRequest.setPassword("123456Aa!");
        createUserRequest.setConfirmPassword("123456Aa!");
        final ResponseEntity<User> response = userController.createUser(createUserRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("HaLH", user.getUsername());
        assertEquals("password", user.getPassword());
    }

    @Test
    public void findUserByNameNotFound() {
        final ResponseEntity<User> response = userController.findByUserName("username");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void findUserByIdSuccess() {
        final ResponseEntity<User> response = userController.findById(0L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());;
    }

    @Test
    public void findUserByNameSuccess() {
        final ResponseEntity<User> response = userController.findByUserName("HaLH");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals("HaLH", user.getUsername());
    }

    @Test
    public void findUserByIdNotFound() {
        final ResponseEntity<User> response = userController.findById(1L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
