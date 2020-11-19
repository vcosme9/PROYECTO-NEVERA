//#include <DHT.h>
#include <M5Stack.h>
#include <TimeLib.h>
#include <SPI.h>
#include <MFRC522.h>
#include <SD.h>

#include "ClaseMagnetico.h"

#define BLANCO 0XFFFF
#define NEGRO 0
/*
  //Sensor Temperatura/Humedad
  #define DHTPIN G2
  #define DHTTYPE DHT11
  DHT dht(DHTPIN, DHTTYPE);
*/
//Sensor RFID
String ID = "";
int id;
const int RST_PIN = 5;            // Pin 9 para el reset del RC522
const int SS_PIN = 21;            // Pin 21 para el SS (SDA) del RC522
MFRC522 mfrc522(SS_PIN, RST_PIN);   // Crear instancia del MFRC522

//Clase magnetico
ClaseMagnetico magn = ClaseMagnetico(2);

void setup() {
  Serial.begin(115200);
  M5.begin();
  M5.Lcd.setTextSize(2.5); //tamaño de texto
  pinMode(2, INPUT_PULLUP); //pin dos como pull-up
  SPI.begin();         //Función que inicializa SPI
  mfrc522.PCD_Init();     //Función  que inicializa RFID
  //dht.begin();
}

void loop() {
  //MEDIR SENSOR MAGNETICO
  String magnetico = magn.sensorPuerta();

  //MEDIR SENSOR RFID
  if (mfrc522.PICC_IsNewCardPresent())
  {
    if (mfrc522.PICC_ReadCardSerial())
    {
      id = 0;
      printArray(mfrc522.uid.uidByte, mfrc522.uid.size);
      //ID = String(id);
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
        ID = "Vino Champagne";
      }
      mfrc522.PICC_HaltA();
    }
  }
  /*
    //MEDIR SENSOR TEMPERATURA/HUMEDAD
    // Leemos la humedad relativa
    float h = dht.readHumidity();
    // Leemos la temperatura en grados centígrados (por defecto)
    float t = dht.readTemperature();
    // Leemos la temperatura en grados Fahreheit
    float f = dht.readTemperature(true);
    // Comprobamos si ha habido algún error en la lectura
    if (isnan(h) || isnan(t) || isnan(f)) {
      Serial.println("Error obteniendo los datos del sensor DHT11");
      return;
    }
     // Calcular el índice de calor en Fahreheit
    float hif = dht.computeHeatIndex(f, h);
    // Calcular el índice de calor en grados centígrados
    float hic = dht.computeHeatIndex(t, h, false);
    String temp = String(t);
  */
  //MOSTRAR POR PANTALLA EN M5STACK
  M5.Lcd.setCursor(0, 10); //posicion del texto
  M5.Lcd.setTextColor(BLANCO); //color del texto
  M5.Lcd.println("Magnetico: " + magnetico); //muestra el resultado del sensor
  M5.Lcd.println("ID: " + ID); //muestra el resultado del sensor
  //M5.Lcd.println("Temperatura: " + temp); //muestra el resultado del sensor
  delay(1000);
  M5.Lcd.fillScreen(NEGRO); //borra la pantalla

  //Envío de datos a la Raspberry
  if (Serial.available() > 0) {
    char command = (char) Serial.read();
    switch (command) {
      case 'M': //Si recibe M se envia el sensor magnetico
        Serial.println(magnetico);
        break;
      case 'I': //Si recibe I se envia la ID
        Serial.println(ID);
        break;
    }
  }
}
void printArray(byte *buffer, byte bufferSize) {
  for (byte i = 0; i < bufferSize; i++) {
    id = id + buffer[i];
  }
}

