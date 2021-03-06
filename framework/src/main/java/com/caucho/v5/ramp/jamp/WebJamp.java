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

package com.caucho.v5.ramp.jamp;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.caucho.v5.amp.ServicesAmp;
import com.caucho.v5.amp.message.HeadersNull;
import com.caucho.v5.amp.remote.ChannelServer;
import com.caucho.v5.amp.service.ServiceRefHandle;
import com.caucho.v5.amp.spi.OutboxAmp;
import com.caucho.v5.json.ser.JsonFactory;
import com.caucho.v5.web.webapp.RequestBaratine;

import io.baratine.service.ServiceRef;
import io.baratine.web.HttpStatus;
import io.baratine.web.RequestWeb;
import io.baratine.web.ServiceWeb;

/**
 * Jamp protocol for deployed pods.
 */
public class WebJamp implements ServiceWeb
{
  private static final Logger log
    = Logger.getLogger(WebJamp.class.getName());
  
  private static final String APPLICATION_JAMP = "x-application/jamp";
  
  private static final String APPLICATION_JAMP_RPC = "x-application/jamp-rpc";
  
  private static final String APPLICATION_JAMP_PUSH = "x-application/jamp-push";
  private static final String APPLICATION_JAMP_PULL = "x-application/jamp-pull";
  private static final String APPLICATION_JAMP_POLL = "x-application/jamp-poll";
  
  private static final String CHANNEL_COOKIE = "Jamp_Channel";
  
  private final JampPodManagerPods _podManager;
  
  private final JsonFactory _jsonFactory;
  
  private long _rpcTimeout = 60000;

  private ServicesAmp _manager;

  public WebJamp()
  {
    _manager = ServicesAmp.current();
    
    _podManager = new JampPodManagerPods();
    
    _jsonFactory = createJsonFactory();
  }
  
  @Override
  public void service(RequestWeb req)
  {
    String pathInfo = req.uri().substring("/s".length());
    
    //PodContext podContext = _podManager.getPodContext(pathInfo);
    
    String method = req.method();
    String contentType = req.header("content-type");
    
    if (contentType != null) {
      int p = contentType.indexOf(';');
      
      if (p > 0) {
        contentType = contentType.substring(0, p).trim();
      }
    }
    
    if ((APPLICATION_JAMP.equals(contentType)
        || APPLICATION_JAMP_RPC.equals(contentType))
        && "POST".equals(method)) {
      doJampRpc(req);
    }
    else {
      req.header("content-type", "text/plain");
    
      req.halt(HttpStatus.NOT_IMPLEMENTED);
    }
  }

  private void doJampRpc(RequestWeb req)
  {
    ChannelServer channel = null;
    
    //String connId = getConnectionId(req);
    
    /*
    if (connId != null) {
      channel = _sessionMap.get(connId);
    }
    */
    
    /*
    ChannelServerJampRpc channelRpc = null;
    
    channelRpc = new ChannelServerJampRpc(podContext.getAmpManager(),
                                          podContext.getLookup(),
                                          ()->getSessionId(req));
    
    if (channel instanceof ChannelServerJampRpc) {
      ChannelServerJampRpc sessionRpc = (ChannelServerJampRpc) channel;

      channelRpc.initSession(sessionRpc);
    }
    */
    ChannelServerJampNew channelRpc = null;
    
    channelRpc = new ChannelServerJampNew(req, this, _manager);
    
    try {
      //channelRpc.init(req, res);
      
      //doJampRpc(req, podContext, channelRpc);
      doJampRpcImpl(req, channelRpc);
    } catch (IOException e) {
      req.write("RouteException: " + e);
    } finally{
      //channelRpc.finish();

      ServiceRef.flushOutbox();
    }
  }
  
  private String getSessionId(RequestBaratine req)
  {
    return "zomg";
  }
  
  public void doJampRpcImpl(RequestWeb req,
                            ChannelServerJampNew channel)
    throws IOException
  {
    // xxx: jsonp
    req.header("content-type", "application/json");
    
    req.body(Reader.class, (is,e)->{
      if (e != null) {
        e.printStackTrace();
        req.fail(e);;
      }
      else {
        doJampRpcImpl(req, channel, is);
      }
    });
  }
  
