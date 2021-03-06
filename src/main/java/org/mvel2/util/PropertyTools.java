package org.mvel2.util;

import org.mvel2.ParserContext;
import org.mvel2.compiler.PropertyVerifier;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import static java.lang.String.valueOf;
import static java.lang.reflect.Modifier.*;
import static org.mvel2.DataConversion.canConvert;
import static org.mvel2.util.ParseTools.boxPrimitive;

/** 一个基于对象,方法,字段,类型,值的工具类 */
public class PropertyTools {
  /** 判断指定对象是否是空的 */
  public static boolean isEmpty(Object o) {
    if (o != null) {
      if (o instanceof Object[]) {
        return ((Object[]) o).length == 0 ||
            (((Object[]) o).length == 1 && isEmpty(((Object[]) o)[0]));
      }
      else {
        return ("".equals(valueOf(o)))
            || "null".equals(valueOf(o))
            || (o instanceof Collection && ((Collection) o).size() == 0)
            || (o instanceof Map && ((Map) o).size() == 0);
      }
    }
    return true;
  }

  /** 获取指定属性的setter方法 */
  public static Method getSetter(Class clazz, String property) {
    property = ReflectionUtil.getSetter(property);

    for (Method method : clazz.getMethods()) {
      if ((method.getModifiers() & PUBLIC) != 0 && method.getParameterTypes().length == 1
          && property.equals(method.getName())) {
        return method;
      }
    }

    return null;
  }

  /** 获取指定类指定属性中的setter方法，并且该方法的类型是兼容的 */
  public static Method getSetter(Class clazz, String property, Class type) {
    String simple = "set" + property;
    property = ReflectionUtil.getSetter(property);

    for (Method meth : clazz.getMethods()) {
      if ((meth.getModifiers() & PUBLIC) != 0 && meth.getParameterTypes().length == 1 &&
          (property.equals(meth.getName()) || simple.equals(meth.getName()))
          && (type == null || canConvert(meth.getParameterTypes()[0], type))) {
        return meth;
      }
    }

    return null;
  }

  /** 指定字段是否是getter方法 */
  public static boolean hasGetter(Field field) {
    Method meth = getGetter(field.getDeclaringClass(), field.getName());
    return meth != null && field.getType().isAssignableFrom(meth.getReturnType());
  }

  /** 指定字段是否是setter方法 */
  public static boolean hasSetter(Field field) {
    Method meth = getSetter(field.getDeclaringClass(), field.getName());
    return meth != null && meth.getParameterTypes().length == 1 &&
        field.getType().isAssignableFrom(meth.getParameterTypes()[0]);
  }

  /** 获取指定成员的getter方法 */
  public static Method getGetter(Class clazz, String property) {
    String simple = "get" + property;
    String simpleIsGet = "is" + property;
    String isGet = ReflectionUtil.getIsGetter(property);
    String getter = ReflectionUtil.getGetter(property);

    Method candidate = null;
    for (Method meth : clazz.getMethods()) {
      if ((meth.getModifiers() & PUBLIC) != 0 && (meth.getModifiers() & STATIC) == 0 && meth.getParameterTypes().length == 0
          && (getter.equals(meth.getName()) || property.equals(meth.getName()) || ((isGet.equals(meth.getName()) || simpleIsGet.equals(meth.getName())) && meth.getReturnType() == boolean.class)
          || simple.equals(meth.getName()))) {
        if (candidate == null || candidate.getReturnType().isAssignableFrom(meth.getReturnType())) {
          candidate = meth;
        }
      }
    }
    return candidate;
  }

  /** 获取一个类中指定属性的类型信息 */
  public static Class getReturnType(Class clazz, String property, ParserContext ctx) {
    return new PropertyVerifier(property, ctx, clazz).analyze();
  }

  /** 获取指定成员公共字段或者是相应的getter访问器 */
  public static Member getFieldOrAccessor(Class clazz, String property) {
    for (Field f : clazz.getFields()) {
      if (property.equals(f.getName())) {
        if ((f.getModifiers() & PUBLIC) != 0) return f;
        break;
      }
    }
    return getGetter(clazz, property);
  }

  /** 获取一个属性的公共字段形式或相应的setter方法(即下一步会使用此成员进行赋值调用) */
  public static Member getFieldOrWriteAccessor(Class clazz, String property) {
    Field field;
    try {
      if ((field = clazz.getField(property)) != null &&
          //这一个判断不需要,因此本身class.getField就是获取公共字段
          isPublic(field.getModifiers())) {
        return field;
      }
    }
    catch (NullPointerException e) {
      return null;
    }
    catch (NoSuchFieldException e) {
      // do nothing.
    }

    return getSetter(clazz, property);
  }

  /** 获取指定类型指定属性的公共字段或相应的getter方法,并且期望能够与相应的类型相兼容 */
  public static Member getFieldOrWriteAccessor(Class clazz, String property, Class type) {
    for (Field f : clazz.getFields()) {
      if (property.equals(f.getName()) && (type == null || canConvert(f.getType(), type))) {
        return f;
      }
    }

    return getSetter(clazz, property, type);
  }

  /**
   * 判断两个对象之间是否能进行contains计算,如果能进行处理
   * 支持的类型包括字符串,集合,map,数组
   */
  public static boolean contains(Object toCompare, Object testValue) {
    if (toCompare == null)
      return false;
    else if (toCompare instanceof String)
      return ((String) toCompare).contains(valueOf(testValue));
      //    return ((String) toCompare).indexOf(valueOf(testValue)) > -1;
    else if (toCompare instanceof Collection)
      return ((Collection) toCompare).contains(testValue);
    else if (toCompare instanceof Map)
      return ((Map) toCompare).containsKey(testValue);
    else if (toCompare.getClass().isArray()) {
      for (Object o : ((Object[]) toCompare)) {
        if (testValue == null && o == null) return true;
        else if (o != null && o.equals(testValue)) return true;
      }
    }
    return false;
  }

  /** 获取基本类型的初始值 */
  public static Object getPrimitiveInitialValue(Class type) {
    if (type == int.class) {
      return 0;
    }
    else if (type == boolean.class) {
      return false;
    }
    else if (type == char.class) {
      return (char) 0;
    }
    else if (type == double.class) {
      return 0d;
    }
    else if (type == long.class) {
      return 0L;
    }
    else if (type == float.class) {
      return 0f;
    }
    else if (type == short.class) {
      return (short) 0;
    }
    else if (type == byte.class) {
      return (byte) 0;
    }
    else {
      return 0;
    }
  }

  /** 指定源是否可声明为目标类型 */
  @SuppressWarnings("unchecked")
  public static boolean isAssignable(Class to, Class from) {
    return (to.isPrimitive() ? boxPrimitive(to) : to).isAssignableFrom(from.isPrimitive() ? boxPrimitive(from) : from);
  }
}
