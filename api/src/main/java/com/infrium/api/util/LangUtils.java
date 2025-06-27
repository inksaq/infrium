package com.infrium.api.util;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class LangUtils {

  public static <A, B> Pair<A, B> pairOf(final A a, final B b) {
    return new Pair<>(a, b);
  }

  public static <T> T[] arrayOf(final T ...vars) {
    return vars;
  }

  public static <T> List<T> listOf(final T ...vars) {
    return Arrays.asList(vars);
  }

  public static <T> List<T> syncListOf(final T ...vars) {
    return Collections.synchronizedList(listOf(vars));
  }


  public static <K extends Enum<K>, V> Map<K, V> mapOfEnum(final Class<K> k, final V defaultValue) {
    final K[] enums = k.getEnumConstants();
    return Arrays.stream(enums).collect(Collectors.toMap(type -> type, type -> defaultValue, (a, b) -> b, HashMap::new));
  }

  public static <K, V> Map<K, V> mapOf(final Pair<K, V> ...pairs) {
    HashMap<K, V> map = new HashMap<>();
    for (Pair<K, V> pair : pairs) {
      map.put(pair.a(), pair.b());
    }
    return map;
  }

  public static <K, V> Map<K, V> syncMapOf(final Pair<K, V> ...pairs) {
    return Collections.synchronizedMap(mapOf(pairs));
  }

  public static <K extends Enum<K>, V> Map<K, V> syncMapOfEnum(final Class<K> k, final V defaultValue) {
    return Collections.synchronizedMap(mapOfEnum(k, defaultValue));
  }

  public static <T> void ifNotNull(final T t, final Consumer<T> func) {
    if (t != null) func.accept(t);
  }

  public static final record Pair<A, B>(A a, B b) {}

}

