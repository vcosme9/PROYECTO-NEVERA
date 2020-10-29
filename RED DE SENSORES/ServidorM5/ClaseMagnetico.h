class ClaseMagnetico {
    unsigned int pin;
  public:
    ClaseMagnetico(unsigned int pin_):
      pin(pin_) {}

    String sensorPuerta() {
      if (digitalRead((*this).pin) == HIGH) {

        String abierto = "Puerta abierta";
        return abierto;
      } else {
        String cerrado = "Puerta Cerrada";
        return cerrado;
      }
    }
};
