package com.example.application.views.login;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;


@AnonymousAllowed
@PageTitle("Login")
@Route("login")
@CssImport("./themes/vaadintyoharjoitus/customtheme.css")
public class LoginView extends LoginOverlay implements BeforeEnterObserver {

    public LoginView() {
        setAction("login");

        LoginI18n i18n = LoginI18n.createDefault();


        LoginI18n.Header header = new LoginI18n.Header();
        header.setTitle("Vaadin Työharjoitus");
        header.setDescription("Kirjaudu sisään: user/user tai admin/admin");
        i18n.setHeader(header);

        i18n.setAdditionalInformation(null);
        setI18n(i18n);

        setForgotPasswordButtonVisible(false);
        setOpened(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            setError(true);
        }
    }
}
