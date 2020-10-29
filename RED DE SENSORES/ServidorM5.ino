#include <M5Stack.h>
#include <TimeLib.h>

#define BLANCO 0XFFFF
#define NEGRO 0

void setup() {
  Serial.begin(115200);
  M5.begin();
  M5.Lcd.setTextSize(2.5); //tamaño de texto
  pinMode(2, INPUT_PULLUP); //pin dos como pull-up
}

void loop() {
  //RECIBIR SENSOR MAGNETICO
  String magnetico = sensorPuerta();
  //MOSTRAR POR PANTALLA EN M5STACK
  M5.Lcd.setCursor(0, 10); //posicion del texto
  M5.Lcd.setTextColor(BLANCO); //color del texto
  M5.Lcd.println("Magnetico: " + magnetico); //muestra el resultado del sensor
  delay(1000);
  M5.Lcd.fillScreen(NEGRO); //borra la pantalla

  //Envío de datos a la Raspberry
  if (Serial.available() > 0) {
    char command = (char) Serial.read();
    switch (command) {
      case 'M': //Si recibe M sensor magnetico
        Serial.print("Magnetico: ");
        Serial.println(magnetico);
        break;
      case 'H': //Ejemplo para futuras funciones
        Serial.print("Hora: "+hour()+":"+minute()+" Fecha; "+day()+"/"+month+"/"+year());
        delay(1000);
    }
  }

}
