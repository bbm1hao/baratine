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
 *   Free Software Foundation, Inc.
 *   59 Temple Place, Suite 330
 *   Boston, MA 02111-1307  USA
 *
 * @author Scott Ferguson
 */

package com.caucho.v5.vfs;

import java.io.IOException;

import com.caucho.v5.io.StreamImpl;

public class StreamFilter extends StreamImpl {
  protected StreamImpl next;

  public void init(StreamImpl next)
  {
    this.next = next;
  }
  
  public boolean canRead()
  {
    return next.canRead();
  }

  public int read(byte []buffer, int offset, int length) throws IOException
  {
    return next.read(buffer, offset, length);
  }

  public int getAvailable() throws IOException
  {
    return next.getAvailable();
  }

  public boolean canWrite()
  {
    return next.canWrite();
  }

  public void write(byte []buffer, int offset, int length, boolean atEnd)
    throws IOException
  {
    next.write(buffer, offset, length, atEnd);
  }

  public void flush() throws IOException
  {
    next.flush();
  }

  public void close() throws IOException
  {
    next.close();
  }
}
