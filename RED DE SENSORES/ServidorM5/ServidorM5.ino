#include <ArduinoMqttClient.h>
#include <DHT.h>
#include <M5Stack.h>
#include <TimeLib.h>
#include <SPI.h>
#include <MFRC522.h>
#include <SD.h>
#include "WiFi.h"
#include "ClaseMagnetico.h"

#define BLANCO 0XFFFF
#define NEGRO 0

//Sensor Temperatura/Humedad
#define DHTPIN 17
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);
String temperatura = "";
String temperaturaAux = "";
String humedad = "";
String humedadAux = "";

//Sensor RFID
String ID = "";
int id;
String IDAux = "";
const int RST_PIN = 5;          // Pin 9 para el reset del RC522
const int SS_PIN = 21;            // Pin 21 para el SS (SDA) del RC522
MFRC522 mfrc522(SS_PIN, RST_PIN);   // Crear instancia del MFRC522

//--Clase magnetico
ClaseMagnetico magn = ClaseMagnetico(2);
String magneticoAux = "";
String magnetico = "";

//--WIFI
char ssid[] = "VINOTECH";
char pass[] = "81903894";

//--MQTT
WiFiClient wifiClient;
MqttClient mqttClient(wifiClient);
const char broker[] = "test.mosquitto.org";
int        port     = 1883;
const char topic[] = "equipo3/VINOTECH/#";
const long interval = 1000;
unsigned long previousMillis = 0;

void setup() {
  Serial.begin(115200);
  while (!Serial) {
    ; // wait for serial port to connect. Needed for native USB port only
  }
  M5.begin();
  M5.Lcd.setTextSize(2.5); //tamaño de texto
  pinMode(2, INPUT_PULLUP); //pin dos como pull-up
  SPI.begin();         //Función que inicializa SPI
  mfrc522.PCD_Init();     //Función  que inicializa RFID
  dht.begin();      //Función que inicializa DHT
  //CONEXION A WIFI Y A BROKER
  while (WiFi.begin(ssid, pass) != WL_CONNECTED) {
    // failed, retry
    Serial.print(".");
    delay(5000);
  }
  if (!mqttClient.connect(broker, port)) {
    Serial.print("MQTT connection failed! Error code = ");
    Serial.println(mqttClient.connectError());

    while (1);
  }
  // set the message receive callback
  // subscribe to a topic
  mqttClient.subscribe(topic);
}

void loop() {
  mqttClient.poll();
  unsigned long currentMillis = millis();

  //MEDIR SENSOR MAGNETICO
  boolean difMagn = false;
  magneticoAux = magnetico;
  magnetico = magn.sensorPuerta();
  if (!magnetico.equals(magneticoAux)) {
    difMagn = true;
  }

  //MEDIR SENSOR RFID
  boolean difID = false;
  IDAux = ID;
  if (mfrc522.PICC_IsNewCardPresent())
  {
    if (mfrc522.PICC_ReadCardSerial())
    {
      id = 0;
      printArray(mfrc522.uid.uidByte, mfrc522.uid.size);
      if (id == 715) {
        ID = "Vino Rosado";
      }
      if (id == 719) {
        ID = "Vino Blanco";
      }
      if (id == 723) {
        ID = "Vino Tinto";
      }
      if (id == 727) {
        ID = "Cava";
      }
      mfrc522.PICC_HaltA();
    }
  }
  if (!ID.equals(IDAux)) {
    difID = true;
  }

  //--MEDIR SENSOR TEMPERATURA/HUMEDAD
  boolean difTemp = false;
  boolean difHum = false;

  //Humedad relativa
  humedadAux = humedad;
  float hum = dht.readHumidity();
  humedad = String(hum);

  //Temperatura en centígrados
  temperaturaAux = temperatura;
  float temp = dht.readTemperature();
  temperatura = String(temp);

  // Comprobamos si ha habido algún error en la lectura
  if (isnan(hum) || isnan(temp)) {
    Serial.println("Error obteniendo los datos del sensor DHT11");
    return;
  }
  if (!temperatura.equals(temperaturaAux)) {
    difTemp = true;
  }
  if (!humedad.equals(humedadAux)) {
    difHum = true;
  }


  //MOSTRAR POR PANTALLA EN M5STACK
  M5.Lcd.setCursor(0, 10); //posicion del texto
  M5.Lcd.setTextColor(BLANCO); //color del texto
  M5.Lcd.println("Magnetico: " + magnetico); //muestra el resultado del sensor
  M5.Lcd.println("ID: " + ID); //muestra el resultado del sensor
  M5.Lcd.println("Temperatura: " + temperatura); //muestra el resultado del sensor
  M5.Lcd.println("Humedad: " + humedad); //muestra el resultado del sensor
  delay(3000);
  M5.Lcd.fillScreen(NEGRO); //borra la pantalla

  //--Envio de datos a MQTT
  if (difMagn) {
    if (currentMillis - previousMillis >= interval) {
      // save the last time a message was sent
      previousMillis = currentMillis;
      // send message, the Print interface can be used to set the message contents
      mqttClient.beginMessage("equipo3/VINOTECH/SensorMagnetico");
      mqttClient.print(magnetico);
      mqttClient.endMessage();
    }
  }
  if (difID) {
    if (currentMillis - previousMillis >= interval) {
      // save the last time a message was sent
      previousMillis = currentMillis;
      // send message, the Print interface can be used to set the message contents
      mqttClient.beginMessage("equipo3/VINOTECH/SensorID");
      mqttClient.print(ID);
      mqttClient.endMessage();
    }
  }
  if (difTemp) {
    if (currentMillis - previousMillis >= interval) {
      // save the last time a message was sent
      previousMillis = currentMillis;
      // send message, the Print interface can be used to set the message contents
      mqttClient.beginMessage("equipo3/VINOTECH/SensorTemperatura");
      mqttClient.print(temperatura);
      mqttClient.endMessage();
    }
  }
  if (difHum) {
    if (currentMillis - previousMillis >= interval) {
      // save the last time a message was sent
      previousMillis = currentMillis;
      // send message, the Print interface can be used to set the message contents
      mqttClient.beginMessage("equipo3/VINOTECH/SensorHumedad");
      mqttClient.print(humedad);
      mqttClient.endMessage();
    }
  }
  //Envío de datos a la Raspberry UART
  if (Serial.available() > 0) {
    char command = (char) Serial.read();
    switch (command) {
      case 'M': //Si recibe M se envia el sensor magnetico
        Serial.println(magnetico);
        break;
      case 'I': //Si recibe I se envia la ID
        Serial.println(ID);
        break;
      case 'H': //Si recibe H se envia la humedad
        Serial.println(humedad);
        break;
    }
  }
}
//--Funciones
void printArray(byte *buffer, byte bufferSize) {
  for (byte i = 0; i < bufferSize; i++) {
    id = id + buffer[i];
  }
}



