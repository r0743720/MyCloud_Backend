import time
import smbus2
import bme280
import paho.mqtt.client as mqtt
import json
import os

MQTT_BROKER = os.getenv("MQTT_BROKER", "localhost")
MQTT_PORT = int(os.getenv("MQTT_PORT", 1883))
BME280_ADDRESS = 0x76
READ_INTERVAL = 30

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

bus = smbus2.SMBus(1)
calibration_params = bme280.load_calibration_params(bus, BME280_ADDRESS)

def read_bme280():
    data = bme280.sample(bus, BME280_ADDRESS, calibration_params)
    return {
        "temperature":round(data.temperature, 2),
        "humidity": round(data.humidity,2),
        "pressure": round(data.pressure,2)
        }

def main():
    connect_mqtt()
    client.loop_start()
    
    print("Sensor service started. Publishing every 30 seconds...")
    
    while True:
        try:
            payload = read_bme280()
            client.publish(
                "mycloud/sensors/bme280",
                json.dumps(payload),
                qos=1
            )
            print(f"Published: {payload}")
        except Exception as e:
            print(f"Error reading BME280: {e}")
        
        time.sleep(READ_INTERVAL)

if __name__=="__main__":
    main()
            
            
            
