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

package io.baratine.web;

import io.baratine.io.Buffer;
import io.baratine.pipe.Pipe;

import java.io.OutputStream;
import java.io.Writer;

public interface OutWeb<T>
{
  OutWeb<T> push(Pipe<Buffer> out);
  
  OutWeb<T> write(Buffer buffer);
  OutWeb<T> write(byte []buffer, int offset, int length);
  
  OutWeb<T> write(String value);
  OutWeb<T> write(String value, String enc);
  OutWeb<T> write(char []buffer, int offset, int length);
  OutWeb<T> write(char []buffer, int offset, int length, String enc);
  
  OutWeb<T> flush();
  
  Writer writer();
  Writer writer(String enc);
  
  OutputStream output();
  
  void halt();
  void halt(HttpStatus status);
  
  //void fallthru();
}
