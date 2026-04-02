package org.example;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.Arrays;
import java.util.List;

public class CaptchaAuthenticatorFactory implements AuthenticatorFactory {

    public static final String PROVIDER_ID = "captcha-authenticator";

    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.REQUIRED,
            AuthenticationExecutionModel.Requirement.ALTERNATIVE,
            AuthenticationExecutionModel.Requirement.DISABLED,
            AuthenticationExecutionModel.Requirement.CONDITIONAL
    };

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return new CaptchaAuthenticator();
    }

    @Override
    public String getDisplayType() {
        return "Captcha Authentication";
    }

    @Override
    public String getReferenceCategory() {
        return "captcha"; // optional grouping
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return true;
    }

    @Override
    public String getHelpText() {
        return "Validates user using Google reCAPTCHA";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {

        ProviderConfigProperty secret = new ProviderConfigProperty();
        secret.setName("captcha.secret.key");
        secret.setLabel("Captcha Secret Key");
        secret.setType(ProviderConfigProperty.STRING_TYPE);
        secret.setHelpText("Enter Google reCAPTCHA Secret Key");

        ProviderConfigProperty siteKey = new ProviderConfigProperty();
        siteKey.setName("captcha.site.key");
        siteKey.setLabel("Captcha Site Key");
        siteKey.setType(ProviderConfigProperty.STRING_TYPE);
        siteKey.setHelpText("Enter Google reCAPTCHA Site Key");

        return Arrays.asList(secret, siteKey);
    }

    @Override
    public void init(Config.Scope scope) {}

    @Override
    public void postInit(KeycloakSessionFactory factory) {}

    @Override
    public void close() {}
}