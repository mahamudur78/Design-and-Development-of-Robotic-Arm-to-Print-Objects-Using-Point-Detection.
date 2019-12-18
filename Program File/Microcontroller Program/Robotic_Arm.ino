#include <Servo.h>
#include <Stepper.h>

#define LINE_BUFFER_LENGTH 512


const int Servo_Pin_Setup = 6;

const int Stepper_Pin_Setup = 20; 

Servo penServo;  

Stepper Stepper_Y_Axis(Stepper_Pin_Setup, 2,3,4,5);            
Stepper Stepper_Y_Axis(Stepper_Pin_Setup, 8,9,10,11);  

float Xpos = X_Axis_min;
float Ypos = Y_Axis_min;
float Zpos = Z_Axis_max; 

struct point actuatorPos;

float StepInc = 1;
int StepDelay = 0;
int LineDelay = 50;
int penDelay = 50;

float Steps_Per_Millimeter_X = 13.0;
float Steps_Per_Millimeter_Y = 13.0;

float X_Axis_min = 0;
float X_Axis_max = 60;
float Y_Axis_min = 0;
float Y_Axis_max = 80;
float Z_Axis_min = 0;
float Z_Axis_max = 1;

boolean verbose = false;

const int Servo_ZUp = 80;
const int Servo_ZDown = 40;

struct point { 
  float x; 
  float y; 
  float z; 
};

void setup() {
  Serial.begin( 9600 );
  
  penServo.attach(Servo_Pin_Setup);
  penServo.write(Servo_ZUp);
  delay(200);


  Stepper_Y_Axis.setSpeed(550);
  Stepper_Y_Axis.setSpeed(550);  

}

void penUp() { 
  penServo.write(Servo_ZUp); 
  delay(LineDelay); 
  Zpos=Z_Axis_max; 
  if (verbose) { 
    Serial.println("Pen up!"); 
  } 
}

void penDown() { 
  penServo.write(Servo_ZDown); 
  delay(LineDelay); 
  Zpos=Z_Axis_min; 
  if (verbose) { 
    Serial.println("Pen down."); 
  } 
}

void processIncomingLine( char* line, int charNB ) {
  int currentIndex = 0;
  char buffer[ 64 ]; 
  struct point newPos;

  newPos.x = 0.0;
  newPos.y = 0.0;


  while( currentIndex < charNB ) {
    switch ( line[ currentIndex++ ] ) {
    case 'U':
      penUp(); 
      break;
    case 'D':
      penDown(); 
      break;
    case 'G':
      buffer[0] = line[ currentIndex++ ];  

      buffer[1] = '\0';

      switch ( atoi( buffer ) ){ 
      case 0:
      case 1:

        char* indexX = strchr( line+currentIndex, 'X' );
        char* indexY = strchr( line+currentIndex, 'Y' );
        if ( indexY <= 0 ) {
          newPos.x = atof( indexX + 1); 
          newPos.y = actuatorPos.y;
        } 
        else if ( indexX <= 0 ) {
          newPos.y = atof( indexY + 1);
          newPos.x = actuatorPos.x;
        } 
        else {
          newPos.y = atof( indexY + 1);
          indexY = '\0';
          newPos.x = atof( indexX + 1);
        }
        drawLine(newPos.x, newPos.y );

        actuatorPos.x = newPos.x;
        actuatorPos.y = newPos.y;
        break;
      }
      break;
    case 'M':
      buffer[0] = line[ currentIndex++ ];
      buffer[1] = line[ currentIndex++ ];
      buffer[2] = line[ currentIndex++ ];
      buffer[3] = '\0';
      switch ( atoi( buffer ) ){
      case 300:
        {
          char* indexS = strchr( line+currentIndex, 'S' );
          float Spos = atof( indexS + 1);

          if (Spos == 30) { 
            penDown(); 
          }
          if (Spos == 50) { 
            penUp(); 
          }
          break;
        }
      case 114: 
        Serial.print( actuatorPos.x );
        Serial.print( "  -  Y = " );
        Serial.println( actuatorPos.y );
        break;
      default:
        Serial.println( buffer );
      }
    }
  }

}

