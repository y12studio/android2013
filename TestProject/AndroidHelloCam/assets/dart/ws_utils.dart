library ws_utils;
import 'dart:math' show Random;
import 'dart:html';
import 'dart:async';


class Foo{
  static Random shaker = new Random();
  String _str = "this is some private String only to myLibrary";
  String pubStr = "created instances of this class can access";
  
  int ran(){
    return shaker.nextInt(6);
  }
}


class WsHelper{
    
  StreamController _onExitController = new StreamController.broadcast();
  Stream get onExit => _onExitController.stream;
  
  StreamController _onMessageController = new StreamController.broadcast();
  Stream get onMessage => _onMessageController.stream;
  
  String dwsHost;
  String dwsPort;
  WebSocket ws;
  
  WsHelper(this.dwsHost,this.dwsPort);
  
  outputMsg(String msg){
    _onMessageController.add(msg);
  }
  
  
  initWebSocket([int retrySeconds = 2]) {
    
    var reconnectScheduled = false;

    String wsHost = window.location.hostname;
    String wsPort = window.location.port;
    
    // dart dev port
    if(wsPort=="3030"){
      wsHost = dwsHost;
      wsPort = dwsPort;
    }

    
    String uri = 'ws://${wsHost}:${wsPort}/ws';
    outputMsg("Connecting to websocket $uri");
    ws = new WebSocket(uri);

    void scheduleReconnect() {
      if (!reconnectScheduled) {
        new Timer(new Duration(milliseconds: 1000 * retrySeconds), () => initWebSocket(retrySeconds * 2));
      }
      reconnectScheduled = true;
    }

    ws.onOpen.listen((e) {
      String msg = 'Hello Websocket Test';
      outputMsg('Connected, send msg = $msg');
      ws.send(msg);
    });

    ws.onClose.listen((e) {
      outputMsg('Websocket closed, retrying in $retrySeconds seconds');
      scheduleReconnect();
    });

    ws.onError.listen((e) {
      outputMsg("Error connecting to ws");
      scheduleReconnect();
    });
    
    ws.onMessage.listen((MessageEvent e) {      
      retrySeconds = 2;
      //outputMsg('msg ${count} : is String ? = ${e.data is String}');
      if(e.data is String){
        outputMsg('resp msg = ${e.data}');
        // List rlist = JSON.decode(e.data); 
     } else {
       // e.data not String what type?
       outputMsg('msg(blob)');
     }
  });
}
  
  foo(){
    String msg = 'Test Send Msg';
    outputMsg('[Send] $msg');
    ws.send(msg);
  }
  
}