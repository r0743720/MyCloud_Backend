import time
import paho.mqtt.client as mqtt
import json
import os
from mpu6050 import mpu6050

MQTT_BROKER = os.getenv("MQTT_BROKER", "localhost")
MQTT_PORT = int(os.getenv("MQTT_PORT", 1883))
READ_INTERVAL = 0.5
MOVEMENT_THRESHOLD = 12
TRAVEL_MODE = False

sensor = mpu6050(0x68)

client = mqtt.Client()

def connect_mqtt():
    while True:
        try:
            client.connect(MQTT_BROKER, MQTT_PORT)
            print(f"Connected to MQTT broker at {MQTT_BROKER}:{MQTT_PORT}")
            break
        except Exception as e:
            print(f"MQTT connection failed: {e}. Retrying in 5 seconds...")
            time.sleep(5)
    
def on_message(client, userdata, message):
    """Listen for travel mode toggle commands from spribnt boot"""
    global TRAVEL_MODE
    payload = json.loads(message.payload.decode())
    TRAVEL_MODE = payload.get("enabled", False)
    print(f"Travel mode {'enabled' if TRAVEL_MODE else 'disabled'}")
    
def read_accelerometer():
    """Returns real acceleration values in g-force."""
    accel = sensor.get_accel_data()
    return {
        "x": round(accel["x"], 3),
        "y": round(accel["y"], 3),
        "z": round(accel["z"], 3)
    }

def detect_movement(accel):
    """returns True if total acceleration exceeds threshold."""
    total = (accel["x"]**2 + accel["y"]**2 + accel["z"]**2) ** 0.5
    return total > MOVEMENT_THRESHOLD

def main():
    connect_mqtt()
    
    client.subscribe("mycloud/commands/travel-mode")
    client.on_message = on_message
    client.loop_start()
    
    print("MPU-6050 service started. Monitoring for movement...")
    
    while True:
        try:
            accel = read_accelerometer()
            
            if detect_movement(accel) and not TRAVEL_MODE:
                payload = {
                    "alert": "movement_detected",
                    "acceleration": accel
                }
                client.publish(
                    "mycloud/alerts/movement",
                    json.dumps(payload),
                    qos=1
                )
                print(f"Movement alert published: {payload}")
            else:
                print(f"Acceleration: {accel} - no alert")
        except Exception as e:
            print(f"Error reading accelerometer: {e}")
        
        time.sleep(READ_INTERVAL)

if __name__ == "__main__":
    main()