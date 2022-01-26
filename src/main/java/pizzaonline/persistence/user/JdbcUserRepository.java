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

package pizzaonline.persistence.user;

import pizzaonline.service.user.User;

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
 * Database-based User object repository.
 */
@Repository
public class JdbcUserRepository implements UserRepository {
    private final JdbcTemplate jdbc;
    private final RowMapper<User> rowMapper;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        rowMapper = (resultSet, rowsNum) -> {
            User user = new User();
            user.setId(resultSet.getInt("id"));
            user.setEnabled(resultSet.getBoolean("enabled"));
            user.setRole(User.Role.valueOf(resultSet.getString("role")));
            user.setEmail(resultSet.getString("email"));
            user.setPassword(resultSet.getString("password"));
            user.setName(resultSet.getString("name"));
            user.setPhoneNumber(resultSet.getString("phone"));
            user.setCountry(resultSet.getString("country"));
            user.setCity(resultSet.getString("city"));
            user.setStreet(resultSet.getString("street"));
            user.setHouseNumber(resultSet.getInt("house"));

            return user;
        };
    }

    @Override
    public Optional<User> findById(int id) {
        String query = "SELECT id, email, password, enabled, role, name, phone," +
                "country, city, street, house " +
                "FROM users WHERE id = ?";
        try {
            User user = jdbc.queryForObject(query, rowMapper, id);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException | IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String query = "SELECT id, email, password, enabled, role, name, phone," +
                "country, city, street, house " +
                "FROM users WHERE email = ?";
        try {
            User user = jdbc.queryForObject(query, rowMapper, email);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException | IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        String query = "SELECT id, email, password, enabled, role, name, phone," +
                "country, city, street, house " +
                "FROM users";
        return jdbc.query(query, rowMapper);
    }

    @Override
    public void save(User user) {
        Optional<User> optionalUser = findById(user.getId());
        if (optionalUser.isPresent()) {
            updateUser(user);
        } else {
            createNewUser(user);
        }
    }

    private void updateUser(User user) {
        String query = "UPDATE users " +
                "SET email = ?, password = ?, enabled = ?, role = ?, name = ?, phone = ?," +
                "country = ?, city = ?, street = ?, house = ? " +
                "WHERE id = ?";
        Object[] args = { user.getEmail(), user.getPassword(), user.isEnabled(),
                          user.getRole().toString(), user.getName(), user.getPhoneNumber(),
                          user.getCountry(), user.getCity(), user.getStreet(),
                          user.getHouseNumber(), user.getId() };
        jdbc.update(query, args);
    }

    private void createNewUser(User user) {
        String query = "INSERT INTO users(email, password, enabled, role, name, phone," +
                "country, city, street, house) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatementCreatorFactory statementFactory = new PreparedStatementCreatorFactory(
                query, Types.VARCHAR, Types.VARCHAR, Types.BOOLEAN, Types.VARCHAR, Types.VARCHAR,
                Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER
        );
        statementFactory.setReturnGeneratedKeys(true);
        PreparedStatementCreator statementCreator = statementFactory.newPreparedStatementCreator(
                new Object[] { user.getEmail(), user.getPassword(), user.isEnabled(),
                               user.getRole().toString(), user.getName(), user.getPhoneNumber(),
                               user.getCountry(), user.getCity(), user.getStreet(), user.getHouseNumber() }
        );

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(statementCreator, keyHolder);
        user.setId(keyHolder.getKey().intValue());
    }

    @Override
    public void deleteById(int id) {
        String query = "DELETE FROM users WHERE id = ?";
        jdbc.update(query, id);
    }
}
