import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import g4p_controls.*; 
import java.awt.event.KeyEvent; 
import javax.swing.JOptionPane; 
import processing.serial.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class CNC_Auto_Machine extends PApplet {






Serial port = null;
String portname = null;
boolean streaming = false;
float speed = 0.001f;
String[] gcode;
int i = 0;

public void openSerialPort()
{
  if (portname == null) return;
  if (port != null) port.stop();
  
  port = new Serial(this, portname, 9600);
  
  port.bufferUntil('\n');
}

public void selectSerialPort()
{
  String result = (String) JOptionPane.showInputDialog(frame,
    "Plase Select Serial Port.", "Serial Port", JOptionPane.WARNING_MESSAGE, null,
    Serial.list(),
    0);
    
  if (result != null) {
    portname = result;
    openSerialPort();
  }
}

public void setup()
{
  
  createGUI();
  //selectSerialPort();
}

public void draw()
{
  background(0);  
  fill(255);
  int y = 24, dy = 12;
  y = height - dy;
  text("Connected Serial Port: " + portname, 12, y); y -= dy;
}

public void brows(){
    gcode = null; i = 0;
    File file = null; 
    textEvents.appendText("Loading file...");
    selectInput("Select a file to process:", "fileSelected", file);
    
}

public void reset_print(){
  streaming = false;
  port.write("M300 S50.00 (pen up) G1 X0.00 Y0.00 F3500.00" + speed + " Z0.000\n");
  
}

int x=1;
public void pause_play_print(){

  if (x == 1) {
     button_pplay.setText("Resume");
     textEvents.appendText("Push Now");
     button_pplay.setTextBold();
     x=0;
     streaming = false;
     
     
  } else{
     button_pplay.setText("Pause");
     textEvents.appendText("Resume Now");
     button_pplay.setTextBold();
     x=1;
     streaming = true;
     port.write("G1 X Y F3500.00" + speed + " Z0.000\n");
  }

}
public void btn_up(){
  //port.write("G91\nG20\nG00 X0.000 Y" + speed + " Z0.000\n");
  port.write("M300 S50.00 (pen up) G1 Y90.00 F3500.00" + speed + " Z0.000\n");
}

public void btn_down(){
 //port.write("G91\nG20\nG00 X0.000 Y-" + speed + " Z0.000\n");
 port.write("M300 S50.00 (pen up) G1 Y0.00 F3500.00" + speed + " Z0.000\n");
}

public void btn_laft(){
  //port.write("G91\nG20\nG00 X-" + speed + " Y0.000 Z0.000\n");
  port.write("M300 S50.00 (pen up) G1 X0.00 F3500.00" + speed + " Z0.000\n");
}

public void btn_right(){
  //port.write("G91\nG20\nG00 X" + speed + " Y0.000 Z0.000\n");
  port.write("M300 S50.00 (pen up) G1 X60.00 F3500.00" + speed + " Z0.000\n");
}

public void btn_center(){
  port.write("M300 S50.00 (pen up) G1 X30.00 Y45.00 F3500.00" + speed + " Z0.000\n");
}

public void fileSelected(File selection) {
  if (selection == null) {
    textEvents.appendText("Window was closed or the user hit cancel.");
  } else {
    textEvents.setText("User selected " + selection.getAbsolutePath());
    gcode = loadStrings(selection.getAbsolutePath());
    if (gcode == null) return;
    streaming = true;
    stream();
  }
}

public void stream()
{
  if (!streaming) return;
  
  while (true) {
    if (i == gcode.length) {
      streaming = false;
      return;
    }
    
    if (gcode[i].trim().length() == 0) i++;
    else break;
  }
  
  text_gcode.setText(gcode[i]);
  port.write(gcode[i] + '\n');
  i++;
}

public void serialEvent(Serial p)
{
  String s = p.readStringUntil('\n');
  println(s.trim());
  
  if (s.trim().startsWith("Complete")) stream();
  if (s.trim().startsWith("error")) stream(); // XXX: really?
}
//------------------------------------------------------------------------------------------------------
/* =========================================================
 * ====                   WARNING                        ===
 * =========================================================
 * The code in this tab has been generated from the GUI form
 * designer and care should be taken when editing this file.
 * Only add/edit code inside the event handlers i.e. only
 * use lines between the matching comment tags. e.g.

 void myBtnEvents(GButton button) { //_CODE_:button1:12356:
     // It is safe to enter your event code here  
 } //_CODE_:button1:12356:
 
 * Do not rename this tab!
 * =========================================================
 */

public void button_reset_click1(GButton source, GEvent event) { //_CODE_:button_reset:239723:
  reset_print();
  textEvents.appendText("Reset All Print.");
} //_CODE_:button_reset:239723:

public void button_up_click1(GButton source, GEvent event) { //_CODE_:button_up:892748:
  btn_up();
  textEvents.appendText("Test Printing Execution System 'Up'...");
} //_CODE_:button_up:892748:

public void button_down_click1(GButton source, GEvent event) { //_CODE_:button_down:734348:
  btn_down();
  textEvents.appendText("Test Printing Execution System 'Down'...");
} //_CODE_:button_down:734348:

public void button_laft_click1(GButton source, GEvent event) { //_CODE_:button_laft:524025:
  btn_laft();
  textEvents.appendText("Test Printing Execution System 'Laft'...");
} //_CODE_:button_laft:524025:

public void button_right_click1(GButton source, GEvent event) { //_CODE_:button_right:302493:
  btn_right();
  textEvents.appendText("Test Printing Execution System 'Right...'");
} //_CODE_:button_right:302493:

public void button_brows_click1(GButton source, GEvent event) { //_CODE_:button_brows:254493:
  brows();
 //textEvents.appendText("button5 - GButton >> GEvent." + event + " @ " + millis());
} //_CODE_:button_brows:254493:

public void serial_port_csport_click1(GButton source, GEvent event) { //_CODE_:serial_port_csport:647049:
  selectSerialPort();
  textEvents.appendText( portname + " Port Selected!");
} //_CODE_:serial_port_csport:647049:

public void textarea1_change1(GTextArea source, GEvent event) { //_CODE_:textEvents:812958:
  //println("textarea1 - GTextArea >> GEvent." + event + " @ " + millis());
} //_CODE_:textEvents:812958:

public void text_gcode_change2(GTextArea source, GEvent event) { //_CODE_:text_gcode:991540:
  //println("textarea1 - GTextArea >> GEvent." + event + " @ " + millis());
} //_CODE_:text_gcode:991540:

public void button_center_click1(GButton source, GEvent event) { //_CODE_:button_center:287060:
  textEvents.appendText("Test Printing Execution System 'Center...'");
  btn_center();
} //_CODE_:button_center:287060:

public void button_pplay_click1(GButton source, GEvent event) { //_CODE_:button_pplay:386243:
  pause_play_print();
  //textEvents.appendText("button1 - GButton >> GEvent." + event + " @ " + millis());
} //_CODE_:button_pplay:386243:

public void checkbox_z_axis_clicked1(GCheckbox source, GEvent event) { //_CODE_:checkbox_z_axis:497925:
  println("checkbox1 - GCheckbox >> GEvent." + event + " @ " + millis());
} //_CODE_:checkbox_z_axis:497925:



// Create all the GUI controls. 
// autogenerated do not edit
public void createGUI(){
  G4P.messagesEnabled(true);
  G4P.setGlobalColorScheme(GCScheme.BLUE_SCHEME);
  G4P.setCursor(ARROW);
  surface.setTitle("Robotic Arm  Driver");
  button_reset = new GButton(this, 27, 204, 100, 29);
  button_reset.setText("Reset");
  button_reset.setTextBold();
  button_reset.setLocalColorScheme(GCScheme.CYAN_SCHEME);
  button_reset.addEventHandler(this, "button_reset_click1");
  button_up = new GButton(this, 367, 144, 80, 30);
  button_up.setText("Up");
  button_up.setTextBold();
  button_up.setLocalColorScheme(GCScheme.CYAN_SCHEME);
  button_up.addEventHandler(this, "button_up_click1");
  button_down = new GButton(this, 367, 223, 80, 30);
  button_down.setText("Down");
  button_down.setTextBold();
  button_down.setLocalColorScheme(GCScheme.CYAN_SCHEME);
  button_down.addEventHandler(this, "button_down_click1");
  button_laft = new GButton(this, 281, 186, 80, 30);
  button_laft.setText("Left");
  button_laft.setTextBold();
  button_laft.setLocalColorScheme(GCScheme.CYAN_SCHEME);
  button_laft.addEventHandler(this, "button_laft_click1");
  button_right = new GButton(this, 458, 186, 80, 30);
  button_right.setText("Right");
  button_right.setTextBold();
  button_right.setLocalColorScheme(GCScheme.CYAN_SCHEME);
  button_right.addEventHandler(this, "button_right_click1");
  button_brows = new GButton(this, 140, 164, 100, 30);
  button_brows.setText("Brows..");
  button_brows.setTextBold();
  button_brows.setLocalColorScheme(GCScheme.CYAN_SCHEME);
  button_brows.addEventHandler(this, "button_brows_click1");
  serial_port_csport = new GButton(this, 29, 165, 100, 30);
  serial_port_csport.setText("Serial Port");
  serial_port_csport.setTextBold();
  serial_port_csport.setLocalColorScheme(GCScheme.CYAN_SCHEME);
  serial_port_csport.addEventHandler(this, "serial_port_csport_click1");
  textEvents = new GTextArea(this, 12, 8, 539, 70, G4P.SCROLLBARS_VERTICAL_ONLY);
  textEvents.setText("World University of Bangladesh.");
  textEvents.setOpaque(true);
  textEvents.addEventHandler(this, "textarea1_change1");
  text_gcode = new GTextArea(this, 123, 85, 427, 40, G4P.SCROLLBARS_VERTICAL_ONLY);
  text_gcode.setOpaque(true);
  text_gcode.addEventHandler(this, "text_gcode_change2");
  label1 = new GLabel(this, 12, 88, 104, 25);
  label1.setText("Read G-Code:");
  label1.setTextBold();
  label1.setLocalColorScheme(GCScheme.SCHEME_8);
  label1.setOpaque(false);
  button_center = new GButton(this, 367, 184, 80, 30);
  button_center.setText("Center");
  button_center.setTextBold();
  button_center.setLocalColorScheme(GCScheme.CYAN_SCHEME);
  button_center.addEventHandler(this, "button_center_click1");
  button_pplay = new GButton(this, 141, 204, 100, 30);
  button_pplay.setText("Pause");
  button_pplay.setTextBold();
  button_pplay.setLocalColorScheme(GCScheme.CYAN_SCHEME);
  button_pplay.addEventHandler(this, "button_pplay_click1");
  checkbox_z_axis = new GCheckbox(this, 188, 244, 177, 20);
  checkbox_z_axis.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
  checkbox_z_axis.setText("Z Axis UP/Down");
  checkbox_z_axis.setTextBold();
  checkbox_z_axis.setLocalColorScheme(GCScheme.SCHEME_9);
  checkbox_z_axis.setOpaque(false);
  checkbox_z_axis.addEventHandler(this, "checkbox_z_axis_clicked1");
}

// Variable declarations 
// autogenerated do not edit
GButton button_reset; 
GButton button_up; 
GButton button_down; 
GButton button_laft; 
GButton button_right; 
GButton button_brows; 
GButton serial_port_csport; 
GTextArea textEvents; 
GTextArea text_gcode; 
GLabel label1; 
GButton button_center; 
GButton button_pplay; 
GCheckbox checkbox_z_axis; 
  public void settings() {  size(565, 270); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "CNC_Auto_Machine" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
