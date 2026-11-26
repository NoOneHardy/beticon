package ch.no1hardy.service.front.user;

import ch.no1hardy.service.exception.user.UserValidationException;

import java.util.regex.Pattern;

public record UserReq(
        String username,
        String password,
        String email,
        String firstname,
        String lastname,
        String avatar,
        PasswordChange passwordChange
) {
    private static final Pattern EMAIL_PATTERN = Pattern
            .compile("^[A-Za-z0-9-._]+@([A-Za-z0-9-]+\\.)+[A-Za-z0-9-]{2,}$");

    /**
     * Complete validation of the user request.
     *
     * @throws UserValidationException if any validation fails
     */
    public void validateAll() throws UserValidationException {
        validateRequired();
        validate();
    }

    /**
     * Validates the user request.
     * This method should be implemented to check the validity of the user data.
     *
     * @throws UserValidationException if the validation fails
     */
    public void validate() throws UserValidationException {
        if (username() != null) validateLength(5, 25, username(), "username", "Benutzername");

        if (firstname() != null) validateLength(2, 25, firstname(), "firstname", "Vorname");

        if (lastname() != null) validateLength(2, 25, lastname(), "lastname", "Nachname");

        if (email() != null) {
            validateLength(5, 50, email(), "email", "Email");

            if (!EMAIL_PATTERN.matcher(email()).matches()) {
                throw new UserValidationException("Invalid email format", "Ungültiges Email-Format");
            }
        }

        if (password() != null) validateLength(8, 50, password(), "password", "Passwort");
    }

    private void validateLength(int min, int max, String value, String name, String displayName) {
        if (value.length() < min) throw new UserValidationException(
                name + " must be at least " + min + " characters long",
                displayName + " muss mindestens " + min + " Zeichen lang sein."
        );

        if (value.length() > max) throw new UserValidationException(
                name + " can only contain " + max + " characters",
                displayName + " darf maximal " + max + " Zeichen enthalten."
        );
    }

    /**
     * Validates the required fields of the user request.
     * This method should be implemented to check that all required fields are present.
     *
     * @throws UserValidationException if any required field is missing
     */
    public void validateRequired() throws UserValidationException {
        if (username() == null || username().isEmpty()) throw new UserValidationException("username");

        if (email() == null || email().isEmpty()) throw new UserValidationException("email");

        if (password() == null || password().isEmpty()) throw new UserValidationException("password");

        if (firstname() == null || firstname().isEmpty()) throw new UserValidationException("firstname");

        if (lastname() == null || lastname().isEmpty()) throw new UserValidationException("lastname");
    }

    public record PasswordChange(String password, String currentPassword) {
    }
}
