package com.lexisnexis.batch.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    @Qualifier("customAuthManager")
    AuthenticationManager authManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${security.oauth2.resource.id}")
    private String resourceId;

    @Value("${keystore.name}")
    private String keystoreName;

    @Value("${keystore.filename}")
    private String keystoreFilename;

    @Value("${keystore.password}")
    private String keystorePassword;

    /**
     *
     * client_credentials grant type - designed to be used for machine to machine communication
     *      where the end user identification is not necessary for resource authorization. In
     *      this approach, the client application itself acts as the resource owner and make
     *      use of the client credentials for obtaining access tokens
     *
     *      curl -H "Accept: application/json" rbb-api-client:password@localhost:8080/oauth/token -d grant_type=client_credentials
     *      curl -H "Authorization: Bearer <ACCESS-TOKEN>" localhost:8080/protected/
     *
     * authorization_code grant type - this approach uses browser redirections for communicating
     *      with the resource server and the authorization server. It has been purposely designed
     *      for web applications that execute its logic on a web server and which have the capability
     *      to store the client credentials securely without being exposed to the client application
     *      that runs on a web browser.
     *
     *      http://localhost:8080/oauth/authorize?response_type=code&client_id=rbb-api-client
     *
     * implicit grant type - designed for client applications such as client-side web applications
     *      that download its code and executes on a web browser which cannot securely store its
     *      client credentials. Unlike the Authorization Code Grant, this does not use client
     *      credentials for authenticating the client application. Instead, it relies on one the
     *      resource owner authentication for providing the access token.
     *
     * Resource Owner Password Credentials Grant type - has been designed to be used when the
     *      resource owner has a trust relationship with the client application for providing
     *      user credentials directly to the client application without redirecting to the
     *      authorization server. According to the specification this grant should only be used
     *      when other flows are not viable as it may only be suitable for first-party
     *      applications that users would trust.
     *
     * References: https://dzone.com/articles/an-oauth2-grant-selection-decision-tree-for-securi
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("browser")
                .authorizedGrantTypes("refresh_token", "password")
                .scopes("ui")
            .and()
                .withClient("rbb-secured-service")
                .secret(passwordEncoder.encode("Passw0rd")) //TODO: env.getProperty("BATCH_MANAGEMENT_SERVICE_PASSWORD")
                .authorizedGrantTypes("client_credentials", "refresh_token", "password")
                .authorities("ROLE_TRUSTED_CLIENT")
                .scopes("read", "write")
                .resourceIds(resourceId)
                .accessTokenValiditySeconds(120) //TODO: env.getProperty
                .refreshTokenValiditySeconds(600); //TODO: env.getProperty
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        final KeyStoreKeyFactory keyStoreKeyFactory =
                new KeyStoreKeyFactory(
                        new ClassPathResource(keystoreFilename),
                        keystorePassword.toCharArray());
        converter.setKeyPair(keyStoreKeyFactory.getKeyPair(keystoreName));
        return converter;
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setTokenEnhancer(accessTokenConverter());
        return defaultTokenServices;
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .authenticationManager(authManager)
                .tokenServices(tokenServices())
                .tokenStore(tokenStore())
                .accessTokenConverter(accessTokenConverter());
    }

    // Allowing access to the token only for clients with 'ROLE_TRUSTED_CLIENT' authority
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer
                .passwordEncoder(passwordEncoder)
                .tokenKeyAccess("hasAuthority('ROLE_TRUSTED_CLIENT')")
                .checkTokenAccess("hasAuthority('ROLE_TRUSTED_CLIENT')");
    }

}