import serial
from pythonosc import udp_client
import argparse
from pynput.keyboard import Key, Controller

# parse the data
def parse(data, osc, keyboard):
    if len(data)>0:
            length = len(data)-2 # remove the 2 newline chars
            i = 0
            while i <length:
                item = data[i]
                # check type
                if item < 16: # fader
                    faderValue = data[i+1] << 8 | data[i+2]
                    print("Fader " + str(item) + ": " + str(faderValue))
                    osc.send_message("/grandpa/Fader" + str(200+item), faderValue/10.23)
                    i += 3
                elif item > 100: # button
                    down = data[i+1] == 1
                    print("Button " + str(item) + (" DOWN" if down == 1 else " UP"))
                    keyNumber = item
                    if item > 160: # page buttons
                        match item:
                            case 161:
                                if down:
                                    keyboard.press(Key.f12)
                                else:
                                    keyboard.release(Key.f12)
                            case 162:
                                break
                            case 163:
                                break
                            case 164:
                                break
                            case 165:
                                break
                            case 166:
                                break
                    else:
                        if item > 145:
                            keyNumber =- 45
                        elif item > 130:
                            keyNumber += (100 - 30)
                        elif item > 115:
                            keyNumber += (200 - 15)
                        else:
                            keyNumber += 300
                        osc.send_message("/grandpa/Key" + str(keyNumber), 1 if down else 0)
                    i += 2 
    #print()


parser = argparse.ArgumentParser()
parser.add_argument("--ip", default="127.0.0.1", help="The ip of the OSC server")
parser.add_argument("--port", type=int, default=8001, help="The port the OSC server is listening on")
args = parser.parse_args()
print("Starting OSC client at " + args.ip + ":" + str(args.port))

client = udp_client.SimpleUDPClient(args.ip, args.port)

keyboard = Controller()

with serial.Serial('COM5', 115200, timeout=1) as ser:
    while(True):
        line = ser.readline()   # read a '\n' terminated line
        parse(line, client, keyboard)