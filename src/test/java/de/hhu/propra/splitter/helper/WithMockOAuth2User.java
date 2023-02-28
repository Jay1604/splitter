package de.hhu.propra.splitter.helper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.test.context.support.WithSecurityContext;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithOAuth2UserSecurityContextFactory.class)
//CHECKSTYLE.OFF: AbbreviationAsWordInName
public @interface WithMockOAuth2User {

  //CHECKSTYLE.ON: AbbreviationAsWordInName
  int id() default 666666;


  String login() default "username";

  String[] roles() default {"USER"};

  String[] authorities() default {};

  String clientRegistrationId() default "github";
}
