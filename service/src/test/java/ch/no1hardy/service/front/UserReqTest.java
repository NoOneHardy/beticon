package ch.no1hardy.service.front;

import ch.no1hardy.service.exception.user.UserValidationException;
import ch.no1hardy.service.front.user.UserReq;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserReqTest {
    @NotNull
    UserReq req = UserReq.builder().build();

    @BeforeEach
    void beforeEach() {
        req = UserReq.builder()
                .username("No1Hardy")
                .password("test1234")
                .email("test@no1hardy.ch")
                .firstname("Silas")
                .lastname("Hardegger")
                .build();
    }

    @Test
    @DisplayName("validate() - should validate user request with valid data")
    void shouldValidateUserRequestWithValidData() {
        assertDoesNotThrow(req::validate);
    }

    @Test
    @DisplayName("validate() - should validate user request with null values")
    void shouldValidateUserRequestWithNullValues() {
        req = req.toBuilder().username(null).build();
        req = req.toBuilder().email(null).build();
        req = req.toBuilder().firstname(null).build();
        req = req.toBuilder().lastname(null).build();
        req = req.toBuilder().password(null).build();

        assertDoesNotThrow(req::validate);
    }

    @Test
    @DisplayName("validate() - GIVEN an empty username | WHEN validating a dto | THEN a UserValidationException should be thrown")
    void validateEmptyUsername() {
        req = req.toBuilder().username("").build();

        var e = assertThrows(UserValidationException.class, () -> req.validate());
        assertEquals("username must be at least 5 characters long", e.getMessage());
    }

    @Test
    @DisplayName("validate() - GIVEN a short username | WHEN validating a dto | THEN a UserValidationException should be thrown")
    void validateShortUsername() {
        req = req.toBuilder().username("m").build();

        var e = assertThrows(UserValidationException.class, () -> req.validate());
        assertEquals("username must be at least 5 characters long", e.getMessage());
    }

    @Test
    @DisplayName("validate() - GIVEN a long username | WHEN validating a dto | THEN a UserValidationException should be thrown")
    void validateLongUsername() {
        req = req.toBuilder().username("aVeryVeryVeryLongUsernameThatWillFailValidation").build();

        var e = assertThrows(UserValidationException.class, () -> req.validate());
        assertEquals("username can only contain 25 characters", e.getMessage());
    }

    @Test
    @DisplayName("validate() - GIVEN an empty firstname | WHEN validating a dto | THEN a UserValidationException should be thrown")
    void validateEmptyFirstname() {
        req = req.toBuilder().firstname("").build();

        var e = assertThrows(UserValidationException.class, () -> req.validate());
        assertEquals("firstname must be at least 2 characters long", e.getMessage());
    }

    @Test
    @DisplayName("validate() - GIVEN a short firstname | WHEN validating a dto | THEN a UserValidationException should be thrown")
    void validateShortFirstname() {
        req = req.toBuilder().firstname("m").build();

        var e = assertThrows(UserValidationException.class, () -> req.validate());
        assertEquals("firstname must be at least 2 characters long", e.getMessage());
    }

    @Test
    @DisplayName("validate() - GIVEN a long firstname | WHEN validating a dto | THEN a UserValidationException should be thrown")
    void validateLongFirstname() {
        req = req.toBuilder().firstname("aVeryVeryVeryLongFirstnameThatWillFailValidation").build();

        var e = assertThrows(UserValidationException.class, () -> req.validate());
        assertEquals("firstname can only contain 25 characters", e.getMessage());
    }

    @Test
    @DisplayName("validate() - GIVEN an empty lastname | WHEN validating a dto | THEN a UserValidationException should be thrown")
    void validateEmptyLastname() {
        req = req.toBuilder().lastname("").build();

        var e = assertThrows(UserValidationException.class, () -> req.validate());
        assertEquals("lastname must be at least 2 characters long", e.getMessage());
    }

    @Test
    @DisplayName("validate() - GIVEN a short lastname | WHEN validating a dto | THEN a UserValidationException should be thrown")
    void validateShortLastname() {
        req = req.toBuilder().lastname("m").build();

        var e = assertThrows(UserValidationException.class, () -> req.validate());
        assertEquals("lastname must be at least 2 characters long", e.getMessage());
    }

    @Test
    @DisplayName("validate() - GIVEN a long lastname | WHEN validating a dto | THEN a UserValidationException should be thrown")
    void validateLongLastname() {
        req = req.toBuilder().lastname("aVeryVeryVeryLongLastnameThatWillFailValidation").build();

        var e = assertThrows(UserValidationException.class, () -> req.validate());
        assertEquals("lastname can only contain 25 characters", e.getMessage());
    }

    @Test
    @DisplayName("validate() - GIVEN an empty email | WHEN validating a dto | THEN a UserValidationException should be thrown")
    void validateEmptyEmail() {
        req = req.toBuilder().email("").build();

        var e = assertThrows(UserValidationException.class, () -> req.validate());
        assertEquals("email must be at least 5 characters long", e.getMessage());
    }

    @Test
    @DisplayName("validate() - GIVEN a short email | WHEN validating a dto | THEN a UserValidationException should be thrown")
    void validateShortEmail() {
        req = req.toBuilder().email("m").build();

        var e = assertThrows(UserValidationException.class, () -> req.validate());
        assertEquals("email must be at least 5 characters long", e.getMessage());
    }

    @Test
    @DisplayName("validate() - GIVEN a long email | WHEN validating a dto | THEN a UserValidationException should be thrown")
    void validateLongEmail() {
        req = req.toBuilder().email("aVeryVeryVeryLongEmailThatWillFailValidationButThisEmailIsEvenLongerBecauseItNeedsToBeLongerThan50Characters").build();

        var e = assertThrows(UserValidationException.class, () -> req.validate());
        assertEquals("email can only contain 50 characters", e.getMessage());
    }

    @Test
    @DisplayName("validate() - GIVEN an empty password | WHEN validating a dto | THEN a UserValidationException should be thrown")
    void validateEmptyPassword() {
        req = req.toBuilder().password("").build();

        var e = assertThrows(UserValidationException.class, () -> req.validate());
        assertEquals("password must be at least 8 characters long", e.getMessage());
    }

    @Test
    @DisplayName("validate() - GIVEN a short password | WHEN validating a dto | THEN a UserValidationException should be thrown")
    void validateShortPassword() {
        req = req.toBuilder().password("m").build();

        var e = assertThrows(UserValidationException.class, () -> req.validate());
        assertEquals("password must be at least 8 characters long", e.getMessage());
    }

    @Test
    @DisplayName("validate() - GIVEN a long password | WHEN validating a dto | THEN a UserValidationException should be thrown")
    void validateLongPassword() {
        req = req.toBuilder().password("aVeryVeryVeryLongPasswordThatWillFailValidationButThisPasswordIsEvenLongerBecauseItNeedsToBeLongerThan50Characters").build();

        var e = assertThrows(UserValidationException.class, () -> req.validate());
        assertEquals("password can only contain 50 characters", e.getMessage());
    }

    @Test
    @DisplayName("validate() - should validate email with invalid format")
    void shouldValidateEmailWithValidFormat() {
        req = req.toBuilder().email("invalid-email-format").build();
        assertThrows(UserValidationException.class, req::validate);

        req = req.toBuilder().email("test!@no1hardy-asdf1234.ch").build();
        assertThrows(UserValidationException.class, req::validate);

        req = req.toBuilder().email("test123@no1hardy!.ch").build();
        assertThrows(UserValidationException.class, req::validate);

        req = req.toBuilder().email("test_123@no1hardy.ch").build();
        assertDoesNotThrow(req::validate);

        req = req.toBuilder().email("test-123@no1hardy.ch").build();
        assertDoesNotThrow(req::validate);

        req = req.toBuilder().email("test.123@no1hardy.ch").build();
        assertDoesNotThrow(req::validate);

        req = req.toBuilder().email("test123@no1hardy.ch").build();
        assertDoesNotThrow(req::validate);

        req = req.toBuilder().email("test@no1hardy.c1h").build();
        assertDoesNotThrow(req::validate);
    }

    @Test
    @DisplayName("validateAll() - should validate all fields of user request")
    void shouldValidateAllFieldsOfUserRequest() {
        assertDoesNotThrow(req::validateAll);
    }

    @Test
    @DisplayName("validateAll() - should throw UserValidationException for missing fields")
    void shouldThrowUserValidationExceptionForMissingFields() {
        req = req.toBuilder().username(null).build();
        req = req.toBuilder().email(null).build();
        req = req.toBuilder().password(null).build();
        req = req.toBuilder().firstname(null).build();
        req = req.toBuilder().lastname(null).build();
        assertThrows(UserValidationException.class, req::validateAll);
    }

    @Test
    @DisplayName("validateAll() - should throw UserValidationException for empty fields")
    void shouldThrowUserValidationExceptionForEmptyFields() {
        req = req.toBuilder().username("").build();
        req = req.toBuilder().email("").build();
        req = req.toBuilder().password("").build();
        req = req.toBuilder().firstname("").build();
        req = req.toBuilder().lastname("").build();
        assertThrows(UserValidationException.class, req::validateAll);
    }

    @Test
    @DisplayName("validateAll() - should throw UserValidationException for invalid fields")
    void shouldThrowUserValidationExceptionForInvalidFields() {
        req = req.toBuilder().email("invalid-email-format").build();
        req = req.toBuilder().password("1234").build();
        assertThrows(UserValidationException.class, req::validateAll);
    }

    @Test
    @DisplayName("validateRequired() - should validate required fields")
    void shouldValidateRequiredFields() {
        assertDoesNotThrow(req::validateRequired);
    }

    @Test
    @DisplayName("validateRequired() - should throw UserValidationException for missing required fields")
    void shouldThrowUserValidationExceptionForMissingRequiredFields() {
        req = req.toBuilder().username(null).build();
        req = req.toBuilder().email(null).build();
        req = req.toBuilder().password(null).build();
        req = req.toBuilder().firstname(null).build();
        req = req.toBuilder().lastname(null).build();
        assertThrows(UserValidationException.class, req::validateRequired);
    }

    @Test
    @DisplayName("validateRequired() - should throw UserValidationException for empty required fields")
    void shouldThrowUserValidationExceptionForEmptyRequiredFields() {
        req = req.toBuilder().username("").build();
        req = req.toBuilder().email("").build();
        req = req.toBuilder().password("").build();
        req = req.toBuilder().firstname("").build();
        req = req.toBuilder().lastname("").build();
        assertThrows(UserValidationException.class, req::validateRequired);
    }

    @Test
    @DisplayName("validateRequired() - should throw UserValidationException if only one field is missing")
    void shouldThrowUserValidationExceptionIfOnlyOneFieldIsMissing() {
        req = req.toBuilder().username(null).build();
        assertThrows(UserValidationException.class, req::validateRequired);

        req = req.toBuilder().username("No1Hardy").build();
        req = req.toBuilder().email(null).build();
        assertThrows(UserValidationException.class, req::validateRequired);

        req = req.toBuilder().email("test@no1hardy.ch").build();
        req = req.toBuilder().password(null).build();
        assertThrows(UserValidationException.class, req::validateRequired);

        req = req.toBuilder().password("test1234").build();
        req = req.toBuilder().firstname(null).build();
        assertThrows(UserValidationException.class, req::validateRequired);

        req = req.toBuilder().firstname("Silas").build();
        req = req.toBuilder().lastname(null).build();
        assertThrows(UserValidationException.class, req::validateRequired);

        req = req.toBuilder().lastname("Hardegger").build();
        assertDoesNotThrow(req::validateRequired);
    }
}
