package ch.no1hardy.service.front;

import ch.no1hardy.service.exception.user.UserValidationException;
import ch.no1hardy.service.front.user.UserReq;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserReqTest {
    private static final String USERNAME = "No1Hardy";
    private static final String PASSWORD = "test1234";
    private static final String EMAIL = "test@no1hardy.ch";
    private static final String FIRSTNAME = "Silas";
    private static final String LASTNAME = "Hardegger";

    @NotNull
    UserReq req = new UserReq(USERNAME, PASSWORD, EMAIL, FIRSTNAME, LASTNAME, null, null);

    @Test
    @DisplayName("validate() - should validate user request with valid data")
    void shouldValidateUserRequestWithValidData() {
        assertDoesNotThrow(req::validate);
    }

    @Test
    @DisplayName("validate() - should validate user request with null values")
    void shouldValidateUserRequestWithNullValues() {
        req = new UserReq(null, null, null, null, null, null, null);

        assertDoesNotThrow(req::validate);
    }

    @Test
    @DisplayName("validate() - GIVEN an empty username | WHEN validating a dto | THEN a UserValidationException should be thrown")
    void validateEmptyUsername() {
        req = new UserReq("", PASSWORD, EMAIL, FIRSTNAME, LASTNAME, null, null);

        var e = assertThrows(UserValidationException.class, () -> req.validate());
        assertEquals("username must be at least 5 characters long", e.getMessage());
    }

    @Test
    @DisplayName("validate() - GIVEN a short username | WHEN validating a dto | THEN a UserValidationException should be thrown")
    void validateShortUsername() {
        req = new UserReq("m", PASSWORD, EMAIL, FIRSTNAME, LASTNAME, null, null);

        var e = assertThrows(UserValidationException.class, () -> req.validate());
        assertEquals("username must be at least 5 characters long", e.getMessage());
    }

    @Test
    @DisplayName("validate() - GIVEN a long username | WHEN validating a dto | THEN a UserValidationException should be thrown")
    void validateLongUsername() {
        req = new UserReq(
                "aVeryVeryVeryLongUsernameThatWillFailValidation",
                PASSWORD,
                EMAIL,
                FIRSTNAME,
                LASTNAME,
                null,
                null
        );

        var e = assertThrows(UserValidationException.class, () -> req.validate());
        assertEquals("username can only contain 25 characters", e.getMessage());
    }

    @Test
    @DisplayName("validate() - GIVEN an empty firstname | WHEN validating a dto | THEN a UserValidationException should be thrown")
    void validateEmptyFirstname() {
        req = new UserReq(USERNAME, PASSWORD, EMAIL, "", LASTNAME, null, null);

        var e = assertThrows(UserValidationException.class, () -> req.validate());
        assertEquals("firstname must be at least 2 characters long", e.getMessage());
    }

    @Test
    @DisplayName("validate() - GIVEN a short firstname | WHEN validating a dto | THEN a UserValidationException should be thrown")
    void validateShortFirstname() {
        req = new UserReq(USERNAME, PASSWORD, EMAIL, "m", LASTNAME, null, null);

        var e = assertThrows(UserValidationException.class, () -> req.validate());
        assertEquals("firstname must be at least 2 characters long", e.getMessage());
    }

    @Test
    @DisplayName("validate() - GIVEN a long firstname | WHEN validating a dto | THEN a UserValidationException should be thrown")
    void validateLongFirstname() {
        req = new UserReq(
                USERNAME,
                PASSWORD,
                EMAIL,
                "aVeryVeryVeryLongFirstnameThatWillFailValidation",
                LASTNAME,
                null,
                null
        );

        var e = assertThrows(UserValidationException.class, () -> req.validate());
        assertEquals("firstname can only contain 25 characters", e.getMessage());
    }

    @Test
    @DisplayName("validate() - GIVEN an empty lastname | WHEN validating a dto | THEN a UserValidationException should be thrown")
    void validateEmptyLastname() {
        req = new UserReq(USERNAME, PASSWORD, EMAIL, FIRSTNAME, "", null, null);

        var e = assertThrows(UserValidationException.class, () -> req.validate());
        assertEquals("lastname must be at least 2 characters long", e.getMessage());
    }

    @Test
    @DisplayName("validate() - GIVEN a short lastname | WHEN validating a dto | THEN a UserValidationException should be thrown")
    void validateShortLastname() {
        req = new UserReq(USERNAME, PASSWORD, EMAIL, FIRSTNAME, "m", null, null);

        var e = assertThrows(UserValidationException.class, () -> req.validate());
        assertEquals("lastname must be at least 2 characters long", e.getMessage());
    }

    @Test
    @DisplayName("validate() - GIVEN a long lastname | WHEN validating a dto | THEN a UserValidationException should be thrown")
    void validateLongLastname() {
        req = new UserReq(
                USERNAME,
                PASSWORD,
                EMAIL,
                FIRSTNAME,
                "aVeryVeryVeryLongLastnameThatWillFailValidation",
                null,
                null
        );

        var e = assertThrows(UserValidationException.class, () -> req.validate());
        assertEquals("lastname can only contain 25 characters", e.getMessage());
    }

    @Test
    @DisplayName("validate() - GIVEN an empty email | WHEN validating a dto | THEN a UserValidationException should be thrown")
    void validateEmptyEmail() {
        req = new UserReq(USERNAME, PASSWORD, "", FIRSTNAME, LASTNAME, null, null);

        var e = assertThrows(UserValidationException.class, () -> req.validate());
        assertEquals("email must be at least 5 characters long", e.getMessage());
    }

    @Test
    @DisplayName("validate() - GIVEN a short email | WHEN validating a dto | THEN a UserValidationException should be thrown")
    void validateShortEmail() {
        req = new UserReq(USERNAME, PASSWORD, "m", FIRSTNAME, LASTNAME, null, null);

        var e = assertThrows(UserValidationException.class, () -> req.validate());
        assertEquals("email must be at least 5 characters long", e.getMessage());
    }

    @Test
    @DisplayName("validate() - GIVEN a long email | WHEN validating a dto | THEN a UserValidationException should be thrown")
    void validateLongEmail() {
        req = new UserReq(
                USERNAME,
                PASSWORD,
                "aVeryVeryVeryLongEmailThatWillFailValidationButThisEmailIsEvenLongerBecauseItNeedsToBeLongerThan50Characters",
                FIRSTNAME,
                LASTNAME,
                null,
                null
        );

        var e = assertThrows(UserValidationException.class, () -> req.validate());
        assertEquals("email can only contain 50 characters", e.getMessage());
    }

    @Test
    @DisplayName("validate() - GIVEN an empty password | WHEN validating a dto | THEN a UserValidationException should be thrown")
    void validateEmptyPassword() {
        req = new UserReq(USERNAME, "", EMAIL, FIRSTNAME, LASTNAME, null, null);

        var e = assertThrows(UserValidationException.class, () -> req.validate());
        assertEquals("password must be at least 8 characters long", e.getMessage());
    }

    @Test
    @DisplayName("validate() - GIVEN a short password | WHEN validating a dto | THEN a UserValidationException should be thrown")
    void validateShortPassword() {
        req = new UserReq(USERNAME, "m", EMAIL, FIRSTNAME, LASTNAME, null, null);

        var e = assertThrows(UserValidationException.class, () -> req.validate());
        assertEquals("password must be at least 8 characters long", e.getMessage());
    }

    @Test
    @DisplayName("validate() - GIVEN a long password | WHEN validating a dto | THEN a UserValidationException should be thrown")
    void validateLongPassword() {
        req = new UserReq(
                USERNAME,
                "aVeryVeryVeryLongPasswordThatWillFailValidationButThisPasswordIsEvenLongerBecauseItNeedsToBeLongerThan50Characters",
                EMAIL,
                FIRSTNAME,
                LASTNAME,
                null,
                null
        );

        var e = assertThrows(UserValidationException.class, () -> req.validate());
        assertEquals("password can only contain 50 characters", e.getMessage());
    }

    @Test
    @DisplayName("validate() - should validate email with invalid format")
    void shouldValidateEmailWithValidFormat() {
        req = new UserReq(USERNAME, PASSWORD, "invalid-email-format", FIRSTNAME, LASTNAME, null, null);
        assertThrows(UserValidationException.class, req::validate);

        req = new UserReq(USERNAME, PASSWORD, "test!@no1hardy-asdf1234.ch", FIRSTNAME, LASTNAME, null, null);
        assertThrows(UserValidationException.class, req::validate);

        req = new UserReq(USERNAME, PASSWORD, "test123@no1hardy!.ch", FIRSTNAME, LASTNAME, null, null);
        assertThrows(UserValidationException.class, req::validate);

        req = new UserReq(USERNAME, PASSWORD, "test_123@no1hardy.ch", FIRSTNAME, LASTNAME, null, null);
        assertDoesNotThrow(req::validate);

        req = new UserReq(USERNAME, PASSWORD, "test-123@no1hardy.ch", FIRSTNAME, LASTNAME, null, null);
        assertDoesNotThrow(req::validate);

        req = new UserReq(USERNAME, PASSWORD, "test.123@no1hardy.ch", FIRSTNAME, LASTNAME, null, null);
        assertDoesNotThrow(req::validate);

        req = new UserReq(USERNAME, PASSWORD, "test123@no1hardy.ch", FIRSTNAME, LASTNAME, null, null);
        assertDoesNotThrow(req::validate);

        req = new UserReq(USERNAME, PASSWORD, "test@no1hardy.c1h", FIRSTNAME, LASTNAME, null, null);
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
        req = new UserReq(null, PASSWORD, EMAIL, FIRSTNAME, LASTNAME, null, null);
        req = new UserReq(USERNAME, PASSWORD, null, FIRSTNAME, LASTNAME, null, null);
        req = new UserReq(USERNAME, null, EMAIL, FIRSTNAME, LASTNAME, null, null);
        req = new UserReq(USERNAME, PASSWORD, EMAIL, null, LASTNAME, null, null);
        req = new UserReq(USERNAME, PASSWORD, EMAIL, FIRSTNAME, null, null, null);
        assertThrows(UserValidationException.class, req::validateAll);
    }

    @Test
    @DisplayName("validateAll() - should throw UserValidationException for empty fields")
    void shouldThrowUserValidationExceptionForEmptyFields() {
        req = new UserReq("", PASSWORD, EMAIL, FIRSTNAME, LASTNAME, null, null);
        req = new UserReq(USERNAME, PASSWORD, "", FIRSTNAME, LASTNAME, null, null);
        req = new UserReq(USERNAME, "", EMAIL, FIRSTNAME, LASTNAME, null, null);
        req = new UserReq(USERNAME, PASSWORD, EMAIL, "", LASTNAME, null, null);
        req = new UserReq(USERNAME, PASSWORD, EMAIL, FIRSTNAME, "", null, null);
        assertThrows(UserValidationException.class, req::validateAll);
    }

    @Test
    @DisplayName("validateAll() - should throw UserValidationException for invalid fields")
    void shouldThrowUserValidationExceptionForInvalidFields() {
        req = new UserReq(USERNAME, PASSWORD, "invalid-email-format", FIRSTNAME, LASTNAME, null, null);
        req = new UserReq(USERNAME, "1234", EMAIL, FIRSTNAME, LASTNAME, null, null);
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
        req = new UserReq(null, PASSWORD, EMAIL, FIRSTNAME, LASTNAME, null, null);
        req = new UserReq(USERNAME, PASSWORD, null, FIRSTNAME, LASTNAME, null, null);
        req = new UserReq(USERNAME, null, EMAIL, FIRSTNAME, LASTNAME, null, null);
        req = new UserReq(USERNAME, PASSWORD, EMAIL, null, LASTNAME, null, null);
        req = new UserReq(USERNAME, PASSWORD, EMAIL, FIRSTNAME, null, null, null);
        assertThrows(UserValidationException.class, req::validateRequired);
    }

    @Test
    @DisplayName("validateRequired() - should throw UserValidationException for empty required fields")
    void shouldThrowUserValidationExceptionForEmptyRequiredFields() {
        req = new UserReq("", PASSWORD, EMAIL, FIRSTNAME, LASTNAME, null, null);
        req = new UserReq(USERNAME, PASSWORD, "", FIRSTNAME, LASTNAME, null, null);
        req = new UserReq(USERNAME, "", EMAIL, FIRSTNAME, LASTNAME, null, null);
        req = new UserReq(USERNAME, PASSWORD, EMAIL, "", LASTNAME, null, null);
        req = new UserReq(USERNAME, PASSWORD, EMAIL, FIRSTNAME, "", null, null);
        assertThrows(UserValidationException.class, req::validateRequired);
    }

    @Test
    @DisplayName("validateRequired() - should throw UserValidationException if only one field is missing")
    void shouldThrowUserValidationExceptionIfOnlyOneFieldIsMissing() {
        req = new UserReq(null, PASSWORD, EMAIL, FIRSTNAME, LASTNAME, null, null);
        assertThrows(UserValidationException.class, req::validateRequired);

        req = new UserReq("No1Hardy", PASSWORD, EMAIL, FIRSTNAME, LASTNAME, null, null);
        req = new UserReq(USERNAME, PASSWORD, null, FIRSTNAME, LASTNAME, null, null);
        assertThrows(UserValidationException.class, req::validateRequired);

        req = new UserReq(USERNAME, PASSWORD, "test@no1hardy.ch", FIRSTNAME, LASTNAME, null, null);
        req = new UserReq(USERNAME, null, EMAIL, FIRSTNAME, LASTNAME, null, null);
        assertThrows(UserValidationException.class, req::validateRequired);

        req = new UserReq(USERNAME, "test1234", EMAIL, FIRSTNAME, LASTNAME, null, null);
        req = new UserReq(USERNAME, PASSWORD, EMAIL, null, LASTNAME, null, null);
        assertThrows(UserValidationException.class, req::validateRequired);

        req = new UserReq(USERNAME, PASSWORD, EMAIL, "Silas", LASTNAME, null, null);
        req = new UserReq(USERNAME, PASSWORD, EMAIL, FIRSTNAME, null, null, null);
        assertThrows(UserValidationException.class, req::validateRequired);

        req = new UserReq(USERNAME, PASSWORD, EMAIL, FIRSTNAME, "Hardegger", null, null);
        assertDoesNotThrow(req::validateRequired);
    }
}
