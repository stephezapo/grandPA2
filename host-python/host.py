import serial

# parse the data
def parse(data):
    if len(data)>0:
            length = len(data)-2 # remove the 2 newline chars
            i = 0
            while i <length:
                item = data[i]
                # check type
                if item < 16: # fader
                    faderValue = data[i+1] << 8 | data[i+2]
                    print("Fader " + str(item) + ": " + str(faderValue))
                    i += 3
                elif item > 100: # button
                    down = data[i+1]
                    print("Button " + str(item) + (" DOWN" if down==1 else " UP"))
                    i += 2 
    #print()

with serial.Serial('COM5', 115200, timeout=1) as ser:
    while(True):
        line = ser.readline()   # read a '\n' terminated line
        parse(line)