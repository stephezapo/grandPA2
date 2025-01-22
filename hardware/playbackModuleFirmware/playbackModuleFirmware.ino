/*
Serial data output format: ([] equals one byte)
[exec fader no.][value high byte][value low byte] fader numbers: 1..15, values 0..1023
[exec button no.][1 or 0] button numbers: 101..160, values 1 (pressed) and 0 (released)
[page button no.][1 or 0] button numbers 201..206, values 1 (pressed) and 0 (released)
*/

int analogIns[] = {A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14};
int buttonCols[] = {17, 16, 15, 14, 2, 3, 4, 5, 6, 7, 8, 9, 18, 19, 20, 21}; 
int ledCols[] = {37, 36, 35, 34, 33, 32, 31, 30, 29, 28, 27, 26, 25, 24, 23, 22}; 
int rows[] = {39, 41, 43, 45, 47, 49, 51, 53}; 
byte dataOut[256];
unsigned int index = 0;

unsigned int analogVals[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
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

void setup()
{
  Serial.begin(115200);

  for(i = 0; i<16; i++)
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

  index = 0;

  // Read Faders
  unsigned int aValue = 0;
  for(i = 0; i<15; i++)
  {
    aValue = 1023 - analogRead(analogIns[i]);
    if(aValue < 5)
    {
      aValue = 0;
    }
    if(abs(analogVals[i]-aValue)>4 || (aValue == 0 && analogVals[i]>0))
    {
      dataOut[index] = i+1;
      dataOut[index+1] = highByte(aValue);
      dataOut[index+2] = lowByte(aValue);
      index += 3;
      analogVals[i] = aValue;
    }

  }

  /*if(now-last>250)
  {
    ledCounter = (ledCounter+1) % 8;
    last = now;
  }
  
  String buttons = "";
  bool buttonState = 0;
  
  for(i = 0; i<8; i++)
  {
    digitalWrite(rows[(i+7)%8], LOW);

    for(j = 0; j<16; j++)
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

    digitalWrite(rows[i], HIGH);

    
    
    for(j = 0; j<16; j++)
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
      
      if(i==0 && j<15)
      {
        analogVals[i] = 1023 - analogRead(analogIns[i]);
      }
    }
  }*/
  
  /*for(i = 0; i<2; i++)
  {
    Serial.print(analogVals[i]);
    Serial.print(" ");
  }

  Serial.print(buttons);

  Serial.println();*/

  /*if(buttons!="")
  {
    Serial.println(buttons);
  }*/

  if(index>0)
  {
    for(i = 0; i< index; i++)
    {
      Serial.print(dataOut[i]);
      Serial.print(" ");
    }
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
