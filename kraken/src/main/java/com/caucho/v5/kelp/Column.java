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

package com.caucho.v5.kelp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.caucho.v5.io.IoUtil;
import com.caucho.v5.io.ReadBuffer;
import com.caucho.v5.io.WriteBuffer;
import com.caucho.v5.util.Crc64;

import io.baratine.db.BlobReader;

/**
 * A column for the log store.
 */
abstract public class Column {
  private final int _index;
  private final String _name;
  private final ColumnType _type;
  private final int _offset;
  
  protected Column(int index,
                       String name, 
                       ColumnType type,
                       int offset)
  {
    _index = index;
    _name = name;
    _type = type;
    _offset = offset;
  }
  
  public final int getIndex()
  {
    return _index;
  }
  
  public final String name()
  {
    return _name;
  }

  public ColumnType type()
  {
    return _type;
  }
  
  public int size()
  {
    return 0;
  }
  
  public final int getOffset()
  {
    return _offset;
  }
  
  abstract public int length();
  
  //
  // lifecycle
  //
  
  void init(DatabaseKelp db)
  {
  }
  
  //
  // operations
  //

  public int getInt(byte[] data, int i)
  {
    throw new UnsupportedOperationException(getClass().getName());
  }

  public void setInt(byte[] data, int i, int value)
  {
    throw new UnsupportedOperationException(getClass().getName());
  }

  public void setLong(byte[] data, int i, long value)
  {
    throw new UnsupportedOperationException(getClass().getName());
  }

  public long getLong(byte[] data, int i)
  {
    throw new UnsupportedOperationException(getClass().getName());
  }

  public void setDouble(byte[] data, int i, double value)
  {
    throw new UnsupportedOperationException(getClass().getName());
  }

  public double getDouble(byte[] data, int i)
  {
    throw new UnsupportedOperationException(getClass().getName());
  }

  public void setFloat(byte[] data, int i, float value)
  {
    throw new UnsupportedOperationException(getClass().getName());
  }

  public float getFloat(byte[] data, int i)
  {
    throw new UnsupportedOperationException(getClass().getName());
  }

  public void setBytes(byte[] data, int i, byte[] buffer, int offset)
  {
  }

  public void getBytes(byte[] data, int i, byte[] buffer, int offset)
  {
  }

  public void buildHash(StringBuilder sb, byte[] buffer, int offset)
  {
  }
  
  public long crc64(long crc64)
  {
    crc64 = Crc64.generate(crc64, type().name());
    crc64 = Crc64.generate(crc64, name());
    crc64 = Crc64.generate(crc64, getOffset());
    crc64 = Crc64.generate(crc64, length());
    
    return crc64;
  }
  
  //
  // blobs
  //
  
  public OutputStream openOutputStream(RowCursor cursor)
  {
    throw new UnsupportedOperationException(getClass().getName());
  }

  public InputStream openInputStream(RowCursor cursor,
                                     byte []rowBuffer,
                                     int rowOffset,
                                     byte []pageBuffer)
  {
    throw new UnsupportedOperationException(getClass().getName());
  }

  public BlobReader openBlobReader(RowCursor cursor,
                                    byte []rowBuffer,
                                    int rowOffset,
                                    byte []pageBuffer)
  {
    throw new UnsupportedOperationException(getClass().getName());
  }
  
  public void setString(RowCursor cursor, String value)
  {
    throw new UnsupportedOperationException(toString());
  }

  public void remove(PageServiceImpl pageActor, byte[] buffer, int rowOffset)
  {
  }

  void freeBlocks(byte []rowBuffer, int rowOffset)
  {
  }

  int insertBlob(byte[] sourceBuffer, int sourceRowOffset,
                 byte[] targetBuffer, int targetRowOffset, 
                 int targetBlobTail)
  {
    return targetBlobTail;
  }
  
  void setBlob(byte []buffer, int rowFirst,
               int blobOffset, int blobLength)
  {
    throw new UnsupportedOperationException(getClass().getName());
  }
  
  //
  // journal persistence
  //

  void writeJournal(OutputStream os, byte[] buffer, int offset,
                    BlobOutputStream blob)
    throws IOException
  {
    os.write(buffer, offset + getOffset(), length());
  }

  void readJournal(PageServiceImpl pageActor, 
                   ReadBuffer is, 
                   byte[] buffer, int offset, RowCursor cursor)
    throws IOException
  {
    is.readAll(buffer, offset + getOffset(), length());
  }

  void readStream(InputStream is, 
                  byte[] buffer, int offset, RowCursor cursor)
    throws IOException
  {
    IoUtil.readAll(is, buffer, offset + getOffset(), length());
  }

  void readStreamBlob(InputStream is, 
                      byte[] buffer, int offset, RowCursor cursor)
    throws IOException
  {
  }
  
  protected void readAll(InputStream is,
                        byte []buffer, int offset, int length)
    throws IOException
  {
    while (length > 0) {
      int sublen = is.read(buffer, offset, length);
      
      if (sublen <= 0) {
        return;
      }
      
      offset += sublen;
      length -= sublen;
    }
  }

  void writeStream(OutputStream os, byte[] buffer, int offset)
    throws IOException
  {
    os.write(buffer, offset + getOffset(), length());
  }

  void writeStreamBlob(OutputStream os, byte[] buffer, int offset,
                       byte[] blobBuffer,
                       PageServiceImpl tableService)
    throws IOException
  {
  }

  public long getLength(long length, byte[] buffer, int rowOffset,
                        PageServiceImpl pageService)
  {
    return length + length();
  }
  
  //
  // checkpoint persistence
  //

  void writeCheckpoint(WriteBuffer os, byte[] buffer, int offset)
    throws IOException
  {
    os.write(buffer, offset + getOffset(), length());
  }
  
  int readCheckpoint(ReadBuffer is, 
                     byte[] buffer, int rowFirst, int rowLength,
                     int blobTail)
    throws IOException
  {
    is.readAll(buffer, rowFirst + getOffset(), length());

    return blobTail;
  }
  
  //
  // validation
  //

  /**
   * Validates the column, checking for corruption.
   */
  public void validate(byte[] buffer, int rowOffset, int rowHead, int blobTail)
  {
  }
  
  @Override
  public String toString()
  {
    return getClass().getSimpleName() + "[" + name() + ",offset=" + getOffset() + "]";
  }
  
  public enum ColumnType {
    STATE,
    KEY_START, // used for serialization
    KEY_END,
    INT8,
    INT16,
    INT32,
    INT64,
    FLOAT,
    DOUBLE,
    BYTES,
    
    BLOB { // variable length data
      @Override
      public boolean isBlob() { return true; }
    },
    STRING {
      @Override
      public boolean isBlob() { return true; }
    },
    OBJECT {
      @Override
      public boolean isBlob() { return true; }
    },
    ; 
    
    public boolean isBlob()
    {
      return false;
    }
  }
}
