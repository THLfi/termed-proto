package fi.thl.termed.security;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ThlAuthenticationProvider implements AuthenticationProvider {

  @SuppressWarnings("all")
  private Logger log = LoggerFactory.getLogger(getClass());

  private String authServiceUrl;
  private String application;
  private String secretKey;

  private RestTemplate restTemplate;

  public ThlAuthenticationProvider(String authServiceUrl, String application, String secretKey) {
    this(authServiceUrl, application, secretKey, new RestTemplate());
  }

  public ThlAuthenticationProvider(String authServiceUrl, String application,
                                   String secretKey, RestTemplate restTemplate) {
    this.authServiceUrl = authServiceUrl;
    this.application = application;
    this.secretKey = secretKey;
    this.restTemplate = restTemplate;
  }

  @Override
  public Authentication authenticate(Authentication authentication) {
    Preconditions.checkNotNull(authentication);

    String username = authentication.getName();
    String password = authentication.getCredentials().toString();

    try {
      return authenticate(username, password);
    } catch (Exception e) {
      throw new BadCredentialsException("Authentication failed for user " + username, e);
    }
  }

  private Authentication authenticate(String username, String password)
      throws GeneralSecurityException, IOException, ParserConfigurationException, SAXException {

    Document token = parseXmlDocument(decryptToken(httpPostForEncryptedToken(username, password)));

    if (documentHasElementWithContent(token, "email", username)) {
      return new UsernamePasswordAuthenticationToken(username, password, admin());
    }

    throw new BadCredentialsException("Authentication failed for user " + username);
  }

  private String httpPostForEncryptedToken(String username, String password) {
    MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
    request.add("userId", username);
    request.add("password", password);
    request.add("application", application);
    return restTemplate.postForObject(authServiceUrl, request, String.class);
  }

  private String decryptToken(String encryptedTokenAndIv)
      throws IOException, GeneralSecurityException {
    Properties properties = new Properties();
    properties.load(new StringReader(encryptedTokenAndIv));
    return EncryptionUtils.decrypt(
        properties.getProperty("token"), properties.getProperty("iv"), secretKey);
  }

  private Document parseXmlDocument(String token)
      throws SAXException, IOException, ParserConfigurationException {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    return documentBuilderFactory.newDocumentBuilder().parse(token);
  }

  private boolean documentHasElementWithContent(Document doc, String tagName, String content) {
    NodeList nodes = doc.getElementsByTagName(tagName);
    return nodes != null && nodes.getLength() > 0 && nodes.item(0).getTextContent().equals(content);
  }

  private List<SimpleGrantedAuthority> admin() {
    return Lists.newArrayList(new SimpleGrantedAuthority("admin"));
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }

}
