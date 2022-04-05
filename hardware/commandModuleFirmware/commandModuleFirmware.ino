/*
FIRMWARE FOR grandPA2 COMMAND WING (HARDWARE V 1.0)
*/

int analogIns[] = {A1, A0, A2}; // Fader A, Fader B, Grand Master
int buttonCols[] = {28, 30, 32, 35, 36, 38, 40, 42, 44, 46}; 
int ledCols[] = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2}; 
int rows[] = {53, 51, 49, 47, 45, 43, 41, 39}; 

int analogVals[] = {0, 0, 0};
int oldAnalogVals[] = {0, 0, 0};
bool buttonVals[8][16] = {{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                         {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                         {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                         {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                         {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                         {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                         {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                         {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};

int i = 0;
int j = 0;
unsigned long now = 0;
unsigned long last = 0;
int ledCounter = 0;
int FADER_THRESHOLD = 2; //minimum difference to be detected as fader value change

void setup()
{
  Serial.begin(115200);

  for(i = 0; i<10; i++)
  {
    pinMode(buttonCols[i], INPUT_PULLUP);
    pinMode(ledCols[i], OUTPUT);
    digitalWrite(ledCols[i], LOW);
  }

  for(i = 0; i<8; i++)
  {
    pinMode(rows[i], OUTPUT);
    digitalWrite(rows[i], LOW);
  }
}

void loop()
{
  now = millis();

  if(now-last>1000)
  {
    ledCounter = (ledCounter+1) % 8;
    last = now;
  }
  
  String buttons = "";
  bool buttonState = 0;
  
  for(i = 0; i<8; i++)
  {
    digitalWrite(rows[(i+7)%8], LOW); // deactivate previous row

    for(j = 0; j<10; j++)
    {
      //LEDS
      if(ledCounter==i)
      {
        digitalWrite(ledCols[j], HIGH);
      }
      else
      {
        digitalWrite(ledCols[j], LOW);
      }
    }

    digitalWrite(rows[i], HIGH); // activate current row
    
    for(j = 0; j<10; j++)
    {
      buttonState = digitalRead(buttonCols[j]) == HIGH;

      if(buttonState && !buttonVals[i][j])
      {
        buttons += getButtonID(i, j, true) + " ";
      }
      else if(!buttonState && buttonVals[i][j])
      {
        buttons += getButtonID(i, j, false) + " ";
      }

      buttonVals[i][j] = buttonState;
    }
  }

  for(i = 0; i<3; i++)
  {
    analogVals[i] = 1023 - analogRead(analogIns[i]);
  }


  bool change = false;

  for(i = 0; i<3; i++)
  {
    if(abs(analogVals[i] - oldAnalogVals[i])>=FADER_THRESHOLD)
    {
      change = true;
      Serial.print(analogVals[i]);
      Serial.print(" ");
    }
    
    oldAnalogVals[i] = analogVals[i];
  }

  if(buttons!="")
  {
    change = true;
    Serial.print(buttons);
  }

  if(change)
  {
    Serial.println();
  }
  
  delay(1);
}

String getButtonID(int row, int col, bool up)
{
  String s = "";
  row += 1;
  col += 1;

  s += row;

  if(col<10)
  {
    s+= "0";
  }

  s += col;

  if(up)
  {
    s += "-";
  }

  return s;
}
