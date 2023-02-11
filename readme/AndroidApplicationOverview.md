## Android application overview

This section describes the application under development.

### Function

- [Login and Register](#login)
- [Reading from NFC cards](#read-fragment)
- [Adding to the blockchain](#adding-to-the-blockchain)

---

### Login

When the application is launched, it starts with a login screen.

![login](./images/login.png)

If you have previously registered on [the Register page](#register), you will be successfully logged in and taken to [the Read Fragment](#read-fragment).

At that time, if the NFC function of the device is off, the following warning will appear.

![nfc-warning](./images/nfc-warning.png)

- Exit : Exit this application.
- Used Only : The read function is not used and only browses the application.
- Go to setting : Moves to the settings screen.

### Register

If you have not registered, you can do so on this page.

![register](./images/register.png)

- USER,SECOND,RECYCLE,MAKER : Select one field name for the organization.
- Your ID : Enter any user ID number. (a number of up to 18 digits)
- Owner Name : Enter any user name. (up to 11 alphanumeric characters)
- Password : Please enter any password. (up to 30 alphanumeric characters)

### Server

This login system uses Microsoft SQL Server.

### Read Fragment

Check the notes in this fragment. When ready, press the Read button.

![read-fragment](./images/read-fragment.png)

You will then be taken to a screen with a cute dog moving.

### Reading from NFC cards

Bring the NFC card and device closer together until they are recognized.

Press the Read start button and wait until the next screen appears.

![read-nfc](./images/read-nfc.png)

### Adding to the blockchain

Once the read information is confirmed, press the Add button at the bottom.

![add-blockchain](./images/add-blockchain.png)

