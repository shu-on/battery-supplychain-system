## Prototype application of EV battery supply chain system

This is a prototype application development of my proposed EV battery supply chain system.

You can check the following link why I created this system.

[The purpose of this system](./readme/Purpose.md)

### Overview

This diagram shows an overview of this prototype system.

![prototype-overview](./readme/images/prototype-overview.png)

This is the system flow at this time.

1. Using the Arduino as a battery management system, the temperature sensor values and pre-set battery information are combined and written to the NFC card.

2. Bring the smart phone and the card close together and press the read button to read the information written on the NFC card.

3. The information read is added to the blockchain.

Each of the following overviews describes this prototype system in more detail.

[Android application overview](./readme/AndroidApplicationOverview.md)

[Arduino overview](./readme/ArduinoOverview.md)

