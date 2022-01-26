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

package pizzaonline.persistence.drink;

import pizzaonline.service.drink.Drink;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.List;
import java.util.Optional;

/**
 * Database-based Drink object repository.
 */
@Repository
public class JdbcDrinkRepository implements DrinkRepository {
    private final JdbcTemplate jdbc;
    private final RowMapper<Drink> rowMapper;

    @Autowired
    public JdbcDrinkRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        rowMapper = (resultSet, rowNum) -> {
            Drink drink = new Drink();
            drink.setId(resultSet.getInt("id"));
            drink.setName(resultSet.getString("name"));
            drink.setPrice(resultSet.getDouble("price"));
            drink.setImageData(resultSet.getBytes("img_data"));
            drink.setCapacity(resultSet.getDouble("size"));

            return drink;
        };
    }

    @Override
    public Optional<Drink> findById(int id) {
        String query = "SELECT id, name, price, img_data, size " +
                "FROM products WHERE id = ?";
        try {
            Drink drink = jdbc.queryForObject(query, rowMapper, id);
            return Optional.ofNullable(drink);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Drink> findAll() {
        String query = "SELECT id, name, price, img_data, size " +
                "FROM products WHERE type = 'drink'";
        return jdbc.query(query, rowMapper);
    }

    @Override
    public List<Drink> findAllByName(String name) {
        String query = "SELECT id, name, price, img_data, size " +
                "FROM products WHERE type = 'drink' and name = ?";
        return jdbc.query(query, rowMapper, name);
    }

    @Override
    public void save(Drink drink) {
        Optional<Drink> optionalDrink = findById(drink.getId());
        if (optionalDrink.isPresent()) {
            updateDrink(drink);
        } else {
            createNewDrink(drink);
        }
    }

    private void updateDrink(Drink data) {
        String query = "UPDATE products " +
                "SET name = ?, type = 'drink', price = ?, img_data = ?, size = ? " +
                "WHERE id = ?";
        Object[] args = { data.getName(), data.getPrice(), data.getImageData(),
                          data.getCapacity(), data.getId() };
        jdbc.update(query, args);
    }

    private void createNewDrink(Drink drink) {
        String query = "INSERT INTO products(name, type, price, img_data, size) " +
                "VALUES (?, 'drink', ?, ?, ?)";
        PreparedStatementCreatorFactory statementFactory = new PreparedStatementCreatorFactory(
                query, Types.VARCHAR, Types.DOUBLE, Types.BLOB, Types.DOUBLE
        );
        statementFactory.setReturnGeneratedKeys(true);
        PreparedStatementCreator statementCreator = statementFactory.newPreparedStatementCreator(
                List.of(drink.getName(), drink.getPrice(), drink.getImageData(), drink.getCapacity())
        );
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(statementCreator, keyHolder);
        drink.setId(keyHolder.getKey().intValue());
    }

    @Override
    public void deleteById(int id) {
        String query = "DELETE FROM products WHERE id = ?";
        jdbc.update(query, id);
    }
}
