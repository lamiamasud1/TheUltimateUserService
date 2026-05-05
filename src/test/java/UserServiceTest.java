import com.example.theultimateuser.dto.UserDTO;
import com.example.theultimateuser.service.ImmutableFieldUpdateException;
import com.example.theultimateuser.service.InvalidSearchCriteriaException;
import com.example.theultimateuser.service.UserNotFoundException;
import com.example.theultimateuser.repository.CsvUserRepository;
import com.example.theultimateuser.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private CsvUserRepository csvUserRepository;

    @InjectMocks
    private UserService userService;

    private UserDTO sampleUser;

    @BeforeEach
    void setup() {
        sampleUser = new UserDTO(110L, "Lamia", "Masud",
                "lamiarob95@gmail.com", "Software Engineer", LocalDate.of(2026, 05, 12), "USA", "Riverview");
    }

    @Test
    void shouldReturnUser_WhenIdIsFound() {
        when(csvUserRepository.readAllUsers()).thenReturn(List.of(sampleUser));
        UserDTO result = userService.findUserInfoById(110L);
        assertEquals(110L, result.id());
        assertEquals("Lamia", result.firstname());
    }

    @Test
    void shouldThrowNotFound_WhenIdIsNotFound() {
        when(csvUserRepository.readAllUsers()).thenReturn(List.of(sampleUser));
        assertThrows(UserNotFoundException.class, () -> userService.findUserInfoById(1L));
    }

    @Test
    void shouldReturnUsers_WhenDateRangeIsValid() {
        UserDTO nurseUser = new UserDTO(111L, "Sally", "Larson", "sallylarson@gmail.com", "nurse",
                LocalDate.of(2027, 10, 26), "USA", "Tampa");
        when(csvUserRepository.readAllUsers()).thenReturn(List.of(sampleUser, nurseUser));

        List<UserDTO> results = userService.findUserInfoInDateRange(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31)
        );

        assertEquals(1, results.size(), "Should find user in range");
    }

    @Test
    void shouldThrowError_WhenDateRangeIsInvalid() {
        LocalDate start = LocalDate.of(2025, 12, 1);
        LocalDate end = LocalDate.of(2024, 10, 27);
        assertThrows(InvalidSearchCriteriaException.class, () -> userService.findUserInfoInDateRange(start,end));
    }

    @Test
    void shouldReturnUserInfo_WithSearchCriteria() {
        UserDTO nurseUser = new UserDTO(111L, "Sally", "Larson", "sallylarson@gmail.com", "nurse",
                LocalDate.of(2025, 10, 26), "USA", "Tampa");
        when(csvUserRepository.readAllUsers()).thenReturn(List.of(sampleUser, nurseUser));
        Map<String, String> searchCriteria =Map.of("profession", "software engineer");
        List<UserDTO> results = userService.fullTextSearch(searchCriteria);
        assertEquals(1, results.size(), "1 software engineer found");
        assertEquals("Software Engineer", results.get(0).profession());
        assertEquals("Lamia", results.get(0).firstname());
        assertFalse
                (results.stream().anyMatch(user -> user.lastname().equals("Larson")));
    }

    @Test
    void shouldThrowError_WithInvalidSearchCriteria() {
        Map<String, String> invalidSearchCriteria =Map.of("address", "1234 Westchase Road");
        assertThrows(InvalidSearchCriteriaException.class, () -> userService.fullTextSearch(invalidSearchCriteria));
    }


    @Test
    void shouldUpdateUserInfo_WithValidFields() {
        Long userId = 110L;
       when(csvUserRepository.readAllUsers()).thenReturn(new ArrayList<>(List.of(sampleUser)));
       Map<String, String> updates = Map.of("profession", "product manager");
       UserDTO results = userService.updateMultipleUserSearchFields(userId, updates);
       assertEquals("product manager", results.profession());
       assertEquals("Lamia", results.firstname());
    }

    @Test
    void shouldThrowError_WhenUpdatingImmutableFields() {
        Long userId = 110L;
        Map<String, String> invalidUpdates = Map.of("id", "120");
        assertThrows(ImmutableFieldUpdateException.class, () ->
                userService.updateMultipleUserSearchFields(userId, invalidUpdates));
    }
}

