import g4p_controls.*; //<>//
import java.awt.event.KeyEvent; //<>//
import javax.swing.JOptionPane;
import processing.serial.*;

Serial port = null;
String portname = null;
boolean streaming = false;
float speed = 0.001;
String[] gcode;
int i = 0;

void openSerialPort()
{
  if (portname == null) return;
  if (port != null) port.stop();
  
  port = new Serial(this, portname, 9600);
  
  port.bufferUntil('\n');
}

void selectSerialPort()
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

void setup()
{
  size(565, 270);
  createGUI();
  //selectSerialPort();
}

void draw()
{
  background(0);  
  fill(255);
  int y = 24, dy = 12;
  y = height - dy;
  text("Connected Serial Port: " + portname, 12, y); y -= dy;
}

void brows(){
    gcode = null; i = 0;
    File file = null; 
    textEvents.appendText("Loading file...");
    selectInput("Select a file to process:", "fileSelected", file);
    
}

void reset_print(){
  streaming = false;
  port.write("M300 S50.00 (pen up) G1 X0.00 Y0.00 F3500.00" + speed + " Z0.000\n");
  
}

int x=1;
void pause_play_print(){

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
void btn_up(){
  //port.write("G91\nG20\nG00 X0.000 Y" + speed + " Z0.000\n");
  port.write("M300 S50.00 (pen up) G1 Y90.00 F3500.00" + speed + " Z0.000\n");
}

void btn_down(){
 //port.write("G91\nG20\nG00 X0.000 Y-" + speed + " Z0.000\n");
 port.write("M300 S50.00 (pen up) G1 Y0.00 F3500.00" + speed + " Z0.000\n");
}

void btn_laft(){
  //port.write("G91\nG20\nG00 X-" + speed + " Y0.000 Z0.000\n");
  port.write("M300 S50.00 (pen up) G1 X0.00 F3500.00" + speed + " Z0.000\n");
}

void btn_right(){
  //port.write("G91\nG20\nG00 X" + speed + " Y0.000 Z0.000\n");
  port.write("M300 S50.00 (pen up) G1 X60.00 F3500.00" + speed + " Z0.000\n");
}

void btn_center(){
  port.write("M300 S50.00 (pen up) G1 X30.00 Y45.00 F3500.00" + speed + " Z0.000\n");
}

void fileSelected(File selection) {
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

void stream()
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

void serialEvent(Serial p)
{
  String s = p.readStringUntil('\n');
  println(s.trim());
  
  if (s.trim().startsWith("Complete")) stream();
  if (s.trim().startsWith("error")) stream(); // XXX: really?
}
//------------------------------------------------------------------------------------------------------
