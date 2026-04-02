<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true; section>

<#if section = "header">
    CAPTCHA Verification

<#elseif section = "form">

    <!-- Load reCAPTCHA v2 (Google recommended) -->
    <script src="https://www.google.com/recaptcha/api.js" async defer></script>

    <form id="kc-captcha-form"
          class="${properties.kcFormClass!}"
          action="${url.loginAction}"
          method="post">

        <!-- Google recommended widget -->
        <div class="g-recaptcha"
              data-sitekey="${siteKey}"
             data-theme="light"
             data-size="normal">
        </div>

        <br/>

        <div id="kc-form-buttons">
            <input class="${properties.kcButtonClass!}
                           ${properties.kcButtonPrimaryClass!}"
                   type="submit"
                   value="Verify & Continue"/>
        </div>

    </form>

<#elseif section = "info">
    Please complete CAPTCHA verification

</#if>

</@layout.registrationLayout>