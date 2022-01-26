/*
 * Copyright (c) 2022 Alexander Tolpeko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package pizzaonline.service.ingredient;

import pizzaonline.persistence.ingredient.IngredientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class IngredientService implements AdminIngredientService {
    private final IngredientRepository repository;
    private final Validator validator;

    @Autowired
    public IngredientService(IngredientRepository repository, Validator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @Override
    public Optional<Ingredient> findById(int id) {
        try {
            return repository.findById(id);
        } catch (DataAccessException e) {
            throw new IllegalStateException("Database unavailable", e);
        }
    }

    @Override
    public List<Ingredient> findAll() {
        try {
            return repository.findAll();
        } catch (DataAccessException e) {
            throw new IllegalStateException("Database unavailable", e);
        }
    }

    @Override
    public void save(Ingredient ingredient) {
        Optional<Ingredient> savedIngredient = repository.findByName(ingredient.getName());
        if (savedIngredient.isPresent()) {
            throw new IllegalArgumentException("Such an ingredient already exists");
        }

        validate(ingredient);
        try {
            repository.save(ingredient);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Invalid data", e);
        } catch (DataAccessException e) {
            throw new IllegalStateException("Database unavailable", e);
        }
    }

    private void validate(Ingredient ingredient) {
        Set<ConstraintViolation<Ingredient>> violations = validator.validate(ingredient);
        if (!violations.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (ConstraintViolation<Ingredient> violation : violations) {
                builder.append(violation.getMessage()).append(", ");
            }

            builder.delete(builder.length() - 2, builder.length() - 1);
            String msg = builder.toString().toLowerCase(Locale.ROOT);

            throw new IllegalArgumentException("Invalid data: " + msg);
        }
    }

    @Override
    public void update(Ingredient ingredient) {
        try {
            Optional<Ingredient> savedIngredient = repository.findById(ingredient.getId());
            savedIngredient.orElseThrow(() ->
                    new NoSuchElementException("No ingredient with id " + ingredient.getId()));

            validate(ingredient);
            repository.save(ingredient);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Invalid data", e);
        } catch (DataAccessException e) {
            throw new IllegalStateException("Database unavailable", e);
        }
    }

    @Override
    public void deleteById(int id) {
        try {
            repository.deleteById(id);
        } catch (DataAccessException e) {
            throw new IllegalStateException("Database unavailable", e);
        }
    }
}
