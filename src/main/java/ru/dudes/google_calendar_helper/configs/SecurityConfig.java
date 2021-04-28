package ru.dudes.google_calendar_helper.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import ru.dudes.google_calendar_helper.auth2.AuthorizationRequestResolverWithChatId;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                .defaultSuccessUrl("/loginSuccess");

        http.oauth2Login()
                .authorizationEndpoint()
                .authorizationRequestResolver(new AuthorizationRequestResolverWithChatId(getApplicationContext().getBean(ClientRegistrationRepository.class)));
    }
}
