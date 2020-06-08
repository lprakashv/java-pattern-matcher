package io.github.lprakashv.match;

import io.github.lprakashv.constants.MatchType;
import io.github.lprakashv.match.DestructuredMatch.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class Match {

  protected abstract MatchType getMatchType();

  protected abstract Object getMatch();

  public boolean matches(Object object) {
    switch (getMatchType()) {
      case CLASS:
        return object != null && object.getClass() == ((Class<?>) getMatch());
      case DESTRUCTURED:
        if (object == null) {
          return false;
        }

        Map<String, Field> fieldMatches = (Map<String, Field>) getMatch();

        java.lang.reflect.Field[] declaredFields = object.getClass().getDeclaredFields();

        Map<String, java.lang.reflect.Field> allFieldsMap =
            Arrays.stream(declaredFields).collect(Collectors.toMap(df -> df.getName(), df -> df));

        if (fieldMatches.keySet().stream().anyMatch(k -> !allFieldsMap.containsKey(k))) {
          return false;
        }

        return Arrays.stream(declaredFields)
            .allMatch(field -> {
              field.setAccessible(true);
              if (fieldMatches.containsKey(field.getName())) {
                try {
                  return fieldMatches
                      .get(field.getName())
                      .getMatch()
                      .matches(field.get(object));
                } catch (IllegalAccessException e) {
                  //TODO - doesn't look good, fix this!
                  e.printStackTrace();
                  return false;
                }
              } else {
                return true;
              }
            });
      case PREDICATE:
        return Optional
            .ofNullable(((Function<Object, Boolean>) getMatch()).apply(object))
            .orElse(false);
      case VALUE:
        return Objects.equals(object, getMatch());
      default:
        return true; //TODO check
    }
  }
}
