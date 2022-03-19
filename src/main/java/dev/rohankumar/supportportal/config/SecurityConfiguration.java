package dev.rohankumar.supportportal.config;

import dev.rohankumar.supportportal.filter.AccessDeniedFilter;
import dev.rohankumar.supportportal.filter.AuthenticationEntryPointFilter;
import dev.rohankumar.supportportal.filter.AuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static dev.rohankumar.supportportal.constant.SecurityConstant.PUBLIC_URLS;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final AccessDeniedFilter accessDeniedFilter;
    private final AuthorizationFilter authorizationFilter;
    private final UserDetailsService userDetailsService;
    private final AuthenticationEntryPointFilter authenticationEntryPointFilter;

    @Autowired
    public SecurityConfiguration(PasswordEncoder passwordEncoder,
                                 AccessDeniedFilter accessDeniedFilter,
                                 AuthorizationFilter authorizationFilter,
                                 UserDetailsService userDetailsService,
                                 AuthenticationEntryPointFilter authenticationEntryPointFilter) {
        this.passwordEncoder = passwordEncoder;
        this.accessDeniedFilter = accessDeniedFilter;
        this.authorizationFilter = authorizationFilter;
        this.userDetailsService = userDetailsService;
        this.authenticationEntryPointFilter = authenticationEntryPointFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().cors()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests().antMatchers(PUBLIC_URLS).permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedFilter)
                .authenticationEntryPoint(authenticationEntryPointFilter)
                .and()
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}