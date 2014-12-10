package fi.thl.termed.controller;

import com.google.gson.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value = "/user")
public class UserController {

  @SuppressWarnings("all")
  private Logger log = LoggerFactory.getLogger(getClass());

  @Qualifier("authenticationProvider")
  @Autowired
  private AuthenticationProvider authenticationProvider;

  @RequestMapping(method = POST, value = "authenticate")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void authenticate(@RequestParam("username") String username,
                           @RequestParam("password") String password,
                           HttpServletRequest request) {
    log.info("Authenticating {}", username);

    Authentication authentication = authenticationProvider.authenticate(
        new UsernamePasswordAuthenticationToken(username, password));

    // init session after successful authentication
    request.getSession();
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  @RequestMapping(method = {GET, POST}, value = "logout")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void logout(HttpServletRequest request) {
    HttpSession context = request.getSession(false);
    if (context != null) {
      context.invalidate();
    }

    SecurityContextHolder.getContext().setAuthentication(null);
    SecurityContextHolder.clearContext();
  }

  @RequestMapping(method = GET, value = "info")
  @ResponseBody
  public JsonObject userInfo(HttpServletRequest request) {
    JsonObject user = new JsonObject();
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null) {
      user.addProperty("name", auth.getName());
      user.addProperty("role", role(auth.getAuthorities()));
    }
    return user;
  }

  private String role(Collection<? extends GrantedAuthority> authorities) {
    return authorities != null && !authorities.isEmpty() ?
           authorities.iterator().next().getAuthority() : "";
  }

}
