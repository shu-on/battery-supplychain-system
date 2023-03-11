## Android application overview

This section describes the application under development.

### Function

- [Login and Register](#login)
- [Reading from NFC cards](#read-fragment)
- [Adding to the blockchain](#adding-to-the-blockchain)

### Future Development

- [Marketplace](#marketplace)
- [Browse past battery information](#browse-past-battery-information)

---

### Login

When the application is launched, it starts with a login screen.

<img src="./images/login.jpg" width="220px">

If you have previously registered on [the Register page](#register), you will be successfully logged in and taken to Home fragment.

If you enter incorrect information, a toast message will appear prompting you to enter it again.

At that time, if the NFC function of the device is off, the following warning will appear.

<img src="./images/nfc-warning.jpg" width="220px">

- USEONLY : The read function is not used and only browses the application.
- EXIT : Exit this application.
- GOSETTING : Go to the device's NFC settings screen.

### Register

If you have not registered, you can do so on this page.

<img src="./images/register.jpg" width="220px">

- USER,SECOND,RECYCLE,MAKER : Select one field name for the organization.
- Your ID : Enter any user ID number. (a number of up to 18 digits)
- Owner Name : Enter any user name. (up to 11 alphanumeric characters)
- Password : Please enter any password. (up to 30 alphanumeric characters)

### Server

This login system uses Microsoft SQL Server.

### Read Fragment

Check the notes in this fragment. When ready, press the START READ button.

<img src="./images/read-fragment.jpg" width="220px">

You will then be taken to a screen with a cute dog moving.

### Reading from NFC cards

Bring the NFC card and device closer together until they are recognized.

Press the READ TAG button and wait until the next screen appears.

<img src="./images/read-nfc.jpg" width="220px">

### Adding to the blockchain

Once the read information is confirmed, press the ADD BLOCKCHAIN button at the bottom.

<img src="./images/add-blockchain.png" width="220px">

### Marketplace

<img src="./images/marketplace.png" width="220px">

### Browse past battery information

<img src="./images/browse-battery.png" width="220px">

---

### Reference

This prototype system was developed from the following sources.

Communication between Android smartphone and NFC card : [This Source code](https://github.com/ikarus23/MifareClassicTool)

Login system using Microsoft SQL Server : [This video](https://www.youtube.com/watch?v=MnmEXqfV5BU&t=500s)
