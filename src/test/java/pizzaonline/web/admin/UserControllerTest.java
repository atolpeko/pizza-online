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

package pizzaonline.web.admin;

import category.IntegrationTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Category(IntegrationTest.class)
@RunWith(SpringRunner.class)
@SpringBootTest
@WithMockUser(username = "atolpeko@gmail.com", authorities = "ADMIN")
public class UserControllerTest {
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    public void shouldReturnUsersPageOnUsersGetRequest() throws Exception {
        mvc.perform(get("/users").param("registered", "false"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/users"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    public void shouldReturnUserPageOnUserGetRequestWhenUserExists() throws Exception {
        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/user-info"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    public void shouldReturn404PageOnUserGetRequestWhenUserDoesNotExist() throws Exception {
        mvc.perform(get("/users/10000"))
                .andExpect(status().isOk())
                .andExpect(view().name("error/404"));
    }

    @Test
    public void shouldRedirectToUserOnUserStatusPatchRequestWhenUserExists() throws Exception {
        mvc.perform(patch("/users/status/3")
                        .with(csrf())
                        .param("block", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/3"));
    }

    @Test
    public void shouldReturn404PageOnUserStatusPatchRequestWhenUserDoesNotExist() throws Exception {
        mvc.perform(patch("/users/status/10000")
                        .with(csrf())
                        .param("block", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("error/404"));
    }
}
