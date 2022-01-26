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

package pizzaonline.service.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import java.util.Collection;
import java.util.List;

/**
 * User domain class
 */
@Data
@AllArgsConstructor
public class User implements UserDetails {

    /**
     * An enumeration denoting user access rights.
     * Default value is ROLE_ANONYMOUS.
     */
    public enum Role { USER, ADMIN, ANONYMOUS }

    private int id;
    private boolean isEnabled;
    private Role role;

    @NotBlank(message = "You must specify email")
    @Email
    private String email;

    @NotBlank(message = "")
    @Size(min = 8, message = "Password must be at least  8 characters long")
    private String password;

    @NotBlank(message = "You must specify full name")
    private String name;

    @NotBlank(message = "You must specify phone number")
    private String phoneNumber;

    @NotBlank(message = "You must specify country")
    private String country;

    @NotBlank(message = "You must specify city")
    private String city;

    @NotBlank(message = "You must specify street")
    private String street;

    @Positive(message = "House number must be positive")
    private int houseNumber;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        GrantedAuthority authority = new SimpleGrantedAuthority(role.toString());
        return List.of(authority);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isEnabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isEnabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isEnabled;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Constructs a new User with ANONYMOUS role.
     */
    public User() {
       role = Role.ANONYMOUS;
       isEnabled = true;
       password = RandomStringUtils.random(10, true, true);
    }

    public User(User user) {
        id = user.id;
        isEnabled = user.isEnabled;
        role = user.role;
        email = user.email;
        password = user.password;
        name = user.name;
        phoneNumber = user.phoneNumber;
        country = user.country;
        city = user.city;
        street = user.street;
        houseNumber = user.houseNumber;
    }
}
