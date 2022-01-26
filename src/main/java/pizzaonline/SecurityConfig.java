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

package pizzaonline;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final DataSource dataSource;

    @Autowired
    public SecurityConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SpringTemplateEngine templateEngine(ITemplateResolver templateResolver,
                                               SpringSecurityDialect sec) {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.addDialect(sec);
        return templateEngine;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests()
                .antMatchers("/ingredients/manage", "/pizzas/manage", "/drinks/manage")
                    .hasAnyAuthority("ADMIN")
                .antMatchers("/ingredients/new", "/pizzas/new", "/drinks/new")
                    .hasAuthority("ADMIN")
                .antMatchers("/ingredients/edit/**", "/pizzas/edit/**", "/drinks/edit/**")
                     .hasAuthority("ADMIN")
                .antMatchers("/users/**")
                    .hasAuthority("ADMIN")
                .antMatchers("/orders/**")
                    .hasAuthority("ADMIN")
                .antMatchers(HttpMethod.POST, "/ingredients/**", "/pizzas/**", "/drinks/**")
                    .hasAuthority("ADMIN")
                .antMatchers(HttpMethod.PATCH,"/ingredients/**", "/pizzas/**", "/drinks/**")
                    .hasAuthority("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/ingredients/**", "/pizzas/**", "/drinks/**")
                    .hasAuthority("ADMIN")
                .antMatchers("/manage/**")
                    .hasAuthority("ADMIN")
                .antMatchers("/account", "/account/edit", "/account/edit-login")
                    .hasAnyAuthority("USER", "ADMIN")
                .antMatchers("/account/**")
                    .permitAll()
                .antMatchers("/**")
                    .permitAll()
                .and().formLogin()
                    .loginPage("/account/login")
                    .defaultSuccessUrl("/account", true)
                    .failureUrl("/account/login?error")
                .and().rememberMe()
                    .rememberMeParameter("remember-me")
                    .rememberMeCookieName("remember-me")
                .and().logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/account/logout"))
                    .logoutSuccessUrl("/account")
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .deleteCookies("JSESSIONID");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(encoder())
                .usersByUsernameQuery(
                        "SELECT email, password, enabled FROM users WHERE email = ?")
                .authoritiesByUsernameQuery(
                        "SELECT email, role FROM users WHERE email = ?");
    }
}