  private void doJampRpcImpl(RequestWeb req,
                             ChannelServerJampNew channel,
                             Reader is)
    throws IOException
  {
    Objects.requireNonNull(is);
    
    InJamp in = new InJamp(channel, _jsonFactory); // , outbox);

    try (OutboxAmp outbox = OutboxAmp.currentOrCreate(channel.services())) {
      int queryCount = in.readMessages(is, outbox);
      
      JampRestMessage msg;
      
      if (queryCount > 0) {
        /*
        if ((msg = channel.pollMessage(_rpcTimeout, TimeUnit.MILLISECONDS)) != null) {
          pw.print("[");
          msg.write(out);
          pw.print("]");
          
          //pw.flush(); // XXX:
          
          return;
        }
        */
        //channel.pollMessage(req, out);
      }
      else {
        //outbox.flush();
        req.write("[]");
        req.ok();
      }
      
      //pw.flush();
      
      
      //outbox.flush();
      
    } catch (Exception e) {
      if (log.isLoggable(Level.FINER)) {
        log.log(Level.FINER, e.toString(), e);
        throw e;
      }
      
      req.fail(e);
      /*
      req.status(HttpStatus.INTERNAL_SERVER_ERROR);
      req.write(e.toString());
      */
    } finally {
      // OutboxThreadLocal.setCurrent(oldOutbox);
      //outbox.flush();
    }
  }

  private String getConnectionId(RequestBaratine req)
  {
    // TODO Auto-generated method stub
    return null;
  }

  void rpcOk(RequestWeb req,
             String remoteName, 
             long qid,
             Object value)
  {
    Writer pw = req.writer();
    OutJamp out = new OutJamp(_jsonFactory);
    out.init(pw);
    
    try {
      pw.write("[");
      out.reply(HeadersNull.NULL, remoteName, qid, value);
      pw.write("]");
      
      req.ok();
    } catch (IOException e) {
      req.fail(e);
    }
    // TODO Auto-generated method stub
    
  }

  void rpcFail(RequestWeb req,
               String remoteName, 
               long qid,
               Throwable exn)
  {
    Writer pw = req.writer();
    OutJamp out = new OutJamp(_jsonFactory);
    out.init(pw);
    
    try {
      pw.write("[");
      out.queryError(HeadersNull.NULL, remoteName, qid, exn);
      pw.write("]");
      
      req.ok();
    } catch (Exception e) {
      e.printStackTrace();
      req.fail(e);
    }
    // TODO Auto-generated method stub
    
  }
  
  protected JsonFactory createJsonFactory()
  {
    JsonFactory jsonFactory = new JsonFactory();
    
    jsonFactory.addSerializer(ServiceRefHandle.class,
                              new JsonSerializerServiceRef());
    
    jsonFactory.addSerializer(ServiceRef.class,
                                new JsonDeserializerServiceRef());
    
    return jsonFactory;
  }

  /*
  private void serviceImpl(HttpServletRequest req, HttpServletResponse res)
    throws IOException, ServletException
  {
    PodContext podContext = _podManager.getPodContext(req.getPathInfo());
    
    String method = req.getMethod();

    String origin = req.getHeader("Origin");

    if (origin != null && "OPTIONS".equals(method)) {
      allowAll(req, res, origin, true);

      return;
    }
    else if (origin != null) {
      allowAll(req, res, origin, false);
    }
    
    if (! podContext.isLocal()) {
      redirect(req, res);
      return;
    }

    String contentType = req.getContentType();
    
    if (contentType != null) {
      int p = contentType.indexOf(';');
      
      if (p > 0) {
        contentType = contentType.substring(0, p).trim();
      }
    }
      
    if ((APPLICATION_JAMP.equals(contentType)
        || APPLICATION_JAMP_RPC.equals(contentType))
        && "POST".equals(method)) {
      ChannelServer channel = null;
      
      String connId = getConnectionId(req);
      
      if (connId != null) {
        channel = _sessionMap.get(connId);
      }
      
      ChannelServerJampRpc channelRpc;
      
      channelRpc = new ChannelServerJampRpc(this,
                                            podContext.getAmpManager(),
                                            podContext.getLookup(),
                                            podContext.getUnparkQueue());
      
      if (channel instanceof ChannelServerJampRpc) {
        ChannelServerJampRpc sessionRpc = (ChannelServerJampRpc) channel;
        
        channelRpc.initSession(sessionRpc);
      }
      
      try {
        channelRpc.init(req, res);
        
        doServiceJampRpc(req, res, podContext, channelRpc);
      } finally{
        channelRpc.finish();

        ServiceRef.flushOutbox();
      }
    }
    else if (APPLICATION_JAMP_PUSH.equals(contentType) && "POST".equals(method)) {
      doServiceJampPush(req, res, podContext, 
                        getChannelRegistry(req, res, podContext));
    }
    else if (APPLICATION_JAMP_PULL.equals(contentType)
        && ("POST".equals(method) || "GET".equals(method))) {
      doServiceJampPull(req, res, podContext, 
                        getChannelRegistry(req, res, podContext));
    }
    else if (APPLICATION_JAMP_POLL.equals(contentType)) {
      doServiceJampPoll(req, res, podContext, 
                        getChannelRegistry(req, res, podContext));
    }
    else {
      doRestJamp(req, res, podContext);
    }
  }
  */
}
