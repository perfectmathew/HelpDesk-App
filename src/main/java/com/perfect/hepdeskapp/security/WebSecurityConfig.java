package com.perfect.hepdeskapp.security;

import com.perfect.hepdeskapp.user.UserDetailService;
import com.perfect.hepdeskapp.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final DataSource dataSource;
    final
    UserRepository userRepository;

    public WebSecurityConfig(DataSource dataSource, UserRepository userRepository) {
        this.dataSource = dataSource;
        this.userRepository = userRepository;
    }
    @Bean
    public UserDetailsService userDetailsService(){
        return new UserDetailService();
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.authenticationProvider(authenticationProvider());
    }
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests()
                .antMatchers("/register","/signup","/showstatus","/status","/error","/404").permitAll()
                .antMatchers("/user","/user/**").hasAnyAuthority("USER","ADMIN")
                .antMatchers("/admin","/admin/**").hasAuthority("ADMIN")
                .antMatchers("/manager","/manager/**").hasAnyAuthority("ADMIN","DEPARTMENT_BOSS")
                .antMatchers("/t","/t/**","tickets","/tickets/**").hasAnyAuthority("ADMIN","WORKER","DEPARTMENT_BOSS")
                .antMatchers("/worker","/worker/**").hasAuthority("WORKER")
                .antMatchers("/api","/api/**").hasAnyAuthority("ADMIN","DEPARTMENT_BOSS","WORKER","USER")
                .antMatchers("/resources/*","/resources/**/*", "/uploads/**/*", "/uploads/*","/css/**","/js/**").permitAll()
                .anyRequest().authenticated()
                .and().formLogin().loginPage("/auth").failureUrl("/auth?error=true")
                .usernameParameter("email").passwordParameter("password").defaultSuccessUrl("/direct")
                .permitAll()
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/auth?logout=true").permitAll();
    }
    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
        db.setDataSource(dataSource);
        return db;
    }
}
