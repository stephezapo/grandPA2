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
int playbackRows[] = {39, 41, 43, 49}; 
int pageRows[] = {39, 41, 43, 45, 47, 49};
byte dataOut[256];
unsigned int index = 0;

unsigned int analogVals[15] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
bool playbackButtonVals[4][15] = {{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};

bool pageButtonVals[6] = {0, 0, 0, 0, 0, 0};

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

  if(now-last>1000)
  {
    ledCounter = (ledCounter+1) % 8;
    last = now;
  }
  
  String buttons = "";
  bool buttonState = 0;
  
  for(i = 0; i<4; i++)
  {
    digitalWrite(playbackRows[(i+3)%4], LOW);
    digitalWrite(playbackRows[i], HIGH);

    /*
    // LEDs
    for(j = 0; j<15; j++)
    {
      if(ledCounter==i)
      {
        digitalWrite(ledCols[j], HIGH);
      }
      else
      {
        digitalWrite(ledCols[j], LOW);
      }
    }*/

    // Playback Buttons
    for(j = 0; j<15; j++)
    {
      buttonState = digitalRead(buttonCols[j]) == LOW;
      int buttonNr = 101 + i*15 + j;

      if(buttonState && !playbackButtonVals[i][j])
      {
        dataOut[index] = buttonNr;
        dataOut[index+1] = 1;
        index+=2;
      }
      else if(!buttonState && playbackButtonVals[i][j])
      {
        dataOut[index] = buttonNr;
        dataOut[index+1] = 0;
        index+=2;
      }

      playbackButtonVals[i][j] = buttonState;
    }
  }
  digitalWrite(playbackRows[3], LOW);

  // Page Buttons
  for(i = 0; i<6; i++)
  {
    digitalWrite(pageRows[(i+5)%6], LOW);
    digitalWrite(pageRows[i], HIGH);

    buttonState = digitalRead(buttonCols[15]) == LOW;
    int buttonNr = 161 + i;

    if(buttonState && !pageButtonVals[i])
    {
      dataOut[index] = buttonNr;
      dataOut[index+1] = 1;
      index+=2;
    }
    else if(!buttonState && pageButtonVals[i])
    {
      dataOut[index] = buttonNr;
      dataOut[index+1] = 0;
      index+=2;
    }

    pageButtonVals[i] = buttonState;
  }
  digitalWrite(pageRows[5], LOW);

  if(index>0)
  {
    Serial.write(dataOut, index);
    Serial.println();
  }
  
  delay(1);
}
