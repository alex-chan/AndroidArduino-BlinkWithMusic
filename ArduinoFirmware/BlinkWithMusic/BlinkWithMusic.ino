// The source for the Android application can be found at the following link: https://github.com/Lauszus/ArduinoBlinkLED
// The code for the Android application is heavily based on this guide: http://allaboutee.com/2011/12/31/arduino-adk-board-blink-an-led-with-your-phone-code-and-explanation/ by Miguel

#include <Usb.h>
#include <adk.h>

USB Usb;
ADK adk(&Usb,"mollocer.com", // Manufacturer Name
             "BlinkWithMusic", // Model Name
             "Example sketch for the USB Host Shield", // Description (user-visible string)
             "1.0", // Version
             "https://github.com/alex-chan/AndroidArduino-BlinkWithMusic/", // URL (web page to visit if no installed apps support the accessory)
             "123456789"); // Serial Number (optional)



#define LED_NUM 5

uint8_t LED[] = {3,5,7,9,11};
uint8_t GND[] = {2,4,6,8,10};

void init_led(){
   for(uint8_t i=0;i<LED_NUM;i++){
     pinMode(LED[i],OUTPUT);
     pinMode(GND[i],OUTPUT);
     digitalWrite(LED[i],HIGH);
     digitalWrite(GND[i],LOW);     
   }
}

void blink_led(uint8_t num){
  for(uint8_t i=0;i<LED_NUM;i++){
    delay(10);
    if(i<num) digitalWrite(LED[i], HIGH);
    else digitalWrite(LED[i], LOW);
  }
}

void setup()
{
  Serial.begin(115200);
  init_led();
  Serial.print("\r\nADK demo start");
  if (Usb.Init() == -1) {
    Serial.print("\r\nOSCOKIRQ failed to assert");
    while(1); //halt
  }
  
}

void loop()
{    
  Usb.Task();
  if(adk.isReady()) {
    uint8_t msg[1];
    uint16_t len = sizeof(msg);
    uint8_t rcode = adk.RcvData(&len, msg);
    if(rcode && rcode != hrNAK)
      USBTRACE2("Data rcv. :", rcode);
    if(len > 0) {
      Serial.print(F("\r\nData Packet: "));
      Serial.print(msg[0]);
      blink_led(msg[0]);

    }
  } 
  //else
  //  blink_led(0); 
}
