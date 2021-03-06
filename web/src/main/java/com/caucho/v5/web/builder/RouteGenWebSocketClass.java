/*
 * Copyright (c) 1998-2015 Caucho Technology -- all rights reserved
 *
 * This file is part of Baratine(TM)
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

package com.caucho.v5.web.builder;

import java.util.Objects;

import io.baratine.web.HttpMethod;
import io.baratine.web.ServiceWeb;
import io.baratine.web.ViewRender;
import io.baratine.web.WebBuilder.OutBuilder;

class RouteGenWebSocketClass implements IncludeWebAmp, OutBuilder
{
  private String _path;
  private HttpMethod _method;
  
  private Class<? extends ServiceWeb> _serviceClass;
  
  private ViewRender<?> _view;
  
  RouteGenWebSocketClass(String path,
               Class<? extends ServiceWeb> serviceClass)
  {
    Objects.requireNonNull(path);
    Objects.requireNonNull(serviceClass);
    
    _method = HttpMethod.GET;
    _path = path;
    _serviceClass = serviceClass;
  }
  
  String getMethod()
  {
    return _method.name();
  }
  
  String getPath()
  {
    return _path;
  }
  
  @Override
  public <T> OutBuilder view(ViewRender<T> view)
  {
    throw new UnsupportedOperationException(getClass().getName());
  }

  @Override
  public void build(WebBuilderAmp builder)
  {
    OutBuilder out = builder.websocket(_path).to(_serviceClass);
  }
  
  @Override
  public String toString()
  {
    return getClass().getSimpleName() + "[" + _path + "," + _serviceClass + "]";
  }
}
