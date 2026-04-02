package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CaptchaAuthenticator implements Authenticator {

    @Override
    public void authenticate(AuthenticationFlowContext context) {

        String siteKey = context.getAuthenticatorConfig()
                .getConfig().get("captcha.site.key");

        System.out.println(siteKey+ " is the site key");

        Response form = context.form()
                .setAttribute("siteKey", siteKey)
                .createForm("captcha.ftl");

        context.challenge(form);
    }

    @Override
    public void action(AuthenticationFlowContext context) {

        MultivaluedMap<String, String> formData =
                context.getHttpRequest().getDecodedFormParameters();

        String captchaResponse = formData.getFirst("g-recaptcha-response");

        System.out.println(captchaResponse+ " is the captcharesponse");

        // ✅ Check if CAPTCHA is completed
        if (captchaResponse == null || captchaResponse.isEmpty()) {
            Response challenge = context.form()
                    .setError("CAPTCHA not completed")
                    .createForm("captcha.ftl");

            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challenge);
            return;
        }

        // ✅ Validate CAPTCHA with Google
        if (!validateCaptcha(context, captchaResponse)) {
            Response challenge = context.form()
                    .setError("Invalid CAPTCHA")
                    .createForm("captcha.ftl");

            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challenge);
            return;
        }

        context.success();
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        // No required actions
    }

    @Override
    public void close() {
    }

    // 🔐 CAPTCHA validation method
    private boolean validateCaptcha(AuthenticationFlowContext context, String responseToken) {
        try {
            String secretKey = context.getAuthenticatorConfig()
                    .getConfig().get("captcha.secret.key");

            System.out.println(secretKey+" is the secret key");
//            secretKey = "6LdI0JssAAAAAK3x0OkNyS7JLObj3ddlta4OmVrn";

            String remoteIp = context.getConnection().getRemoteAddr();

            String params = "secret=" + URLEncoder.encode(secretKey, "UTF-8") +
                    "&response=" + URLEncoder.encode(responseToken, "UTF-8") +
                    "&remoteip=" + URLEncoder.encode(remoteIp, "UTF-8");

            URL url = new URL("https://www.google.com/recaptcha/api/siteverify");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            os.write(params.getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
            );

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            String jsonResponse = response.toString();
            System.out.println("Google CAPTCHA response: " + jsonResponse);

            // ✅ Parse JSON properly
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(jsonResponse);

            return jsonNode.get("success").asBoolean();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}