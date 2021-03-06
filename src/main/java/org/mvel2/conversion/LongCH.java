/**
 * MVEL 2.0
 * Copyright (C) 2007 The Codehaus
 * Mike Brock, Dhanji Prasanna, John Graham, Mark Proctor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mvel2.conversion;

import org.mvel2.ConversionException;
import org.mvel2.ConversionHandler;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.valueOf;

/** 各种类型转长整形 */
public class LongCH implements ConversionHandler {
  private static final Map<Class, Converter> CNV =
      new HashMap<>();

  /** 自实现字符串转long型,采用parseLong来实现 */
  private static Converter stringConverter = o -> {
    if (((String) o).length() == 0) return (long) 0;

    return Long.parseLong(((String) o));
  };

  public Object convertFrom(Object in) {
    if (!CNV.containsKey(in.getClass())) throw new ConversionException("cannot convert type: "
        + in.getClass().getName() + " to: " + Long.class.getName());
    return CNV.get(in.getClass()).convert(in);
  }


  public boolean canConvertFrom(Class cls) {
    return CNV.containsKey(cls);
  }

  static {
    //字符串转,使用已实现的方式来处理
    CNV.put(String.class,
        stringConverter
    );

    //对象转,先转成字符串,再使用字符串转
    CNV.put(Object.class,
        o -> stringConverter.convert(valueOf(o))
    );

    //bigDecimal转,窄化处理
    CNV.put(BigDecimal.class,
        new Converter() {
          public Long convert(Object o) {
            return ((BigDecimal) o).longValue();
          }
        }
    );


    //bigInteger,窄化处理
    CNV.put(BigInteger.class,
        new Converter() {
          public Long convert(Object o) {
            return ((BigInteger) o).longValue();
          }
        }
    );


    //short,宽化处理
    CNV.put(Short.class,
        o -> {
          //noinspection UnnecessaryBoxing
          return ((Short) o).longValue();
        }
    );

    //long,原样返回
    CNV.put(Long.class,
        o -> {
          //noinspection UnnecessaryBoxing
          return new Long(((Long) o));
        }
    );

    //integer,宽化处理
    CNV.put(Integer.class,
        o -> {
          //noinspection UnnecessaryBoxing
          return ((Integer) o).longValue();
        }
    );

    //double,窄化处理
    CNV.put(Double.class,
        o -> ((Double) o).longValue());

    //float,窄化处理
    CNV.put(Float.class,
        o -> ((Float) o).longValue());

    //boolean,true为1,false为0
    CNV.put(Boolean.class,
        new Converter() {
          public Long convert(Object o) {
            if ((Boolean) o) return 1L;
            else return 0L;
          }
        }
    );
  }
}
