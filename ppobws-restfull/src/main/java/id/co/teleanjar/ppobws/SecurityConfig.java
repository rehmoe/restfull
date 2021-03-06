/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package id.co.teleanjar.ppobws;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author moe
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Configuration
    public static class WebappSecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired private DataSource dataSource;
        
        @Autowired private PasswordEncoder passwordEncoder;
        
        private static final String SQL_LOGIN
            = "select u.username as username,p.user_password as password, active "
            + "from c_security_user u "
            + "inner join c_security_user_password p on p.id_user = u.id "
            + "where username = ?";

        private static final String SQL_ROLE
            = "select u.username, p.permission_value as authority "
            + "from c_security_user u "
            + "inner join c_security_role r on u.id_role = r.id "
            + "inner join c_security_role_permission rp on rp.id_role = r.id "
            + "inner join c_security_permission p on rp.id_permission = p.id "
            + "where u.username = ?";
    
    
        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(daoAuthenticationProvider());
        }
       
        @Bean
        public AuthenticationProvider daoAuthenticationProvider(){
            DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
            provider.setPasswordEncoder(passwordEncoder);
            provider.setUserDetailsService(userDetailsService());
            return provider;
        }

        @Bean
        @Override
        public UserDetailsService userDetailsService(){
            JdbcDaoImpl userDetails = new JdbcDaoImpl();
            userDetails.setDataSource(dataSource);
            userDetails.setUsersByUsernameQuery(SQL_LOGIN);
            userDetails.setAuthoritiesByUsernameQuery(SQL_ROLE);
            return userDetails;
        }
    }

    @Configuration
    @Order(-10)
    public static class OAuthSecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        private AuthenticationManager authenticationManager;
        
        @Bean 
        public SessionRegistry sessionRegistry(){
            return new SessionRegistryImpl();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .csrf().disable();
            http
                    .sessionManagement()
                    .maximumSessions(1)
                    .maxSessionsPreventsLogin(true)
                    .sessionRegistry(sessionRegistry());
            
            http
                    .authorizeRequests()
                        .antMatchers("/api/user").hasRole("DATA_USER")
                        .anyRequest().authenticated()
                .and()
                    .httpBasic();
        }
        
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.parentAuthenticationManager(authenticationManager);
        }
    }
}
