import 'package:polymer/polymer.dart';
import 'ws_utils.dart';
import 'dart:html';

/**
 * A Polymer click counter element.
 */
@CustomTag('click-counter')
class ClickCounter extends PolymerElement {
  @published int count = 0;
  WsHelper wsh;
  TextAreaElement output;
  int outputCount  = 0 ;
  

  ClickCounter.created() : super.created() {
    
    output = shadowRoot.querySelector('#output');
    
    Foo foo = new Foo();
    print(foo.ran());
    wsh = new WsHelper("192.168.2.103","8888");
    wsh.onMessage.listen((String msg)=>textAreaOutput(msg));
    wsh.initWebSocket(6);
  }
  
  textAreaOutput(String msg){
    outputCount++;
    var text = msg;
    if (!output.text.isEmpty) {
      text = "[$outputCount] ${text}\n${output.text}";
    }
    output.text = text;
  }

  void increment() {
    count++;
    wsh.foo();
  }
}

