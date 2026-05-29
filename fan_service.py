import time
import paho.mqtt.client as mqtt
import os
from gpiozero import OutputDevice
from gpiozero.pins.lgpio import LGPIOFactory
from gpiozero import Device

Device.pin_factory = LGPIOFactory()

MQTT_BROKER = os.getenv("MQTT_BROKER", "localhost")
MQTT_PORT = int(os.getenv("MQTT_PORT", 1883))

fan = OutputDevice(17)
client = mqtt.Client()

def on_message(client, userdata, message):
    import json
    payload = json.loads(message.payload.decode())
    if payload.get("on"):
        fan.on()
        print("Fan turned on")
    else:
        fan.off()
        print("Fan tuned off")

def main():
    client.connect(MQTT_BROKER, MQTT_PORT)
    client.subscribe("mycloud/commands/fan")
    client.on_message = on_message
    print("fan service started, waiting for commands...")
    client.loop_forever()

if __name__ == "__main__":
    main()