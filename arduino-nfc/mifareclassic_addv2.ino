/**************************************************************************/
/*!
    Updates a sector that is already formatted for NDEF (using
    mifareclassic_formatndef.pde for example), inserting a new url

    To enable debug message, define DEBUG in PN532/PN532_debug.h
*/
/**************************************************************************/

#include <SPI.h>
#include <PN532_SPI.h>
#include "PN532.h"
#include "DHT.h" //ライブラリインクルード

#define DHT_Pin 7 //DHT11のDATAピンを定義
#define DHT_Type DHT11 //センサの型番定義

DHT dht(DHT_Pin, DHT_Type); //センサ初期化

PN532_SPI pn532spi(SPI, 10);
PN532 nfc(pn532spi);

void setup(void) {
  Serial.begin(115200);
  Serial.println("Looking for PN532...");
  dht.begin(); //温湿度センサー開始
  nfc.begin();
  uint32_t versiondata = nfc.getFirmwareVersion();
  if (! versiondata) {
    Serial.print("Didn't find PN53x board");
    while (1); // halt
  }
  // Got ok data, print it out!
  Serial.print("Found chip PN5"); Serial.println((versiondata>>24) & 0xFF, HEX); 
  Serial.print("Firmware ver. "); Serial.print((versiondata>>16) & 0xFF, DEC); 
  Serial.print('.'); Serial.println((versiondata>>8) & 0xFF, DEC);
  // configure board to read RFID tags
  nfc.SAMConfig();
}

void loop(void) {
  //for BCApp DataEx
  const char * url_1 = "2023-02-03-----hanako";
  const char * url_2 = "----------UsedCara000002-x0000001";
  char url_3[40];
  char u1[] = "hx00001";
  int tempC = dht.readTemperature(); //温度の読み出し 摂氏
  char u3[] = "85--90--70040";
  sprintf(url_3, "%s%d%s", u1, tempC, u3);
  Serial.println(url_3);
  Serial.println(tempC);
  uint8_t ndefprefix = NDEF_URIPREFIX_NONE;
  uint8_t success;                          // Flag to check if there was an error with the PN532
  uint8_t uid[] = { 0, 0, 0, 0, 0, 0, 0 };  // Buffer to store the returned UID
  uint8_t uidLength;                        // Length of the UID (4 or 7 bytes depending on ISO14443A card type)
  bool authenticated = false;               // Flag to indicate if the sector is authenticated
  uint8_t success1;
  uint8_t success2;
  uint8_t success3;
  // Use the default NDEF keys (these would have have set by mifareclassic_formatndef.pde!)
  uint8_t keya[6] = { 0xA0, 0xA1, 0xA2, 0xA3, 0xA4, 0xA5 };
  uint8_t keyb[6] = { 0xD3, 0xF7, 0xD3, 0xF7, 0xD3, 0xF7 };
  uint8_t keya2[6] = { 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF };
  uint8_t keyb2[6] = { 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF };

  delay(3000); //3秒待つ　データの読み出し周期1秒以上必要。
  Serial.println("Place your NDEF formatted Mifare Classic card on the reader to update the");
  Serial.println("NDEF record and press any key to continue ...");
  // Wait for user input before proceeding
  while (!Serial.available());
  // a key was pressed1
  while (Serial.available()) Serial.read();
  // Wait for an ISO14443A type card (Mifare, etc.).  When one is found
  // 'uid' will be populated with the UID, and uidLength will indicate
  // if the uid is 4 bytes (Mifare Classic) or 7 bytes (Mifare Ultralight)
  success = nfc.readPassiveTargetID(PN532_MIFARE_ISO14443A, uid, &uidLength);
  if (success){
    // Display some basic information about the card
    Serial.println("Found an ISO14443A card");
    Serial.print("  UID Length: ");Serial.print(uidLength, DEC);Serial.println(" bytes");
    Serial.print("  UID Value: ");
    nfc.PrintHex(uid, uidLength);
    Serial.println("");
    // Make sure this is a Mifare Classic card
    if (uidLength != 4){
      Serial.println("Ooops ... this doesn't seem to be a Mifare Classic card!"); 
      return;
    }
    // We probably have a Mifare Classic card ... 
    Serial.println("Seems to be a Mifare Classic card (4 byte UID)");
    // Check if this is an NDEF card (using first block of sector 1 from mifareclassic_formatndef.pde)
    // Must authenticate on the first key using 0xD3 0xF7 0xD3 0xF7 0xD3 0xF7
    success = nfc.mifareclassic_AuthenticateBlock (uid, uidLength, 5, 1, keyb2);
    if (!success){
      Serial.println("Unable to authenticate block 5 ... is this card NDEF formatted?");
      return;
    }
    Serial.println("Authentication succeeded (seems to be an NDEF/NFC Forum tag) ...");
    // Authenticated seems to have worked. Try to write an NDEF record to sector 1
    // Use 0x01 for the URI Identifier Code to prepend "http://www."
    // to the url (and save some space).  For information on URI ID Codes
    // see http://www.ladyada.net/wiki/private/articlestaging/nfc/ndef
    if (strlen(url_1) > 38){
      // The length is also checked in the WriteNDEFURI function, but lets
      // warn users here just in case they change the value and it's bigger
      // than it should be
      Serial.println("URI is too long ... must be less than 38 characters!");
      return;
    }
    Serial.println("Updating sector 1 with URI as NDEF Message");
    // URI is within size limits ... write it to the card and report success/failure
    success1 = nfc.mifareclassic_WriteNDEFURI(1, ndefprefix, url_1);
    if (success1){
      Serial.println("NDEF URI Record written to sector 1");
      Serial.println("");      
    }else{
      Serial.println("NDEF Record creation failed! :(");
    }
    success = nfc.mifareclassic_AuthenticateBlock (uid, uidLength, 8, 1, keyb2);
    if (!success){
      Serial.println("Unable to authenticate block 8 ... is this card NDEF formatted?");
      return;
    }
    Serial.println("Authentication succeeded (seems to be an NDEF/NFC Forum tag) ...");
    if (strlen(url_2) > 38){//|| strlen(url_2) > 38
      Serial.println("URI is too long ... must be less than 38 characters!");
      return;
    }
    Serial.println("Updating sector 2 with URI as NDEF Message");
    success2 = nfc.mifareclassic_WriteNDEFURI(2, ndefprefix, url_2);
    if (success2){
      Serial.println("NDEF URI Record written to sector  2");
      Serial.println("\n\nDone!");      
    }else{
      Serial.println("NDEF Record creation failed! :(");
    }
    success = nfc.mifareclassic_AuthenticateBlock (uid, uidLength, 12, 1, keyb2);
    if (!success){
      Serial.println("Unable to authenticate block 12 ... is this card NDEF formatted?");
      return;
    }
    Serial.println("Authentication succeeded (seems to be an NDEF/NFC Forum tag) ...");
    if (strlen(url_3) > 38){//|| strlen(url_2) > 38
      Serial.println("URI is too long ... must be less than 38 characters!");
      return;
    }
    Serial.println("Updating sector 2 with URI as NDEF Message");
    success3 = nfc.mifareclassic_WriteNDEFURI(3, ndefprefix, url_3);
    if (success3){
      Serial.println("NDEF URI Record written to sector  3");
      Serial.println("\n\nDone!");      
    }else{
      Serial.println("NDEF Record creation failed! :(");
    }
  }
  // Wait a bit before trying again
  delay(1000);
  Serial.flush();
  while(Serial.available()) Serial.read();
}
