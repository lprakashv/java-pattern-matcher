package io.github.lprakashv.patternmatcher4j.match;

import io.github.lprakashv.patternmatcher4j.constants.MatchType;

public class TypeMatch extends Match {

  private final Class<?> type;

  public TypeMatch(Class<?> type) {
    this.type = type;
  }

  public static TypeMatch of(Class<?> type) {
    return new TypeMatch(type);
  }

  @Override
  protected MatchType getMatchType() {
    return MatchType.CLASS;
  }

  @Override
  protected Class<?> getMatch() {
    return type;
  }
}