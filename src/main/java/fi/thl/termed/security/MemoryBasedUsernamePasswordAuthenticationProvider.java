package fi.thl.termed.security;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Map;

public class MemoryBasedUsernamePasswordAuthenticationProvider implements AuthenticationProvider {

  @SuppressWarnings("all")
  private Logger log = LoggerFactory.getLogger(getClass());

  private Map<String, String> users;

  public MemoryBasedUsernamePasswordAuthenticationProvider(String userPassList) {
    this(parseMap(userPassList));
  }

  public MemoryBasedUsernamePasswordAuthenticationProvider(Map<String, String> users) {
    this.users = users;
    log.warn("Don't use this in production.");
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    Preconditions.checkNotNull(authentication);

    String username = authentication.getName();
    String password = authentication.getCredentials().toString();

    try {
      if (users.containsKey(username) && users.get(username).equals(password)) {
        return new UsernamePasswordAuthenticationToken(username, password, admin());
      } else {
        throw new BadCredentialsException("Authentication failed for user " + username);
      }
    } catch (Exception e) {
      throw new BadCredentialsException("Authentication failed for user " + username, e);
    }
  }

  private List<SimpleGrantedAuthority> admin() {
    return Lists.newArrayList(new SimpleGrantedAuthority("admin"));
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }

  private static Map<String, String> parseMap(String str) {
    Map<String, String> map = Maps.newHashMap();
    for (String entry : str.split(",")) {
      String[] keyValue = entry.trim().split(":");
      map.put(keyValue[0], keyValue[1]);
    }
    return map;
  }

}
