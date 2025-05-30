package com.sisimpur.library.validation;

import com.sisimpur.library.repository.AuthorRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthorExistsValidator implements ConstraintValidator<AuthorExists, Integer> {

    @Autowired
    private AuthorRepository authorRepository;

    @Override
    public boolean isValid(Integer authorId, ConstraintValidatorContext context) {
        if (authorId == null) {
            return false; // handle null here if not using @NotNull
        }
        return authorRepository.existsById(authorId.longValue());
    }
}
