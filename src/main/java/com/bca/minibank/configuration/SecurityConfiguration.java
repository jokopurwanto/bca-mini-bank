package com.bca.minibank.configuration;


//import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
//import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
		
	@Bean
	public UserDetailsService userDetailsService() {
		return new MiniBankUserDetailsService();
	};
	
    @Bean
    public AuthenticationSuccessHandler myAuthenticationSuccessHandler(){
        return new UrlAuthenticationSuccessHandler();
    }
    
    @Bean
    public AuthenticationFailureHandler myAuthenticationFailureHandler(){
        return new CustomAuthenticationFailureHandler();
    }
    
    //Tambahan untuk di merge ke branch hany
    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new MBLogoutSuccessHandler();
    }
	 
    @Bean 
    public AuthenticationProvider authProvider()
    {
    	DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    	provider.setUserDetailsService(userDetailsService());
    	provider.setPasswordEncoder(bCryptPasswordEncoder);
    	return provider;
    }

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests()
					.antMatchers("/").anonymous()
					.antMatchers("/login").anonymous()
					.antMatchers("/registrasi/**").anonymous()
					.antMatchers("/admin/**").hasAuthority("ADMIN")
					.antMatchers("/verifikasi/**").hasAuthority("AKUNBARU")
					.antMatchers("/beranda").hasAnyAuthority("ADMIN", "NASABAH")
					.antMatchers("/nasabah/**").hasAuthority("NASABAH")
					.anyRequest().authenticated()
				.and()
					.csrf().disable().formLogin()
					.loginPage("/login")
					.failureHandler(myAuthenticationFailureHandler())
					.successHandler(myAuthenticationSuccessHandler())
					.usernameParameter("username")
					.passwordParameter("password")
				.and()
					.logout()
					.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
					.logoutSuccessHandler(logoutSuccessHandler())
				.and()
					.exceptionHandling()
					.accessDeniedPage("/access-denied");
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**");
	}

	
}