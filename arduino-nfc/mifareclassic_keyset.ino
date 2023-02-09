#include <emulatetag.h>
#include <llcp.h>
#include <mac_link.h>
#include <PN532.h>
#include <PN532Interface.h>
#include <PN532_debug.h>
#include <snep.h>

/**************************************************************************/
/*!
    This example attempts to format a clean Mifare Classic 1K card as
    an NFC Forum tag (to store NDEF messages that can be read by any
    NFC enabled Android phone, etc.)

    Note that you need the baud rate to be 115200 because we need to print
    out the data and read from the card at the same time!

    To enable debug message, define DEBUG in PN532/PN532_debug.h
*/
/**************************************************************************/

//#if 0
//  #include <SPI.h>
//  #include <PN532_SPI.h>
//  #include "PN532.h"
//
//  PN532_SPI pn532spi(SPI, 10);
//  PN532 nfc(pn532spi);
//#elif 1
//  #include <PN532_HSU.h>
//  #include <PN532.h>
//      
//  PN532_HSU pn532hsu(Serial1);
//  PN532 nfc(pn532hsu);
//#else 
//  #include <Wire.h>
//  #include <PN532_I2C.h>
//  #include <PN532.h>
//#endif

#include <SPI.h>
#include <PN532_SPI.h>
#include "PN532.h"
PN532_SPI pn532spi(SPI, 10);
PN532 nfc(pn532spi);

void setup(void) {
  Serial.begin(115200);
  Serial.println("Looking for PN532...");
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
  uint8_t success;                          // Flag to check if there was an error with the PN532
  uint8_t uid[] = { 0, 0, 0, 0, 0, 0, 0 };  // Buffer to store the returned UID
  uint8_t uidLength;                        // Length of the UID (4 or 7 bytes depending on ISO14443A card type)
  bool authenticated = false;               // Flag to indicate if the sector is authenticated
  uint8_t keya[6] = { 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF }; // Use the default key
  uint8_t keya2[6] = { 0xA0, 0xA1, 0xA2, 0xA3, 0xA4, 0xA5 };
 /*
  * NOTE: Formatting your card for NDEF records will change the authentication keys.
  * To reformat your NDEF tag as a clean Mifare Classic tag, use the mifareclassic_ndeftoclassic example!
  */
  Serial.println("Place your Mifare Classic card on the reader to format with NDEF");
  Serial.println("and press any key to continue ...");
  // Wait for user input before proceeding
  while (!Serial.available());
  // a key was pressed1
  while (Serial.available()) Serial.read();
  // Wait for an ISO14443A type card (Mifare, etc.).  When one is found 'uid' will be populated with the UID, 
  // and uidLength will indicate if the uid is 4 bytes (Mifare Classic) or 7 bytes (Mifare Ultralight)
  success = nfc.readPassiveTargetID(PN532_MIFARE_ISO14443A, uid, &uidLength);
  if (success){
      // Display some basic information about the card
      Serial.println("Found an ISO14443A card");
      Serial.print("  UID Length: ");Serial.print(uidLength, DEC);Serial.println(" bytes");
      Serial.print("  UID Value: ");
      nfc.PrintHex(uid, uidLength);
      for (uint8_t i = 0; i < uidLength; i++){
        Serial.print(uid[i], HEX);
        Serial.print(' ');
      }
      Serial.println("");
      // Make sure this is a Mifare Classic card
      if (uidLength != 4){
        Serial.println("this doesn't seem to be a Mifare Classic card!");
        return;
      }
      // We probably have a Mifare Classic card ...
      Serial.println("Seems to be a Mifare Classic card (4 byte UID)");
      // Try to format the card for NDEF data
      success = nfc.mifareclassic_AuthenticateBlock (uid, uidLength, 0, 0, keya2);
      if (!success){
        Serial.println("Unable to authenticate block 0 to enable card formatting!");
        return;
      }
//      uint8_t sectorbuffer1[16] = {0xA0, 0xA1, 0xA2, 0xA3, 0xA4, 0xA5, 0xFF, 0x07, 0x80, 0x69, 0xD3, 0xF7, 0xD3, 0xF7, 0xD3, 0xF7};
//      uint8_t sectorbuffer2[16] = {0xA0, 0xA1, 0xA2, 0xA3, 0xA4, 0xA5, 0xFF, 0x07, 0x80, 0x69, 0xD3, 0xF7, 0xD3, 0xF7, 0xD3, 0xF7};
//      uint8_t sectorbuffer3[16] = {0xA0, 0xA1, 0xA2, 0xA3, 0xA4, 0xA5, 0xFF, 0x07, 0x80, 0x69, 0xD3, 0xF7, 0xD3, 0xF7, 0xD3, 0xF7};
      uint8_t sectorbuffer1[16] = {0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x07, 0x80, 0x69, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF};
//      uint8_t sectorbuffer2[16] = {0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x07, 0x80, 0x69, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF};
//      uint8_t sectorbuffer3[16] = {0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x07, 0x80, 0x69, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF};
      
      // Note 0xA0 0xA1 0xA2 0xA3 0xA4 0xA5 must be used for key A
      // for the MAD sector in NDEF records (sector 0)
      // Write block 1 and 2 to the card
      if(!(nfc.mifareclassic_WriteDataBlock(11,sectorbuffer1))){
       success = false; 
       Serial.println("uneble sector7?");
          return;
      }
//      else if(!(nfc.mifareclassic_WriteDataBlock(7,sectorbuffer2))){
//        success = false;
//        Serial.println("uneble sector1?");
//          return;
//      }else if(!(nfc.mifareclassic_WriteDataBlock(11,sectorbuffer3))){// Write key A and access rights card
//        success = false;
//        Serial.println("uneble sector2?");
//          return;
//      }
      else{
        success = true;// Seems that everything was OK
        Serial.println("ok");
      }
      if (!success){
        Serial.println("Unable to format the card for NDEF");
        return;
      }
  }
  // Wait a bit before trying again
  Serial.println("\n\nDone!");
  delay(1000);
  Serial.flush();
  while(Serial.available()) Serial.read();
}
