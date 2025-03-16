package br.com.gerenciador.domain.model;

import br.com.gerenciador.domain.exception.UserException;
import br.com.gerenciador.domain.exception.enums.ErrorCodeEnum;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class User {
    private UUID id;
    private String name;
    private String password;
    private String email;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=*!(){}\\[\\]~-])(?=\\S+$).{8,}$");

    public User(UUID id, String name, String password, String email) throws UserException {
        this.id = id;
        setName(name);
        setPassword(password);
        setEmail(email);
    }

    public User(String name, String password, String email) throws UserException {
        this.id = null;
        setName(name);
        setPassword(password);
        setEmail(email);
    }

    public User() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws UserException {
        if (name == null || name.trim().isEmpty()) {
            throw new UserException(
                    ErrorCodeEnum.USER0006.getMessage(),
                    ErrorCodeEnum.USER0006.getCode()
            );
        }

        if (name.trim().length() < 3) {
            throw new UserException(
                    ErrorCodeEnum.USER0007.getMessage(),
                    ErrorCodeEnum.USER0007.getCode()
            );
        }

        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) throws UserException {
        if (password == null || password.trim().isEmpty()) {
            throw new UserException(
                    ErrorCodeEnum.USER0003.getMessage(),
                    ErrorCodeEnum.USER0003.getCode()
            );
        }

        if (password.length() < 8) {
            throw new UserException(
                    ErrorCodeEnum.USER0008.getMessage(),
                    ErrorCodeEnum.USER0008.getCode()
            );
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new UserException(
                    ErrorCodeEnum.USER0009.getMessage(),
                    ErrorCodeEnum.USER0009.getCode()
            );
        }

        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws UserException {
        if (email == null || email.trim().isEmpty()) {
            throw new UserException(
                    ErrorCodeEnum.USER0010.getMessage(),
                    ErrorCodeEnum.USER0010.getCode()
            );
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new UserException(
                    ErrorCodeEnum.USER0011.getMessage(),
                    ErrorCodeEnum.USER0011.getCode()
            );
        }

        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(name, user.name) && Objects.equals(password, user.password) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, password, email);
    }
}
