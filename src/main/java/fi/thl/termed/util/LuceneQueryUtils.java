package fi.thl.termed.util;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public final class LuceneQueryUtils {

  private LuceneQueryUtils() {
  }

  public static String termQuery(String field, String text) {
    return !Strings.isNullOrEmpty(text) ? (!Strings.isNullOrEmpty(field) ? field + ":" : "") + text
                                        : "";
  }

  public static String and(String... queries) {
    return Joiner.on(" AND ")
        .join(wrapWithParentheses(filter(fromArray(queries), notNullOrEmpty())));
  }

  private static List<String> fromArray(String[] strings) {
    return strings == null ? Collections.<String>emptyList() : Lists.newArrayList(strings);
  }

  private static List<String> filter(List<String> strings, Predicate<String> predicate) {
    return Lists.newArrayList(Iterables.filter(strings, predicate));
  }

  private static Predicate<String> notNullOrEmpty() {
    return Predicates.not(Predicates.or(Predicates.isNull(), Predicates.equalTo("")));
  }

  private static List<String> wrapWithParentheses(List<String> strings) {
    return strings == null ? Collections.<String>emptyList() :
           Lists.transform(strings, new Function<String, String>() {
             @Override
             public String apply(String input) {
               return wrapWithParentheses(input);
             }
           });
  }

  private static String wrapWithParentheses(String string) {
    return !Strings.isNullOrEmpty(string) && !string.startsWith("(") && !string.endsWith(")") ?
           "(" + string + ")" : "";
  }

}
