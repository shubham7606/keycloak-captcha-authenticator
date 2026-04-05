# keycloak-captcha-authenticator
A custom authenticator for Keycloak that integrates Google reCAPTCHA (v2) into the login flow to enhance security and prevent automated attacks.

How it works
1. First of all, run Keycloak using Docker. Create a realm and client as per your convenience.

2. Create a JAR of this project using:
   mvn clean install

3. Deploy this JAR to Keycloak using Docker volume:

        volumes:
          - D:\captcha-authenticator-jar:/opt/keycloak/providers

   Alternatively, copy the JAR manually into the providers folder.
4. Add CSP (Content Security Policy) in Keycloak:

   1. Login to Keycloak Admin Console.
   2. Go to Realm Settings → Security Defenses.
   3. Paste the following content:

      frame-src 'self' https://www.google.com https://www.gstatic.com;
      frame-ancestors 'self';
      object-src 'none';
      script-src 'self' 'unsafe-inline' https://www.google.com https://www.gstatic.com;
      style-src 'self' 'unsafe-inline';
      img-src 'self' data: https://www.google.com https://www.gstatic.com;
      connect-src 'self' https://www.google.com;

5. For Google reCAPTCHA keys:

   1. Open:
      https://www.google.com/recaptcha/admin/create

   2. Fill details:
      - Label: any name
      - reCAPTCHA type: v2
      - Domains: localhost

   3. Click submit.

   Now you will get the Secret Key and Site Key.

6. Configure Authentication Flow:

   1. Go to Authentication → Flows.
   2. Duplicate the Browser Flow.
   3. Add execution below username/password form:
      "Captcha Authentication"
   4. Click settings icon and add:
      - Alias
      - Captcha Secret Key
      - Captcha Site Key

7. Activate the flow:
   Go to Authentication → Bindings and set your custom flow to browser flow.