void drawLine(float x1, float y1) {

  if (verbose)
  {
    Serial.print("fx1, fy1: ");
    Serial.print(x1);
    Serial.print(",");
    Serial.print(y1);
    Serial.println("");
  }  

  if (x1 >= X_Axis_max) { 
    x1 = X_Axis_max; 
  }
  if (x1 <= X_Axis_min) { 
    x1 = X_Axis_min; 
  }
  if (y1 >= Y_Axis_max) { 
    y1 = Y_Axis_max; 
  }
  if (y1 <= Y_Axis_min) { 
    y1 = Y_Axis_min; 
  }

  if (verbose)
  {
    Serial.print("Xpos, Ypos: ");
    Serial.print(Xpos);
    Serial.print(",");
    Serial.print(Ypos);
    Serial.println("");
  }

  if (verbose)
  {
    Serial.print("x1, y1: ");
    Serial.print(x1);
    Serial.print(",");
    Serial.print(y1);
    Serial.println("");
  }

  x1 = (int)(x1*Steps_Per_Millimeter_X);
  y1 = (int)(y1*Steps_Per_Millimeter_Y);
  float x0 = Xpos;
  float y0 = Ypos;

  long dx = abs(x1-x0);
  long dy = abs(y1-y0);
  int sx = x0<x1 ? StepInc : -StepInc;
  int sy = y0<y1 ? StepInc : -StepInc;

  long i;
  long over = 0;

  if (dx > dy) {
    for (i=0; i<dx; ++i) {
      Stepper_Y_Axis.step(sx);
      over+=dy;
      if (over>=dx) {
        over-=dx;
        Stepper_Y_Axis.step(sy);
      }
      delay(StepDelay);
    }
  }
  else {
    for (i=0; i<dy; ++i) {
      Stepper_Y_Axis.step(sy);
      over+=dx;
      if (over>=dy) {
        over-=dy;
        Stepper_Y_Axis.step(sx);
      }
      delay(StepDelay);
    }    
  }
  if (verbose)
  {
    Serial.print("dx, dy:");
    Serial.print(dx);
    Serial.print(",");
    Serial.print(dy);
    Serial.println("");
  }

  if (verbose)
  {
    Serial.print("Going to (");
    Serial.print(x0);
    Serial.print(",");
    Serial.print(y0);
    Serial.println(")");
  }
  delay(LineDelay);

  Xpos = x1;
  Ypos = y1;
}
void loop() 
{
  delay(200);
  char line[ LINE_BUFFER_LENGTH ];
  char c;
  int lineIndex;
  bool lineIsComment, lineSemiColon;

  lineIndex = 0;
  lineSemiColon = false;
  lineIsComment = false;

  while (1) {

    while ( Serial.available()>0 ) {
      c = Serial.read();
      if (( c == '\n') || (c == '\r') ) {
        if ( lineIndex > 0 ) { 
          line[ lineIndex ] = '\0';
          if (verbose) { 
            Serial.print( "Received : "); 
            Serial.println( line ); 
          }
          processIncomingLine( line, lineIndex );
          lineIndex = 0;
        } 
        else { 
        }
        lineIsComment = false;
        lineSemiColon = false;
        Serial.println("Complete");    
      } 
      else {
        if ( (lineIsComment) || (lineSemiColon) ) {
          if ( c == ')' )  lineIsComment = false;
        } 
        else {
          if ( c <= ' ' ) {
          } 
          else if ( c == '/' ) { 
          } 
          else if ( c == '(' ) {
            lineIsComment = true;
          } 
          else if ( c == ';' ) {
            lineSemiColon = true;
          } 
          else if ( lineIndex >= LINE_BUFFER_LENGTH-1 ) {
            lineIsComment = false;
            lineSemiColon = false;
          } 
          else if ( c >= 'a' && c <= 'z' ) {
            line[ lineIndex++ ] = c-'a'+'A';
          } 
          else {
            line[ lineIndex++ ] = c;
          }
        }
      }
    }
  }
}




