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

package pizzaonline.persistence.ingredient;

import pizzaonline.service.ingredient.Ingredient;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.List;
import java.util.Optional;

/**
 * Database-based Ingredient object repository.
 */
@Repository
public class JdbcIngredientRepository implements IngredientRepository {
    private final JdbcTemplate jdbc;
    private final RowMapper<Ingredient> rowMapper;

    @Autowired
    public JdbcIngredientRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        rowMapper = (resultSet, rowsNum) -> {
            Ingredient ingredient = new Ingredient();
            ingredient.setId(resultSet.getInt("id"));
            ingredient.setName(resultSet.getString("name"));

            return ingredient;
        };
    }

    @Override
    public Optional<Ingredient> findById(int id) {
        String query = "SELECT id, name FROM ingredients WHERE id = ?";
        try {
            Ingredient ingredient = jdbc.queryForObject(query, rowMapper, id);
            return Optional.ofNullable(ingredient);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Ingredient> findByName(String name) {
        String query = "SELECT id, name FROM ingredients WHERE name = ?";
        try {
            Ingredient ingredient = jdbc.queryForObject(query, rowMapper, name);
            return Optional.ofNullable(ingredient);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Ingredient> findAll() {
        String query = "SELECT id, name FROM ingredients ";
        return jdbc.query(query, rowMapper);
    }

    @Override
    public void save(Ingredient ingredient) {
        Optional<Ingredient> optionalIngredient = findById(ingredient.getId());
        if (optionalIngredient.isPresent()) {
            updateIngredient(ingredient);
        } else {
            createNewIngredientDrink(ingredient);
        }
    }

    private void updateIngredient(Ingredient data) {
        String query = "UPDATE ingredients SET name = ? WHERE id = ?";
        jdbc.update(query, data.getName(), data.getId());
    }

    private void createNewIngredientDrink(Ingredient ingredient) {
        String query = "INSERT INTO ingredients(name) VALUES (?)";
        PreparedStatementCreatorFactory statementFactory = new PreparedStatementCreatorFactory(
                query, Types.VARCHAR
        );
        statementFactory.setReturnGeneratedKeys(true);
        PreparedStatementCreator statementCreator = statementFactory.newPreparedStatementCreator(
                List.of(ingredient.getName())
        );
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(statementCreator, keyHolder);
        ingredient.setId(keyHolder.getKey().intValue());
    }

    @Override
    public void deleteById(int id) {
        String query = "DELETE FROM ingredients WHERE id = ?";
        jdbc.update(query, id);
    }
}
