unsigned long previousMillis = 0;
unsigned long currentMillis = 0;
int temp_read_Delay = 100;

//PID variables
float elapsedTime, Time, timePrev;
float errorR = 0;
float errorPR = 0;
float IntegralToplamR = 0;
float DerivR = 0;
float ProportionalPartR = 0;
float IntegralPartR = 0;
float DerivativePartR = 0;
float OutR1 = 0;
int OutR = 0;
float KP = 2300;
float KI = 450;
float KD = 250;

float real_temperature = 0;

//Sıcaklığı string değil de float olarak sera yazılımına göndermek için gerekli.
byte* bytesOfRealTemperature = (byte*)&real_temperature;
//Seri porttan okunan string buna atılıyor.
String readInput;

int pwm = 3;
float sys_out = A0;

float setpoint = 26;

void setup() {
  Serial.begin(9600);
  pinMode(sys_out, INPUT);
  pinMode(pwm, OUTPUT);
  attachInterrupt(0, zero_crossing, RISING);

}

void loop() {

  currentMillis = millis();
  if (currentMillis - previousMillis >= temp_read_Delay) {
    previousMillis += temp_read_Delay;              //Increase the previous time for next loop
    float sys_out = analogRead(A0);  //get the real temperature
    real_temperature = 25 + sys_out / 29.42;
    errorR = setpoint - real_temperature;    //Calculate the pid ERROR
    timePrev = Time;                    // the previous time is stored before the actual time read
    Time = millis();                    // actual time read
    elapsedTime = (Time - timePrev) / 1000;

    IntegralToplamR = IntegralToplamR + elapsedTime * (errorR + errorPR) / 2.00; //Bilinear Transform aka yamukta alan
    if (IntegralToplamR >= 10000.0) {
      IntegralToplamR = 10000.0;
    }
    if (IntegralToplamR <= -10000.0) {
      IntegralToplamR = -10000.0;
    }

    DerivR = (errorR - errorPR) / elapsedTime;

    ProportionalPartR = KP * errorR;

    IntegralPartR = KI * IntegralToplamR;

    DerivativePartR = KD * DerivR;

    OutR1 = ProportionalPartR + IntegralPartR + DerivativePartR;

    if (OutR1 >= 7500) {
      OutR = 7500;
    }
    else if (OutR1 <= 0) {
      OutR = 1000;
    }
    else {
      OutR = OutR1;
    }

    errorPR = errorR;

    while(Serial.available()) {
      delay(10);
      readInput = Serial.readString();
    }

    if (readInput.length() >= 5) {
      setpoint = str2float(readInput);
    }
    
    Serial.write(bytesOfRealTemperature, 4);
    //Serial.println(real_temperature);
    delay(100);
  }
}

void zero_crossing() {
  delayMicroseconds(9000 - OutR); //This delay controls the power
  digitalWrite(3, HIGH);
  delayMicroseconds(100);
  digitalWrite(3, LOW);
}

//Verilen stringi floata çeviriyor.
//Ör: "25,72" 'yi 25.72 yapıyor.
float str2float(String s) {
  int i;
  int j = 0;
  int len = s.length();
  int flag = 0;
  
  float value = 0;
  float afterComma = 0;

  for(i = 0; i < len; i++) {
    if (flag == 0 && s.charAt(i) != ',') {
      value *= 10;
      value += s.charAt(i) - '0';
    } else if (s.charAt(i) == ',') {
      flag = 1;
    } else {
      afterComma += s.charAt(len - j - 1) - '0';
      afterComma /= 10;
      j++;
    }
  }

  return value + afterComma;
}
