/*
 * Copyright (c) 1998-2015 Caucho Technology -- all rights reserved
 *
 * This file is part of Baratine(TM)(TM)
 *
 * Each copy or derived work must preserve the copyright notice and this
 * notice unmodified.
 *
 * Baratine is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Baratine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, or any warranty
 * of NON-INFRINGEMENT.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Baratine; if not, write to the
 * 
 *   Free Software Foundation, Inc.
 *   59 Temple Place, Suite 330
 *   Boston, MA 02111-1307  USA
 *
 * @author Scott Ferguson
 */

package com.caucho.v5.convert.bean;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

import com.caucho.v5.util.L10N;

/**
 * getter/setter for bean fields.
 */
public class FieldInt<T> extends FieldBase<T>
{
  private static final L10N L = new L10N(FieldInt.class);
  
  private final MethodHandle _getter;
  private final MethodHandle _setter;

  public FieldInt(Field field)
  {
    super(field);
    
    try {
      MethodHandle getter = MethodHandles.lookup().unreflectGetter(field);
      _getter = getter.asType(MethodType.methodType(int.class, Object.class));
    
      MethodHandle setter = MethodHandles.lookup().unreflectSetter(field);
      _setter = setter.asType(MethodType.methodType(void.class, Object.class, int.class));
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }
  
  @Override
  public final int getInt(T bean)
  {
    try {
      return (int) _getter.invokeExact((Object) bean);
    } catch (Throwable e) {
      throw error(e);
    }
  }
  
  @Override
  public final void setInt(T bean, int value)
  {
    try {
      _setter.invokeExact((Object) bean, value);
    } catch (Throwable e) {
      throw error(e);
    }
  }
  
  @Override
  public final long getLong(T bean)
  {
    try {
      return (int) _getter.invokeExact((Object) bean);
    } catch (Throwable e) {
      throw error(e);
    }
  }
  
  @Override
  public final void setLong(T bean, long value)
  {
    try {
      _setter.invokeExact((Object) bean, (int) value);
    } catch (Throwable e) {
      throw error(e);
    }
  }
  
  @Override
  public final Object getObject(T bean)
  {
    return (int) getInt(bean);
  }
  
  @Override
  public final void setObject(T bean, Object value)
  {
    if (value instanceof Number) {
      setInt(bean, ((Number) value).intValue());
    }
    else if (value == null) {
    }
    else if (value instanceof String) {
      setInt(bean, Integer.parseInt((String) value));
    }
    else {
      throw error(L.l("{0} is an invalid value", value));
    }
  }
}
