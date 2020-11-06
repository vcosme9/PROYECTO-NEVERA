#include <M5Stack.h>
#include <TimeLib.h>
#include <SPI.h>
#include <MFRC522.h>
#include <SD.h>

#include "ClaseMagnetico.h"
#include "ClaseRFID.h"

#define BLANCO 0XFFFF
#define NEGRO 0

const int RST_PIN = 5;            // Pin 9 para el reset del RC522
const int SS_PIN = 21;            // Pin 21 para el SS (SDA) del RC522
MFRC522 mfrc522(SS_PIN, RST_PIN);   // Crear instancia del MFRC522

ClaseMagnetico magn = ClaseMagnetico(2);
ClaseRFID rfid = ClaseRFID(mfrc522);

void setup() {
  Serial.begin(115200);
  M5.begin();
  M5.Lcd.setTextSize(2.5); //tamaño de texto
  pinMode(2, INPUT_PULLUP); //pin dos como pull-up
  SPI.begin();         //Función que inicializa SPI
  mfrc522.PCD_Init();     //Función  que inicializa RFID
}

void loop() {
  //MEDIR LA HORA
  String hora = "Hora: " + String(hour()) + ":" + String(minute()) + " Fecha; " + String(day()) + "/" + String(month()) + "/" + String(year());

  //MEDIR SENSOR MAGNETICO
  String magnetico = magn.sensorPuerta();

  //MEDIR SENSOR RFID
  String ID = rfid.saberID();

  //MOSTRAR POR PANTALLA EN M5STACK
  M5.Lcd.setCursor(0, 10); //posicion del texto
  M5.Lcd.setTextColor(BLANCO); //color del texto
  M5.Lcd.println(hora); //muestra el resultado del sensor
  M5.Lcd.println("Magnetico: " + magnetico); //muestra el resultado del sensor
  M5.Lcd.println("ID: " + ID); //muestra el resultado del sensor
  delay(1000);
  M5.Lcd.fillScreen(NEGRO); //borra la pantalla

  //Envío de datos a la Raspberry
  if (Serial.available() > 0) {
    char command = (char) Serial.read();
    switch (command) {
      case 'M': //Si recibe M se envia el sensor magnetico
        Serial.println(magnetico);
        break;
      case 'H': //Si recibe H se envia la hora
        Serial.println(hora);
        break;
      case 'I': //Si recibe I se envia la ID
        Serial.println(ID);
        break;
        delay(1000);
    }
  }

}
