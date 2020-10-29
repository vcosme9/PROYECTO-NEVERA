class ClaseRFID {
    MFRC522 mfrc522;
    String laID;
    int id;
  public:
    ClaseRFID(MFRC522 mfrc522_):
      mfrc522(mfrc522_) {}

    void printArray(byte *buffer, byte bufferSize) {
      for (byte i = 0; i < bufferSize; i++) {
        //Serial.print(buffer[i] < 0x10 ? " 0" : " ");
        // Serial.print(buffer[i], HEX);
        id = id + buffer[i];
      }
    }

    String saberID() {
      if (mfrc522.PICC_IsNewCardPresent())
      {
        if (mfrc522.PICC_ReadCardSerial())
        {
          id = 0;
          printArray(mfrc522.uid.uidByte, mfrc522.uid.size);
          laID = String(id);
          mfrc522.PICC_HaltA();
          return laID;
        }
      }
    }
};
